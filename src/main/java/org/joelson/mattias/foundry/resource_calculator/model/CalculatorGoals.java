package org.joelson.mattias.foundry.resource_calculator.model;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public record CalculatorGoals(
        Map<String, String> recipes,
        Map<String, String> makers,
        Map<Item, Integer> productionGoals) {

    public CalculatorGoals(
            Map<String, String> recipes, Map<String, String> makers, Map<Item, Integer> productionGoals) {
        this.recipes = Objects.requireNonNull(recipes);
        this.makers = Objects.requireNonNull(makers);
        this.productionGoals = Objects.requireNonNull(productionGoals);
    }

    public static CalculatorGoals fromPath(Path goalsPath, CalculatorConfig calculatorConfig) throws IOException {
        JsonCalculatorGoals jsonCalculatorGoals = JsonReader.readJsonCalculatorGoals(goalsPath);
        Map<String, String> chosenRecipes = jsonCalculatorGoals.recipes();
        verifyChosenRecipes(calculatorConfig, chosenRecipes);
        Map<String, String> chosenMakers = jsonCalculatorGoals.makers();
        verifyChosenMakers(calculatorConfig, chosenMakers);
        Map<Item, Integer> productionGoals = jsonCalculatorGoals.productionGoals().entrySet().stream().collect(
                Collectors.toMap(entry -> Objects.requireNonNull(calculatorConfig.getItem(entry.getKey())),
                        Map.Entry::getValue));
        return new CalculatorGoals(chosenRecipes, chosenMakers, productionGoals);
    }

    private static void verifyChosenRecipes(CalculatorConfig calculatorConfig, Map<String, String> chosenRecipes) {
        for (Map.Entry<String, String> recipeEntry : chosenRecipes.entrySet()) {
            boolean found = false;
            for (Recipe recipe : calculatorConfig.getRecipes(recipeEntry.getKey())) {
                if (recipe.name().equals(recipeEntry.getValue())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new IllegalArgumentException(
                        "Missing recipe " + recipeEntry.getValue() + " for item " + recipeEntry.getKey());
            }
        }
    }

    private static void verifyChosenMakers(CalculatorConfig calculatorConfig, Map<String, String> chosenMakers) {
        for (Map.Entry<String, String> makerEntry : chosenMakers.entrySet()) {
            boolean found = false;
            for (Maker maker : calculatorConfig.getMakers(makerEntry.getKey())) {
                if (maker.name().equals(makerEntry.getValue())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new IllegalArgumentException(
                        "Missing maker " + makerEntry.getValue() + " for maker group " + makerEntry.getKey());
            }
        }
    }
}
