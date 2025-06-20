package org.joelson.mattias.foundry.resource_calculator.model;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CalculatorConfig {

    private final Map<String, Set<Maker>> makerGroups;
    private final Map<String, Item> items;
    private final Map<String, Set<Recipe>> recipes;

    public CalculatorConfig(
            Map<String, Set<Maker>> makerGroups, Map<String, Item> items, Map<String, Set<Recipe>> recipes) {
        this.makerGroups = Objects.requireNonNull(makerGroups);
        this.items = Objects.requireNonNull(items);
        this.recipes = Objects.requireNonNull(recipes);
    }

    public Set<Maker> getMakers(String makerGroupName) {
        return makerGroups.get(makerGroupName);
    }

    public Set<Item> getItems() {
        Collection<Item> itemCollection = items.values();
        return new HashSet<>(itemCollection);
    }

    public Item getItem(String itemName) {
        return items.get(itemName);
    }

    public Set<Recipe> getRecipes(String itemName) {
        return recipes.get(itemName);
    }

    public static CalculatorConfig fromPath(Path calculatorConfigPath) throws IOException {
        JsonCalculatorConfig jsonCalculatorConfig = JsonReader.readJsonCalculatorConfig(calculatorConfigPath);
        Set<Maker> makers = makersFrom(jsonCalculatorConfig.makers());
        Map<String, Set<Maker>> makerGroups = makerGroupsFrom(makers, jsonCalculatorConfig.makerGroups());
        Map<String, Item> items = itemsFrom(jsonCalculatorConfig.items());
        Map<String, Set<Recipe>> recipes = recipesFrom(jsonCalculatorConfig.recipes(), items, makerGroups);
        Set<String> itemNames = items.keySet();
        Set<String> recipeItemNames = recipes.keySet();
        if (!recipeItemNames.containsAll(itemNames)) {
            itemNames = new HashSet<>(itemNames);
            itemNames.removeAll(recipeItemNames);
            System.err.println("Lacking recipes for " + itemNames);
            //throw new IllegalStateException("Lacking recipes for " + itemNames);
        }
        return new CalculatorConfig(makerGroups, items, recipes);
    }

    private static Set<Maker> makersFrom(Set<JsonMaker> jsonMakers) {
        return jsonMakers.stream().map(CalculatorConfig::makerFrom).collect(Collectors.toSet());
    }

    private static Maker makerFrom(JsonMaker jsonMaker) {
        return new Maker(jsonMaker.name(), jsonMaker.gameName(), jsonMaker.speedMultiplier());
    }

    private static Map<String, Set<Maker>> makerGroupsFrom(
            Set<Maker> makers, Map<String, Set<String>> jsonMakerGroups) {
        Map<String, Set<Maker>> makerGroupsMap = new HashMap<>();
        for (Maker maker : makers) {
            if (makerGroupsMap.put(maker.name(), Collections.singleton(maker)) != null) {
                throw new IllegalStateException("Multiple makers with name " + maker.name());
            }
        }
        Map<String, Maker> makersMap = makers.stream().collect(Collectors.toMap(Maker::name, Function.identity()));
        for (Map.Entry<String, Set<String>> jsonMakerGroupEntry : jsonMakerGroups.entrySet()) {
            Set<Maker> groupMakers = new HashSet<>();
            for (String makerName : jsonMakerGroupEntry.getValue()) {
                Maker maker = makersMap.get(makerName);
                if (maker == null) {
                    throw new IllegalStateException("No maker found for name " + makerName);
                }
                groupMakers.add(maker);
            }
            if (makerGroupsMap.put(jsonMakerGroupEntry.getKey(), groupMakers) != null) {
                throw new IllegalStateException("Multiple maker groups with name " + jsonMakerGroupEntry.getKey());
            }
        }
        return makerGroupsMap;
    }

    private static Map<String, Item> itemsFrom(Set<JsonItem> jsonItems) {
        return jsonItems.stream().map(CalculatorConfig::itemFrom).collect(
                Collectors.toMap(Item::name, Function.identity()));
    }

    private static Item itemFrom(JsonItem jsonItem) {
        return new Item(jsonItem.name(), jsonItem.gameName(), jsonItem.stackSize(), jsonItem.weight());
    }

    private static Map<String, Set<Recipe>> recipesFrom(
            Set<JsonRecipe> jsonRecipes, Map<String, Item> items, Map<String, Set<Maker>> makerGroups) {
        Map<String, Set<Recipe>> recipes = new HashMap<>();
        Set<String> recipeNames = new HashSet<>();
        for (JsonRecipe jsonRecipe : jsonRecipes) {
            String recipeName = jsonRecipe.name();
            if (recipeNames.contains(recipeName)) {
                throw new IllegalArgumentException("Multiple recipes with name " + recipeName);
            }
            recipeNames.add(recipeName);
            Recipe recipe = recipeFrom(jsonRecipe, items, makerGroups);
            recipes.computeIfAbsent(recipe.item().name(), s -> new HashSet<>()).add(recipe);
        }
        return recipes;
    }

    private static Recipe recipeFrom(
            JsonRecipe jsonRecipe, Map<String, Item> items, Map<String, Set<Maker>> makerGroups) {
        Map<Item, Integer> ingredients = jsonRecipe.ingredients().entrySet().stream()
                .map(nameIntegerEntry -> Map.entry(items.get(nameIntegerEntry.getKey()), nameIntegerEntry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Set<Maker> recipeMakers = Objects.requireNonNull(makerGroups.get(jsonRecipe.makerName()));
        return new Recipe(jsonRecipe.name(), jsonRecipe.gameName(), items.get(jsonRecipe.itemName()), ingredients,
                jsonRecipe.itemsProduced(), jsonRecipe.time(), recipeMakers);
    }
}
