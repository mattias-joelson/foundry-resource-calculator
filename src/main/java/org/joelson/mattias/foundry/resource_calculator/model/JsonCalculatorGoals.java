package org.joelson.mattias.foundry.resource_calculator.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

record JsonCalculatorGoals(
        Map<String, Integer> productionGoals) {

    @JsonCreator
    public JsonCalculatorGoals(
            @JsonProperty(value = "productionGoals", required = true) Map<String, Integer> productionGoals) {
        this.productionGoals = productionGoals;
    }
}
