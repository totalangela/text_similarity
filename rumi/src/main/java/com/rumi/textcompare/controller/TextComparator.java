package com.rumi.textcompare.controller;

import com.rumi.textcompare.model.TextSimilarity;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class TextComparator {

    private static final String SEARCH_API_KEY_FILE = "brave_search_api_key";

    private static final String CONFIG_FILE = "/application.properties";

    private final Logger logger = Log.getLogger(getClass().getName());

    private final WebSearcher webSearcher;

    private final JsonParser jsonParser;

    private byte offset;

    public TextComparator() {
        String apiKey = null;
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(SEARCH_API_KEY_FILE);
            Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name());
            scanner.useDelimiter("\\A"); // "\\A" is the beginning of the input
            if (scanner.hasNext()) {
                apiKey = scanner.next();
            }
        } catch (Exception e) {
            logger.warn("Failed to get Brave search engine api key: " + e.getMessage());
        }

        if (apiKey == null) {
            logger.warn("Failed to get Brave search engine api key");
            System.exit(1);
        }
        this.webSearcher = new WebSearcher(apiKey);
        this.jsonParser = new JsonParser();

        Properties properties = new Properties();
        try (InputStream input = getClass().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                logger.warn("Failed to read from " + CONFIG_FILE);
            } else {
                properties.load(input);
                String offsetStr = properties.getProperty("bravesearch.offset");
                offset = Byte.parseByte(offsetStr);
            }
        } catch (Exception e) {
            logger.warn("Failed to read from " + CONFIG_FILE + ": " + e.getMessage());
        }
    }

    public List<TextSimilarity> compare(String givenText) throws Exception {
        List<TextSimilarity> result = new ArrayList<TextSimilarity>();
        for (int i = 0; i <= offset; i++) {
            result.addAll(this.getTextSimilarities(givenText, i));
            Thread.sleep(500);
        }
        result.sort(Collections.reverseOrder(Comparator.comparingDouble(TextSimilarity::getSimilarityScore)));
        return result;
    }

    public List<TextSimilarity> getTextSimilarities(String givenText, int offset) {
        List<TextSimilarity> result = new ArrayList<TextSimilarity>();
        String searchResult = this.webSearcher.getSearchResult(givenText, offset);
        if (searchResult != null) {
            Map<String, String> dict = this.jsonParser.createUrlAndDescriptionMapping(searchResult);
            for (String webUrl : dict.keySet()) {
                try {
                    String webContent = this.webSearcher.getWebContentInPlainText(webUrl);
                    TextSimilarity textSimilarity = this.getTextSimilarity(givenText, webContent, webUrl);
                    result.add(textSimilarity);
                } catch (Exception e) {
                    logger.warn("Failed to get web content from " + webUrl + ": " + e.getMessage());
                }
            }
        }
        return result;
    }

    public TextSimilarity getTextSimilarity(String givenText, String webContent, String webUrl) {
        Set<String> givenTextTokens = preprocessText(givenText);
        Set<String> webContentTokens = preprocessText(webContent);

        double[] vector1 = calculateTFIDFVector(givenTextTokens, givenTextTokens);
        double[] vector2 = calculateTFIDFVector(webContentTokens, givenTextTokens);

        double similarity = calculateCosineSimilarity(vector1, vector2);
        logger.debug(String.format("Url: %s, similarity score: %f", webUrl, similarity));

        List<String> list = new ArrayList<String>();
        Set<String> set = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        set.addAll(Arrays.asList(webContent.split("\\s+")));
        for (String word : givenText.split("\\s+")) {
            if (set.contains(word)) {
                list.add("<strong style=\"color:red\">" + word + "</strong>");
            } else {
                list.add(word);
            }
        }
        return new TextSimilarity(webUrl, String.join(" ", list), similarity, this.formatDouble(similarity));
    }

    private Set<String> preprocessText(String text) {
        return Arrays.stream(text.split("\\s+")).collect(Collectors.toSet());
    }

    private double[] calculateTFIDFVector(Set<String> tokens, Set<String> allTokens) {
        double[] tfidfVector = new double[allTokens.size()];
        List<String> allTokensList = List.copyOf(allTokens);

        for (String token : tokens) {
            int index = allTokensList.indexOf(token);
            if (index != -1) {
                tfidfVector[index] += 1.0;
            }
        }

        return tfidfVector;
    }

    private double calculateCosineSimilarity(double[] vector1, double[] vector2) {
        // Calculate cosine similarity using Apache Commons Math library
        RealVector v1 = new ArrayRealVector(vector1);
        RealVector v2 = new ArrayRealVector(vector2);

        double dotProduct = v1.dotProduct(v2);
        double magnitudeV1 = v1.getNorm();
        double magnitudeV2 = v2.getNorm();

        // Check for zero magnitude (to avoid division by zero)
        if (magnitudeV1 == 0 || magnitudeV2 == 0) {
            throw new IllegalArgumentException("Failed to calculate cosine similarity because magnitude of one or both vectors is zero");
        }

        return dotProduct / (magnitudeV1 * magnitudeV2);
    }

    private String formatDouble(double number) {
        return String.format("%.2f", number);
    }
}
