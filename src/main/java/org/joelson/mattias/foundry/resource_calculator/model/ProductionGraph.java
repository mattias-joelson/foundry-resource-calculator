package org.joelson.mattias.foundry.resource_calculator.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ProductionGraph {

    private final Map<String, ProductionGraphNode> itemNodeMap;
    private final ArrayList<Set<ProductionGraphNode>> productionGraphLevels;

    private ProductionGraph(
            Map<String, ProductionGraphNode> itemNodeMap,
            ArrayList<Set<ProductionGraphNode>> productionGraphLevels) {
        this.itemNodeMap = Objects.requireNonNull(itemNodeMap);
        this.productionGraphLevels = Objects.requireNonNull(productionGraphLevels);
    }

    public void addGoals(Map<Item, Integer> productionGoals) {
        for (Map.Entry<Item, Integer> goalEntry : productionGoals.entrySet()) {
            addItemsPerMinute(goalEntry.getKey(), goalEntry.getValue());
        }
    }

    private void addItemsPerMinute(Item item, Integer itemsPerMinute) {
        itemNodeMap.get(item.name()).addItemsPerMinute(itemsPerMinute);
    }

    public void calculateProduction() {
        for (int level = productionGraphLevels.size() - 1; level > 0; level -= 1) {
            for (ProductionGraphNode productionGraphNode : productionGraphLevels.get(level)) {
                calculateProduction(productionGraphNode);
            }
        }

        printProduction();
    }

    private void calculateProduction(ProductionGraphNode productionGraphNode) {
        Recipe recipe = productionGraphNode.getRecipe();
        float itemsPerMinute = productionGraphNode.getItemsPerMinute();
        float productionCycles = itemsPerMinute / recipe.itemsProduced();
        for (Map.Entry<Item, Integer> ingredientEntry : recipe.ingredients().entrySet()) {
            itemNodeMap.get(ingredientEntry.getKey().name()).addItemsPerMinute(
                    productionCycles * ingredientEntry.getValue());
        }
    }

    private void printProduction() {
        for (int level = productionGraphLevels.size() - 1; level >= 0; level -= 1) {
            System.out.printf("%n========%nLevel %2d%n========%n", level);
            for (ProductionGraphNode productionGraphNode : productionGraphLevels.get(level)) {
                float itemsPerMinute = productionGraphNode.getItemsPerMinute();
                if (itemsPerMinute > 0) {
                    System.out.printf("%s%s: %.3f (%.3f -> %.0f belts-2)%n", productionGraphNode.getItem().gameName(),
                            recipeGameNameOf(productionGraphNode), itemsPerMinute, itemsPerMinute / 320,
                            Math.ceil(itemsPerMinute / 320));
                }
            }
        }
    }

    private String recipeGameNameOf(ProductionGraphNode productionGraphNode) {
        if (productionGraphNode.getRecipe() != null) {
            return String.format(" (%s)", productionGraphNode.getRecipe().gameName());
        }
        return "";
    }

    public static ProductionGraph from(CalculatorConfig calculatorConfig, CalculatorGoals calculatorGoals) {
        Map<String, ProductionGraphNode> itemNodeMap = new HashMap<>();
        ArrayList<Set<ProductionGraphNode>> productionGraphLevels = new ArrayList<>();
        Map<String, Integer> productionLevels = new HashMap<>();

        Map<String, String> chosenRecipes = calculatorGoals.recipes();

        for (Item item : calculatorConfig.getItems()) {
            Recipe recipe = chooseRecipe(calculatorConfig, chosenRecipes, item.name());
            if (recipe != null) {
                addRecipe(calculatorConfig, chosenRecipes, recipe, itemNodeMap, productionGraphLevels,
                        productionLevels);
            }
        }
        return new ProductionGraph(itemNodeMap, productionGraphLevels);
    }

    private static Recipe chooseRecipe(
            CalculatorConfig calculatorConfig, Map<String, String> chosenRecipes,
            String itemName) {
        Set<Recipe> itemRecipes = calculatorConfig.getRecipes(itemName);
        if (itemRecipes == null) {
            return null;
        }
        if (itemRecipes.isEmpty()) {
            throw new IllegalArgumentException("No recipe exists for " + itemName);
        }
        if (chosenRecipes.containsKey(itemName)) {
            String recipeName = chosenRecipes.get(itemName);
            for (Recipe recipe : itemRecipes) {
                if (recipe.name().equals(recipeName)) {
                    return recipe;
                }
            }
            throw new IllegalArgumentException("Recipe " + recipeName + " for item " + itemName + " not found.");
        } else {
            if (itemRecipes.size() > 1) {
                throw new IllegalStateException("Multiple recipes exists for " + itemName);
            }
            return itemRecipes.iterator().next();
        }
    }

    private static int addRecipe(
            CalculatorConfig calculatorConfig, Map<String, String> chosenRecipes, Recipe recipe,
            Map<String, ProductionGraphNode> itemNodeMap,
            ArrayList<Set<ProductionGraphNode>> productionGraphLevels, Map<String, Integer> productionLevels) {
        Item item = recipe.item();
        if (itemNodeMap.containsKey(item.name())) {
            return productionLevels.get(item.name());
        }

        Set<Item> ingredients = recipe.ingredients().keySet();
        int ingredientsMaxProductionLevel = -1;
        for (Item ingredient : ingredients) {
            Recipe ingredientRecipe = chooseRecipe(calculatorConfig, chosenRecipes, ingredient.name());
            if (ingredientRecipe == null) {
                addProductionGraphNode(null, itemNodeMap, productionGraphLevels, productionLevels, 0, ingredient);
                ingredientsMaxProductionLevel = Math.max(ingredientsMaxProductionLevel, 0);
            } else {
                ingredientsMaxProductionLevel = Math.max(ingredientsMaxProductionLevel,
                        addRecipe(calculatorConfig, chosenRecipes, ingredientRecipe, itemNodeMap, productionGraphLevels,
                                productionLevels));
            }
        }
        int recipeProductionLevel = ingredientsMaxProductionLevel + 1;
        addProductionGraphNode(recipe, itemNodeMap, productionGraphLevels, productionLevels, recipeProductionLevel,
                item);
        return recipeProductionLevel;
    }

    private static void addProductionGraphNode(
            Recipe recipe, Map<String, ProductionGraphNode> itemNodeMap,
            ArrayList<Set<ProductionGraphNode>> productionGraphLevels, Map<String, Integer> productionLevels,
            int recipeProductionLevel, Item item) {
        Set<ProductionGraphNode> productionGraphLevel = getProductionGraphLevel(productionGraphLevels,
                recipeProductionLevel);
        ProductionGraphNode productionGraphNode = new ProductionGraphNode(item, recipe);
        itemNodeMap.put(item.name(), productionGraphNode);
        productionGraphLevel.add(productionGraphNode);
        productionLevels.put(item.name(), recipeProductionLevel);
    }

    private static Set<ProductionGraphNode> getProductionGraphLevel(
            ArrayList<Set<ProductionGraphNode>> productionGraphLevels, int level) {
        while (productionGraphLevels.size() <= level) {
            productionGraphLevels.add(new HashSet<>());
        }
        return productionGraphLevels.get(level);
    }
}
