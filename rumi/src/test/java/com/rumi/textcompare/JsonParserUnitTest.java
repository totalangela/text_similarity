package com.rumi.textcompare;

import com.rumi.textcompare.controller.JsonParser;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;

import static org.junit.Assert.*;

public class JsonParserUnitTest {

    private JsonParser jsonParser;

    @Before
    public void init() {
        this.jsonParser = new JsonParser();
    }

    @Test
    public void testCreateUrlAndDescriptionMapping() throws Exception {
        String fileName = "test_search_result.json";
        String url = "https://en.wikipedia.org/wiki/Rumi";
        String description = "Jalāl al-Dīn Muḥammad Rūmī (Persian: جلال‌الدین مُحمّد رُومی), or simply Rumi (30 September 1207 – 17 December 1273), was a 13th-century poet, Hanafi faqih, Islamic scholar, Maturidi theologian and Sufi mystic originally from Greater Khorasan in Greater Iran.";
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
             Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
            scanner.useDelimiter("\\A"); // "\\A" is the beginning of the input
            assertTrue(scanner.hasNext());
            String jsonStr = scanner.next();
            Map<String, String> mapping = this.jsonParser.createUrlAndDescriptionMapping(jsonStr);
            assertEquals(20, mapping.size());
            assertTrue(mapping.containsKey(url));
            assertEquals(description, mapping.get(url));
        }
    }
}
