package org.joelson.mattias.foundry.resource_calculator.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

record JsonCalculatorGoals(
        Map<String, String> recipes,
        Map<String, Integer> productionGoals) {

    @JsonCreator
    public JsonCalculatorGoals(
            @JsonProperty(value = "recipes", required = true) Map<String, String> recipes,
            @JsonProperty(value = "productionGoals", required = true) Map<String, Integer> productionGoals) {
        this.recipes = recipes;
        this.productionGoals = productionGoals;
    }
}
