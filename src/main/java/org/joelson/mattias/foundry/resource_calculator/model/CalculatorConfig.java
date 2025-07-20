package org.joelson.mattias.foundry.resource_calculator.model;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class CalculatorConfig {

    private final Map<String, Set<Maker>> makerGroups;
    private final Map<String, Item> items;
    private final Map<String, Set<Recipe>> recipes;

    private CalculatorConfig(
            Map<String, Set<Maker>> makerGroups, Map<String, Item> items, Map<String, Set<Recipe>> recipes) {
        this.makerGroups = Objects.requireNonNull(makerGroups);
        this.items = Objects.requireNonNull(items);
        this.recipes = Objects.requireNonNull(recipes);
    }

    Set<Maker> getMakers(String makerGroupName) {
        return makerGroups.get(makerGroupName);
    }

    public Set<Item> getItems() {
        Collection<Item> itemCollection = items.values();
        return new HashSet<>(itemCollection);
    }

    public Item getItem(String itemName) {
        return items.get(itemName);
    }

    Set<Recipe> getRecipes(String itemName) {
        return recipes.get(itemName);
    }

    public static CalculatorConfig fromPath(Path calculatorConfigPath) throws IOException {
        JsonCalculatorConfig jsonCalculatorConfig = JsonReader.readJsonCalculatorConfig(calculatorConfigPath);
        Set<Maker> makers = makersFrom(jsonCalculatorConfig.makers());
        Map<String, Set<Maker>> makerGroups = makerGroupsFrom(makers, jsonCalculatorConfig.makerGroups());
        Map<String, Item> items = itemsFrom(jsonCalculatorConfig.items());
        fuelItemsFrom(items, jsonCalculatorConfig.fuelItems());
        handheldsFrom(items, jsonCalculatorConfig.handhelds());
        robotsFrom(items, jsonCalculatorConfig.robots());
        Map<String, Set<Recipe>> recipes = recipesFrom(jsonCalculatorConfig.recipes(), items, makerGroups);
        Set<String> itemNamesWithoutRecipes = new HashSet<>(items.keySet());
        itemNamesWithoutRecipes.removeAll(
                Set.of("biomass", "xenoferrite-ore-rubble", "technum-ore-rubble", "ignium-ore-rubble", "mineral-rocks",
                        "telluxite-ore-rubble", "firmarlite-bar", "water", "crude-olumite", "air",
                        "hot-air", "steam", "blast-furnace-slag")); // FIXME not really resources
        itemNamesWithoutRecipes.removeAll(recipes.keySet());
        if (!itemNamesWithoutRecipes.isEmpty()) {
            throw new IllegalStateException("Lacking " + itemNamesWithoutRecipes.size() + " recipes for " + itemNamesWithoutRecipes);
        }
        return new CalculatorConfig(makerGroups, items, recipes);
    }

    private static Set<Maker> makersFrom(List<JsonMaker> jsonMakers) {
        return jsonMakers.stream().map(CalculatorConfig::makerFrom).collect(Collectors.toSet());
    }

    private static Maker makerFrom(JsonMaker jsonMaker) {
        return new Maker(jsonMaker.name(), jsonMaker.gameName(), jsonMaker.speedMultiplier());
    }

    private static Map<String, Set<Maker>> makerGroupsFrom(
            Set<Maker> makers, List<JsonMakerGroup> jsonMakerGroups) {
        Map<String, Maker> makersMap = new HashMap<>();
        Map<String, Set<Maker>> makerGroups = new HashMap<>();
        for (Maker maker : makers) {
            makersMap.put(maker.name(), maker);
            makerGroups.put(maker.name(), Collections.singleton(maker));
        }
        for (JsonMakerGroup jsonMakerGroup : jsonMakerGroups) {
            if (makerGroups.containsKey(jsonMakerGroup.groupName())) {
                throw new IllegalArgumentException(
                        "makerGroups already contains group name " + jsonMakerGroup.groupName());
            }
            Set<Maker> groupMakers = new HashSet<>();
            for (String makerName : jsonMakerGroup.makerNames()) {
                Maker maker = makersMap.get(makerName);
                if (maker == null) {
                    throw new IllegalStateException("No maker found for name " + makerName);
                }
                groupMakers.add(maker);
            }
            makerGroups.put(jsonMakerGroup.groupName(), groupMakers);
        }
        return makerGroups;
    }

    private static Map<String, Item> itemsFrom(List<JsonItem> jsonItems) {
        Map<String, Item> items = new HashMap<>();
        for (JsonItem jsonItem : jsonItems) {
            Item item = itemFrom(jsonItem);
            addItem(items, item);
        }
        return items;
    }

    private static Item itemFrom(JsonItem jsonItem) {
        return new Item(jsonItem.getName(), jsonItem.getGameName(), jsonItem.getStackSize(), jsonItem.getWeight());
    }

    private static void addItem(Map<String, Item> items, Item item) {
        if (items.containsKey(item.getName())) {
            throw new IllegalArgumentException("Items already contains item with name " + item.getName());
        }
        items.put(item.getName(), item);
    }

    private static void fuelItemsFrom(Map<String, Item> items, List<JsonFuelItem> jsonFuelItems) {
        for (JsonFuelItem jsonFuelItem : jsonFuelItems) {
            addItem(items, fuelItemFrom(items, jsonFuelItem));
        }
    }

    private static FuelItem fuelItemFrom(Map<String, Item> items, JsonFuelItem jsonFuelItem) {
        Item residualItem = (jsonFuelItem.getResidualItem() != null) ? items.get(jsonFuelItem.getResidualItem()) : null;
        return new FuelItem(jsonFuelItem.getName(), jsonFuelItem.getGameName(), jsonFuelItem.getStackSize(),
                jsonFuelItem.getWeight(), jsonFuelItem.getFuelValue(), residualItem);
    }

    private static void handheldsFrom(Map<String, Item> items, List<JsonHandheld> jsonHandhelds) {
        for (JsonHandheld jsonHandheld : jsonHandhelds) {
            addItem(items, handheldFrom(jsonHandheld));
        }
    }

    private static Handheld handheldFrom(JsonHandheld jsonHandheld) {
        return new Handheld(jsonHandheld.getName(), jsonHandheld.getGameName(), jsonHandheld.getStackSize());
    }

    private static void robotsFrom(Map<String, Item> items, List<JsonRobot> jsonRobots) {
        for (JsonRobot jsonRobot : jsonRobots) {
            addItem(items, robotFrom(jsonRobot));
        }
    }

    private static Robot robotFrom(JsonRobot jsonRobot) {
        return new Robot(jsonRobot.getName(), jsonRobot.getGameName(), jsonRobot.getStackSize(), jsonRobot.getWeight(),
                jsonRobot.getSalesPrice(), jsonRobot.getCategory());
    }

    private static Map<String, Set<Recipe>> recipesFrom(
            List<JsonRecipe> jsonRecipes, Map<String, Item> items, Map<String, Set<Maker>> makerGroups) {
        Map<String, Set<Recipe>> recipes = new HashMap<>();
        for (JsonRecipe jsonRecipe : jsonRecipes) {
            Recipe recipe = recipeFrom(jsonRecipe, items, makerGroups);
            recipes.computeIfAbsent(recipe.item().getName(), s -> new HashSet<>()).add(recipe);
        }
        return recipes;
    }

    private static Recipe recipeFrom(
            JsonRecipe jsonRecipe, Map<String, Item> items, Map<String, Set<Maker>> makerGroups) {
        Map<Item, Integer> ingredientAmounts = new HashMap<>();
        if (items.get(jsonRecipe.itemName()) == null) {
            throw new IllegalArgumentException("No item found for name " + jsonRecipe.itemName());
        }
        for (JsonItemAmount jsonIngredient : jsonRecipe.ingredients()) {
            Item item = items.get(jsonIngredient.itemName());
            if (item == null) {
                throw new IllegalArgumentException(String.format("No ingredient found for name %s in recipe %s",
                        jsonIngredient.itemName(), jsonRecipe.name()));
            }
            ingredientAmounts.put(item, jsonIngredient.amount());
        }
        Set<Maker> makers = makerGroups.get(jsonRecipe.makerGroupName());
        if (makers == null) {
            throw new IllegalArgumentException("No maker group found for name " + jsonRecipe.makerGroupName());
        }
        return new Recipe(jsonRecipe.name(), jsonRecipe.gameName(), items.get(jsonRecipe.itemName()), ingredientAmounts,
                jsonRecipe.itemsProduced(), jsonRecipe.time(), makers);
    }
}
