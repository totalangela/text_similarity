package com.rumi.textcompare.controller;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class WebSearcher {

    private final Logger logger = Log.getLogger(getClass().getName());

    private final HttpClient httpClient;

    private final String apiKey;

    public WebSearcher(String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newHttpClient();
    }

    public String getSearchResult(String givenText, int offset) {
        String resultFilter = "&result_filter=web";
        String offsetStr = "&offset=" + offset;
        String uriStr = String.format(
                "https://api.search.brave.com/res/v1/web/search?q=%s%s%s",
                URLEncoder.encode(givenText, StandardCharsets.UTF_8),
                resultFilter,
                offsetStr);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uriStr))
                .header("Accept", "application/json")
                .header("X-Subscription-Token", this.apiKey)
                .build();
        CompletableFuture<HttpResponse<String>> responseFuture = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        try {
            HttpResponse<String> response = responseFuture.get();
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                logger.warn("Failed to get search result from Brave Search Engine API, status code: " + response.statusCode());
            }
        } catch (Exception e) {
            logger.warn("Failed to get search result from Brave Search Engine API: " + e.getMessage());
        }
        return null;
    }

    public String getWebContentInPlainText(String url) throws IOException {
        Document document = Jsoup.connect(url).get();
        String plainTextContent = document.text();
        return plainTextContent;
    }
}
