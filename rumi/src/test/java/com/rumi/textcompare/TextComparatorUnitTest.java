package com.rumi.textcompare;

import com.rumi.textcompare.controller.TextComparator;
import com.rumi.textcompare.model.TextSimilarity;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.Assert.*;

public class TextComparatorUnitTest {

    private TextComparator textComparator;

    @Before
    public void init() {
        this.textComparator = new TextComparator();
    }

    @Test
    public void testGetTextSimilarity() throws Exception {
        String fileName = "rumi_wiki.txt";
        String webUrl = "https://en.wikipedia.org/wiki/Rumi";
        String givenText = "Like other mystic and Sufi poets of Persian literature, Rumi's poetry speaks of love which infuses the world. Rumi's teachings also express the tenets summarized in the Quranic verse which Shams-e Tabrizi cited as the essence of prophetic guidance: \"Know that ‘There is no god but He,’ and ask forgiveness for your sin\" (Q. 47:19).";
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
             Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
            scanner.useDelimiter("\\A"); // "\\A" is the beginning of the input
            assertTrue(scanner.hasNext());
            String webContent = scanner.next();
            TextSimilarity textSimilarity = this.textComparator.getTextSimilarity(givenText, webContent, webUrl);
            assertEquals(1.0, textSimilarity.getSimilarityScore(), 1.23e-7f);
            assertEquals(111, textSimilarity.getText().split("strong").length);
        }
    }
}
