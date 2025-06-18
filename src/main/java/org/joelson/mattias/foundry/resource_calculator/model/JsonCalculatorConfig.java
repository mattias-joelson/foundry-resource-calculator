package org.joelson.mattias.foundry.resource_calculator.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

record JsonCalculatorConfig(
        List<JsonMaker> makers,
        List<JsonItem> items,
        List<JsonRecipe> recipes)
{
    @JsonCreator
    public JsonCalculatorConfig(
            @JsonProperty(value = "makers", required = true) List<JsonMaker> makers,
            @JsonProperty(value = "items", required = true) List<JsonItem> items,
            @JsonProperty(value = "recipes", required = true) List<JsonRecipe> recipes) {
        this.makers = makers;
        this.items = items;
        this.recipes = recipes;
    }
}
