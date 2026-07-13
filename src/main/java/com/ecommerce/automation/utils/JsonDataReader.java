package com.ecommerce.automation.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

/**
 * Generic reader for JSON test-data files stored under src/test/resources/testdata.
 * Used to feed TestNG {@code @DataProvider} methods so test data stays out of
 * the test code itself.
 */
public final class JsonDataReader {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonDataReader() {
        // utility class - no instantiation
    }

    /**
     * Reads a JSON file and returns the node located at the given top-level key.
     *
     * @param filePath path to the JSON file, e.g. "src/test/resources/testdata/loginData.json"
     * @param nodeName the top-level property to extract, e.g. "invalidCredentials"
     */
    public static JsonNode readNode(String filePath, String nodeName) {
        try {
            JsonNode root = MAPPER.readTree(new File(filePath));
            JsonNode node = root.get(nodeName);
            if (node == null) {
                throw new RuntimeException("Node '" + nodeName + "' not found in " + filePath);
            }
            return node;
        } catch (IOException e) {
            throw new RuntimeException("Unable to read JSON test data from " + filePath, e);
        }
    }
}
