package com.rumi.textcompare.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import java.util.*;

public class JsonParser {

    private final Logger logger = Log.getLogger(getClass().getName());

    public Map<String, String> createUrlAndDescriptionMapping(String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, Object> jsonMap = mapper.readValue(jsonString, Map.class);
            Map<String, String> dict = new HashMap<String, String>();
            createUrlAndDescriptionMappingHelper(jsonMap, 0, dict);
            return dict;
        } catch (Exception e) {
            logger.warn("Failed to convert json string to key value mappings: " + e.getMessage());
            return new HashMap<>();
        }
    }

    private void createUrlAndDescriptionMappingHelper(Map<String, Object> map, int depth, Map<String, String> dict) {
        String url = null;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (key.equalsIgnoreCase("url")) {
                url = value.toString();
            }
            if (key.equalsIgnoreCase("description") && depth == 2) {
                dict.put(url, value.toString().replace("<strong>", "").replace("</strong>", ""));
            }
            if (value instanceof List) {
                List objects = (List)value;
                for (Object obj : objects) {
                    if (obj instanceof Map) {
                        createUrlAndDescriptionMappingHelper((Map<String, Object>)obj, depth + 1, dict);
                    }
                }
            } else if (value instanceof Map) {
                createUrlAndDescriptionMappingHelper((Map<String, Object>)value, depth + 1, dict);
            }
        }
    }
}
