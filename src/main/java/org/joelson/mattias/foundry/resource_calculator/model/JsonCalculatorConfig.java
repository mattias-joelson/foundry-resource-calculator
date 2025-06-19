package org.joelson.mattias.foundry.resource_calculator.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.Set;

record JsonCalculatorConfig(
        Set<JsonMaker> makers,
        Map<String, Set<String>> makerGroups,
        Set<JsonItem> items,
        Set<JsonRecipe> recipes)
{
    @JsonCreator
    public JsonCalculatorConfig(
            @JsonProperty(value = "makers", required = true) Set<JsonMaker> makers,
            @JsonProperty(value = "makerGroups", required = true) Map<String, Set<String>> makerGroups,
            @JsonProperty(value = "items", required = true) Set<JsonItem> items,
            @JsonProperty(value = "recipes", required = true) Set<JsonRecipe> recipes) {
        this.makers = makers;
        this.makerGroups = makerGroups;
        this.items = items;
        this.recipes = recipes;
    }
}
