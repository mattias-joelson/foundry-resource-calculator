package org.joelson.mattias.foundry.resource_calculator.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class JsonReader {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private JsonReader() throws InstantiationException {
        throw new InstantiationException("Should not be instantiated.");
    }

    public static JsonCalculatorConfig readJsonCalculatorConfig(Path calculatorConfigPath) throws IOException {
        return readValue(Files.readString(calculatorConfigPath), JsonCalculatorConfig.class);
    }

    public static JsonCalculatorGoals readJsonCalculatorGoals(Path calculatorGoalsPath) throws IOException {
        return readValue(Files.readString(calculatorGoalsPath), JsonCalculatorGoals.class);
    }

    private static <T> T readValue(String content, Class<T> valueType) throws JsonProcessingException {
        return objectMapper.readValue(content, valueType);
    }
}
