package org.joelson.mattias.foundry.resource_calculator.model;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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
        Map<String, String> chosenRecipes = verifyChosenRecipes(calculatorConfig, jsonCalculatorGoals.chosenRecipes());
        Map<String, String> chosenMakers = verifyChosenMakers(calculatorConfig, jsonCalculatorGoals.chosenMakers());
        Map<Item, Integer> productionGoals = verifyProductionGoals(calculatorConfig,
                jsonCalculatorGoals.productionGoals());
        return new CalculatorGoals(chosenRecipes, chosenMakers, productionGoals);
    }

    private static Map<String, String> verifyChosenRecipes(
            CalculatorConfig calculatorConfig, List<JsonChosenRecipe> jsonChosenRecipes) {
        Map<String, String> chosenRecipes = new HashMap<>();
        for (JsonChosenRecipe jsonChosenRecipe : jsonChosenRecipes) {
            boolean found = false;
            for (Recipe recipe : calculatorConfig.getRecipes(jsonChosenRecipe.itemName())) {
                if (recipe.name().equals(jsonChosenRecipe.recipeName())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new IllegalArgumentException(
                        "Missing recipe " + jsonChosenRecipe.recipeName() + " for item " + jsonChosenRecipe.itemName());
            }
            chosenRecipes.put(jsonChosenRecipe.itemName(), jsonChosenRecipe.recipeName());
        }
        return chosenRecipes;
    }

    private static Map<String, String> verifyChosenMakers(
            CalculatorConfig calculatorConfig, List<JsonChosenMaker> jsonChosenMakers) {
        Map<String, String> chosenMakers = new HashMap<>();
        for (JsonChosenMaker jsonChosenMaker : jsonChosenMakers) {
            boolean found = false;
            Set<Maker> makers = calculatorConfig.getMakers(jsonChosenMaker.makerGroupName());
            if (makers == null) {
                throw new IllegalArgumentException("Missing maker group " + jsonChosenMaker.makerGroupName());
            }
            for (Maker maker : makers) {
                if (maker.name().equals(jsonChosenMaker.makerName())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new IllegalArgumentException(
                        "Missing maker " + jsonChosenMaker.makerName() + " for maker group "
                                + jsonChosenMaker.makerGroupName());
            }
            chosenMakers.put(jsonChosenMaker.makerGroupName(), jsonChosenMaker.makerName());
        }
        return chosenMakers;
    }

    private static Map<Item, Integer> verifyProductionGoals(
            CalculatorConfig calculatorConfig, List<JsonItemAmount> jsonProductionGoals) {
        Map<Item, Integer> productionGoals = new HashMap<>();
        for (JsonItemAmount jsonProductionGoal : jsonProductionGoals) {
            Item item = calculatorConfig.getItem(jsonProductionGoal.itemName());
            if (item == null) {
                throw new IllegalArgumentException("Missing item with name " + jsonProductionGoal.itemName());
            }
            productionGoals.put(item, jsonProductionGoal.amount());
        }
        return productionGoals;
    }
}
