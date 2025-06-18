package org.joelson.mattias.foundry.resource_calculator.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonReader {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonCalculatorConfig readJsonCalculatorConfig(Path calculatorConfigPath) throws IOException {
        return readValue(Files.readString(calculatorConfigPath), JsonCalculatorConfig.class);
    }

    private <T> T readValue(String content, Class<T> valueType) throws JsonProcessingException {
        return objectMapper.readValue(content, valueType);
    }
}
