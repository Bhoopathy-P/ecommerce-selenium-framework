package com.ecommerce.automation.dataproviders;

import com.ecommerce.automation.utils.JsonDataReader;
import com.fasterxml.jackson.databind.JsonNode;
import org.testng.annotations.DataProvider;

/**
 * Centralized TestNG {@code @DataProvider} definitions used across the Login
 * and Payment test classes. Reading data from JSON keeps test data separate
 * from test logic, so new scenarios can be added without touching Java code.
 */
public final class DataProviders {

    private static final String LOGIN_DATA_FILE = "src/test/resources/testdata/loginData.json";
    private static final String PAYMENT_DATA_FILE = "src/test/resources/testdata/paymentData.json";

    private DataProviders() {
        // utility class - no instantiation
    }

    /**
     * Supplies {email, password, scenarioDescription} rows for negative login tests.
     */
    @DataProvider(name = "invalidLoginData")
    public static Object[][] invalidLoginData() {
        JsonNode rows = JsonDataReader.readNode(LOGIN_DATA_FILE, "invalidCredentials");
        Object[][] data = new Object[rows.size()][3];
        for (int i = 0; i < rows.size(); i++) {
            JsonNode row = rows.get(i);
            data[i][0] = row.get("email").asText();
            data[i][1] = row.get("password").asText();
            data[i][2] = row.get("scenario").asText();
        }
        return data;
    }

    /**
     * Supplies {nameOnCard, cardNumber, cvc, expiryMonth, expiryYear, scenarioDescription}
     * rows for negative payment field-validation tests.
     */
    @DataProvider(name = "invalidCardData")
    public static Object[][] invalidCardData() {
        JsonNode rows = JsonDataReader.readNode(PAYMENT_DATA_FILE, "invalidCardDetails");
        Object[][] data = new Object[rows.size()][6];
        for (int i = 0; i < rows.size(); i++) {
            JsonNode row = rows.get(i);
            data[i][0] = row.get("nameOnCard").asText();
            data[i][1] = row.get("cardNumber").asText();
            data[i][2] = row.get("cvc").asText();
            data[i][3] = row.get("expiryMonth").asText();
            data[i][4] = row.get("expiryYear").asText();
            data[i][5] = row.get("scenario").asText();
        }
        return data;
    }
}
