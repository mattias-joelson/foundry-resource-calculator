package org.joelson.mattias.foundry.resource_calculator.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class JsonReader {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, JsonItem> readJsonItems(Path itemsPath) throws IOException {
        return readJsonObjects(itemsPath, JsonItem.class);
    }

    public Map<String, JsonMaker> readJsonMakers(Path makersPath) throws IOException {
        return readJsonObjects(makersPath, JsonMaker.class);
    }

    public Map<String, JsonRecipe> readJsonRecipes(Path recipesPath) throws IOException {
        return readJsonObjects(recipesPath, JsonRecipe.class);
    }

    private <T extends JsonNamedObject> Map<String, T> readJsonObjects(Path path, Class<T> type) throws IOException {
        T[] objs = (T[]) readValue(Files.readString(path), type.arrayType());
        Map<String, T> objectMap = new HashMap<>(objs.length);
        for (T t : objs) {
            objectMap.put(t.name(), t);
        }
        return objectMap;
    }

    private <T> T readValue(String content, Class<T> valueType) throws JsonProcessingException {
        return objectMapper.readValue(content, valueType);
    }
}
