package org.joelson.mattias.foundry.resource_calculator.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ProductionGraph {

    private final Map<String, ProductionGraphNode> itemNodeMap;
    private final ArrayList<Set<ProductionGraphNode>> productionGraphLevels;
    private final List<String> productionTableItemNameColumns;
    private final List<String> productionTableItemNameRows;

    private ProductionGraph(
            Map<String, ProductionGraphNode> itemNodeMap,
            ArrayList<Set<ProductionGraphNode>> productionGraphLevels, List<String> productionTableItemNameColumns,
            List<String> productionTableItemNameRows) {
        this.itemNodeMap = Objects.requireNonNull(itemNodeMap);
        this.productionGraphLevels = Objects.requireNonNull(productionGraphLevels);
        this.productionTableItemNameColumns = Objects.requireNonNull(productionTableItemNameColumns);
        this.productionTableItemNameRows = Objects.requireNonNull(productionTableItemNameRows);
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
        printProductionTable();
    }

    private void calculateProduction(ProductionGraphNode productionGraphNode) {
        Recipe recipe = productionGraphNode.getRecipe();
        float itemsPerMinute = productionGraphNode.getItemsPerMinute();
        float productionCycles = itemsPerMinute / recipe.itemsProduced();
        for (Map.Entry<Item, Integer> ingredientAmount : recipe.ingredientAmounts().entrySet()) {
            itemNodeMap.get(ingredientAmount.getKey().name()).addItemsPerMinute(
                    productionCycles * ingredientAmount.getValue());
        }
    }

    private void printProduction() {
        for (int level = productionGraphLevels.size() - 1; level >= 0; level -= 1) {
            System.out.printf("%n========%nLevel %2d%n========%n", level);
            for (ProductionGraphNode productionGraphNode : productionGraphLevels.get(level)) {
                float itemsPerMinute = productionGraphNode.getItemsPerMinute();
                if (itemsPerMinute > 0) {
                    printItemProduction(productionGraphNode, itemsPerMinute);
                }
            }
        }
    }

    private void printItemProduction(ProductionGraphNode productionGraphNode, float itemsPerMinute) {
        System.out.printf("%s%s: %.3f (%s)%s%n",
                productionGraphNode.getItem().gameName(), recipeGameNameOf(productionGraphNode), itemsPerMinute,
                logisticsOf(productionGraphNode, itemsPerMinute), makersOf(productionGraphNode, itemsPerMinute));
    }

    private String recipeGameNameOf(ProductionGraphNode productionGraphNode) {
        if (productionGraphNode.getRecipe() != null) {
            return String.format(" (%s)", productionGraphNode.getRecipe().gameName());
        }
        return "";
    }

    private String logisticsOf(ProductionGraphNode productionGraphNode, float itemsPerMinute) {
        if (productionGraphNode.getItem().stackSize() > 0) {
            return String.format("%.3f -> %.0f conveyor-2", itemsPerMinute / 320, Math.ceil(itemsPerMinute / 320));
        } else {
            return String.format("%.3f -> %.0f pipes", itemsPerMinute / 36000, Math.ceil(itemsPerMinute / 36000));
        }
    }

    private Object makersOf(ProductionGraphNode productionGraphNode, float itemsPerMinute) {
        Maker maker = productionGraphNode.getMaker();
        if (maker != null) {
            Recipe recipe = productionGraphNode.getRecipe();
            float productionCyclesPerMinute = 60.0f / recipe.time() * maker.speedMultiplier();
            String ingredients = ingredientsPerMinuter(recipe, productionCyclesPerMinute);
            String products = itemsPerMinute(recipe.item(), recipe.itemsProduced(), productionCyclesPerMinute);
            String totalIngredients = ingredientsPerMinuter(recipe, itemsPerMinute / recipe.itemsProduced());
            return String.format(" by %.3f %s (%s -> %s) needing %s",
                    itemsPerMinute / recipe.itemsProduced() / productionCyclesPerMinute, maker.gameName(), ingredients,
                    products, totalIngredients);
        }
        return "";
    }

    private static String ingredientsPerMinuter(Recipe recipe, float productionCyclesPerMinute) {
        return recipe.ingredientAmounts().entrySet().stream()
                .map(itemIntegerEntry -> itemsPerMinute(itemIntegerEntry.getKey(), itemIntegerEntry.getValue(),
                        productionCyclesPerMinute))
                .collect(Collectors.joining(", "));
    }

    private static String itemsPerMinute(Item item, float numberOfItems, float productionCyclesPerMinute) {
        return String.format("%s %.2f/min", item.gameName(), numberOfItems * productionCyclesPerMinute);
    }

    private void printProductionTable() {
        if (productionTableItemNameColumns.isEmpty() || productionTableItemNameRows.isEmpty()) {
            return;
        }
        System.out.printf("%n================%nProduction Table%n================%n");
        System.out.println("item;amount;" + String.join(";", productionTableItemNameColumns) + ";other");
        for (String rowItemName : productionTableItemNameRows) {
            StringBuilder otherBuilder = new StringBuilder();
            float[] productionRow = new float[productionTableItemNameColumns.size()];
            ProductionGraphNode productionGraphNode = itemNodeMap.get(rowItemName);
            Recipe recipe = productionGraphNode.getRecipe();
            float itemsPerMinute = productionGraphNode.getItemsPerMinute();
            for (Map.Entry<Item, Integer> ingredientAmount : recipe.ingredientAmounts().entrySet()) {
                int index = productionTableItemNameColumns.indexOf(ingredientAmount.getKey().name());
                float ingredientItemsPerMinute = ingredientAmount.getValue() * itemsPerMinute / recipe.itemsProduced();
                if (index >= 0) {
                    productionRow[index] = ingredientItemsPerMinute;
                } else {
                    if (!otherBuilder.isEmpty()) {
                        otherBuilder.append(", ");
                    }
                    otherBuilder.append(ingredientAmount.getKey().name()).append(" ").append(
                            formatFloat(ingredientItemsPerMinute));
                }
            }
            StringBuilder rowBuilder = new StringBuilder();
            rowBuilder.append(rowItemName).append(";").append(formatFloat(itemsPerMinute)).append(";");
            for (float f : productionRow) {
                if (f != 0) {
                    rowBuilder.append(formatFloat(f));
                }
                rowBuilder.append(";");
            }
            rowBuilder.append(otherBuilder);
            System.out.println(rowBuilder);
        }
    }

    private static String formatFloat(float f) {
        return String.format("%.3f", f);
    }

    public static ProductionGraph from(CalculatorConfig calculatorConfig, CalculatorGoals calculatorGoals) {
        Map<String, ProductionGraphNode> itemNodeMap = new HashMap<>();
        ArrayList<Set<ProductionGraphNode>> productionGraphLevels = new ArrayList<>();
        Map<String, Integer> productionLevels = new HashMap<>();

        Map<String, Maker> chosenMakers = lookupChosenMakers(calculatorConfig, calculatorGoals.makers());
        Map<String, Recipe> chosenRecipes = lookupChosenRecipes(calculatorConfig, calculatorGoals.recipes());

        for (Item item : calculatorConfig.getItems()) {
            Recipe recipe = chooseRecipe(calculatorConfig, chosenRecipes, item.name());
            if (recipe != null) {
                Maker maker = chooseMaker(chosenMakers, recipe.makers());
                addRecipe(calculatorConfig, chosenRecipes, chosenMakers, recipe, maker, itemNodeMap,
                        productionGraphLevels,
                        productionLevels);
            }
        }
        return new ProductionGraph(itemNodeMap, productionGraphLevels, calculatorGoals.productionTableColumns(),
                calculatorGoals.productionTableRows());
    }

    private static Map<String, Maker> lookupChosenMakers(
            CalculatorConfig calculatorConfig, Map<String, String> chosenMakerNames) {
        Map<String, Maker> chosenMakers = new HashMap<>();
        for (Map.Entry<String, String> chosenMakerGroupMaker : chosenMakerNames.entrySet()) {
            chosenMakers.put(chosenMakerGroupMaker.getKey(),
                    selectMaker(calculatorConfig.getMakers(chosenMakerGroupMaker.getKey()), chosenMakerGroupMaker.getKey(),
                            chosenMakerGroupMaker.getValue()));
        }
        return chosenMakers;
    }

    private static Maker selectMaker(Set<Maker> makers, String makerGroupName, String makerName) {
        for (Maker maker : makers) {
            if (maker.name().equals(makerName)) {
                return maker;
            }
        }
        throw new IllegalArgumentException(
                "No maker found with name " + makerName + " for maker group " + makerGroupName);
    }

    private static Maker chooseMaker(Map<String, Maker> chosenMakers, Set<Maker> possibleMakers) {
        if (possibleMakers.size() == 1) {
            return possibleMakers.iterator().next();
        }
        for (Maker chosenMaker : chosenMakers.values()) {
            for (Maker possibleMaker : possibleMakers) {
                if (possibleMaker.name().equals(chosenMaker.name())) {
                    return chosenMaker;
                }
            }
        }
        throw new IllegalArgumentException("No chosen maker for possible chosenMakers " + possibleMakers);
    }

    private static Map<String, Recipe> lookupChosenRecipes(
            CalculatorConfig calculatorConfig, Map<String, String> chosenRecipeNames) {
        Map<String, Recipe> chosenRecipes = new HashMap<>();
        for (Map.Entry<String, String> chosenRecipeName : chosenRecipeNames.entrySet()) {
            chosenRecipes.put(chosenRecipeName.getKey(),
                    selectRecipe(calculatorConfig.getRecipes(chosenRecipeName.getKey()), chosenRecipeName.getValue()));
        }
        return chosenRecipes;
    }

    private static Recipe selectRecipe(Set<Recipe> recipes, String recipeName) {
        for (Recipe recipe : recipes) {
            if (recipe.name().equals(recipeName)) {
                return recipe;
            }
        }
        throw new IllegalArgumentException(
                "No recipe found with name " + recipeName + " for item " + recipes.iterator().next().item().name());
    }

    private static Recipe chooseRecipe(
            CalculatorConfig calculatorConfig, Map<String, Recipe> chosenRecipes,
            String itemName) {
        if (chosenRecipes.containsKey(itemName)) {
            return chosenRecipes.get(itemName);
        }
        Set<Recipe> recipes = calculatorConfig.getRecipes(itemName);
        if (recipes == null) {
            return null;
        }
        if (recipes.size() != 1) {
            throw new IllegalArgumentException(
                    "There exists not a single recipe for unchosen item " + itemName + ": " + recipes);
        }
        return recipes.iterator().next();
    }

    private static int addRecipe(
            CalculatorConfig calculatorConfig, Map<String, Recipe> chosenRecipes, Map<String, Maker> chosenMakers,
            Recipe recipe, Maker maker, Map<String, ProductionGraphNode> itemNodeMap,
            ArrayList<Set<ProductionGraphNode>> productionGraphLevels, Map<String, Integer> productionLevels) {
        Item item = recipe.item();
        if (itemNodeMap.containsKey(item.name())) {
            return productionLevels.get(item.name());
        }

        Set<Item> ingredients = recipe.ingredientAmounts().keySet();
        int ingredientsMaxProductionLevel = -1;
        for (Item ingredient : ingredients) {
            Recipe ingredientRecipe = chooseRecipe(calculatorConfig, chosenRecipes, ingredient.name());
            if (ingredientRecipe == null) {
                ingredientsMaxProductionLevel = Math.max(ingredientsMaxProductionLevel,
                        addResource(itemNodeMap, productionGraphLevels, productionLevels, ingredient));
            } else {
                Maker ingredientMaker = chooseMaker(chosenMakers, ingredientRecipe.makers());
                ingredientsMaxProductionLevel = Math.max(ingredientsMaxProductionLevel,
                        addRecipe(calculatorConfig, chosenRecipes, chosenMakers, ingredientRecipe, ingredientMaker,
                                itemNodeMap, productionGraphLevels, productionLevels));
            }
        }
        int recipeProductionLevel = ingredientsMaxProductionLevel + 1;
        addProductionGraphNode(recipe, maker, itemNodeMap, productionGraphLevels, productionLevels,
                recipeProductionLevel,
                item);
        return recipeProductionLevel;
    }

    private static int addResource(
            Map<String, ProductionGraphNode> itemNodeMap, ArrayList<Set<ProductionGraphNode>> productionGraphLevels,
            Map<String, Integer> productionLevels, Item resource) {
        if (productionLevels.containsKey(resource.name())) {
            return productionLevels.get(resource.name());
        }
        addProductionGraphNode(null, null, itemNodeMap, productionGraphLevels, productionLevels, 0, resource);
        return 0;
    }

    private static void addProductionGraphNode(
            Recipe recipe, Maker maker, Map<String, ProductionGraphNode> itemNodeMap,
            ArrayList<Set<ProductionGraphNode>> productionGraphLevels, Map<String, Integer> productionLevels,
            int recipeProductionLevel, Item item) {
        Set<ProductionGraphNode> productionGraphLevel =
                getProductionGraphLevel(productionGraphLevels, recipeProductionLevel);
        if (productionLevels.containsKey(item.name())) {
            throw new IllegalStateException("ProductionGraphNode already exists in map for item " + item.name());
        }
        for (ProductionGraphNode productionGraphNode : productionGraphLevel) {
            if (productionGraphNode.getItem().name().equals(item.name())) {
                throw new IllegalStateException(
                        "ProductionGraphNode already exists on level " + recipeProductionLevel + " for item "
                                + item.name());
            }
        }
        ProductionGraphNode productionGraphNode = new ProductionGraphNode(item, recipe, maker);
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
