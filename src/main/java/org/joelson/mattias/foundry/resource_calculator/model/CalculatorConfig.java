package org.joelson.mattias.foundry.resource_calculator.model;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CalculatorConfig {

    private final Set<Maker> makers;
    private final Map<String, Item> items;
    private final Map<String, Recipe> recipes;

    public CalculatorConfig(Set<Maker> makers, Map<String, Item> items, Map<String, Recipe> recipes) {
        this.makers = Objects.requireNonNull(makers);
        this.items = Objects.requireNonNull(items);
        this.recipes = Objects.requireNonNull(recipes);
    }

    public Item getItem(String itemName) {
        return items.get(itemName);
    }

    public Collection<Recipe> getRecipes() {
        return recipes.values();
    }

    public Recipe getRecipe(String itemName) {
        return recipes.get(itemName);
    }

    public static CalculatorConfig fromPath(Path calculatorConfigPath) throws IOException {
        JsonCalculatorConfig jsonCalculatorConfig = JsonReader.readJsonCalculatorConfig(calculatorConfigPath);
        Set<Maker> makers = makersFrom(jsonCalculatorConfig.makers());
        Map<String, Maker> makersMap = makers.stream().collect(Collectors.toMap(Maker::name, Function.identity()));
        Map<String, Item> items = itemsFrom(jsonCalculatorConfig.items());
        Map<String, Recipe> recipes = recipesFrom(jsonCalculatorConfig.recipes(), items, makersMap);
        Set<String> itemNames = items.keySet();
        Set<String> recipeItemNames = recipes.keySet();
        if (!recipeItemNames.containsAll(itemNames)) {
            itemNames = new HashSet<>(itemNames);
            itemNames.removeAll(recipeItemNames);
            System.err.println("Lacking recipes for " + itemNames);
            //throw new IllegalStateException("Lacking recipes for " + itemNames);
        }
        return new CalculatorConfig(makers, items, recipes);
    }

    private static Set<Maker> makersFrom(List<JsonMaker> jsonMakers) {
        return jsonMakers.stream().map(CalculatorConfig::makerFrom).collect(Collectors.toSet());
    }

    private static Maker makerFrom(JsonMaker jsonMaker) {
        return new Maker(jsonMaker.name(), jsonMaker.gameName(), jsonMaker.speedMultiplier());
    }

    private static Map<String, Item> itemsFrom(List<JsonItem> jsonItems) {
        return jsonItems.stream().map(CalculatorConfig::itemFrom).collect(
                Collectors.toMap(Item::name, Function.identity()));
    }

    private static Item itemFrom(JsonItem jsonItem) {
        return new Item(jsonItem.name(), jsonItem.gameName(), jsonItem.stackSize(), jsonItem.weight());
    }

    private static Map<String, Recipe> recipesFrom(
            List<JsonRecipe> jsonRecipes, Map<String, Item> items, Map<String, Maker> makers) {
        return jsonRecipes.stream().map(jsonRecipe -> recipeFrom(jsonRecipe, items, makers)).collect(
                Collectors.toMap(recipe -> recipe.item().name(), Function.identity()));
    }

    private static Recipe recipeFrom(JsonRecipe jsonRecipe, Map<String, Item> items, Map<String, Maker> makers) {
        Map<Item, Integer> ingredients = jsonRecipe.ingredients().entrySet().stream()
                .map(nameIntegerEntry -> Map.entry(items.get(nameIntegerEntry.getKey()), nameIntegerEntry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Set<Maker> recipeMakers = jsonRecipe.makerNames().stream()
                .map(makerName -> Objects.requireNonNull(makers.get(makerName))).collect(Collectors.toSet());
        return new Recipe(jsonRecipe.name(), jsonRecipe.gameName(), items.get(jsonRecipe.itemName()), ingredients,
                jsonRecipe.itemsProduced(), jsonRecipe.time(), recipeMakers);
    }
}
