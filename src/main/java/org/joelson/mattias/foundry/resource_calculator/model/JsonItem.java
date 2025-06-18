package org.joelson.mattias.foundry.resource_calculator.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

record JsonItem(
        String name,
        String gameName,
        String description,
        int stackSize,
        float weight) {

    @JsonCreator
    public JsonItem(
            @JsonProperty(value = "name", required = true) String name,
            @JsonProperty(value = "gameName", required = true) String gameName,
            @JsonProperty(value = "description", required = true) String description,
            @JsonProperty(value = "stackSize", required = true) int stackSize,
            @JsonProperty(value = "weight", required = true) float weight) {
        this.name = name;
        this.gameName = gameName;
        this.description = description;
        this.stackSize = stackSize;
        this.weight = weight;
    }
}
