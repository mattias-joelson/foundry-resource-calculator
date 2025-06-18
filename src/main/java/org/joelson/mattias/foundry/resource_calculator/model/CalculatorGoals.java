package org.joelson.mattias.foundry.resource_calculator.model;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class CalculatorGoals {

    private final Map<Item, Integer> productionGoals;

    public CalculatorGoals(Map<Item, Integer> productionGoals) {
        this.productionGoals = Objects.requireNonNull(productionGoals);
    }

    public static CalculatorGoals fromPath(Path goalsPath, CalculatorConfig calculatorConfig) throws IOException {
        JsonCalculatorGoals jsonCalculatorGoals = JsonReader.readJsonCalculatorGoals(goalsPath);
        Map<Item, Integer> productionGoals = jsonCalculatorGoals.productionGoals().entrySet().stream().collect(
                Collectors.toMap(entry -> calculatorConfig.getItem(entry.getKey()), Map.Entry::getValue));
        return new CalculatorGoals(productionGoals);
    }
}
