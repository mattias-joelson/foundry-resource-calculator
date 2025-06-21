package org.joelson.mattias.foundry.resource_calculator.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joelson.mattias.foundry.resource_calculator.util.StringUtil;

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
        this.name = StringUtil.requireNotNullAndNotEmpty(name, "name is null or empty");
        this.gameName = StringUtil.requireNotNullAndNotEmpty(gameName, "gameName is null or empty");
        this.description = StringUtil.requireNotNull(description, "description is null");
        if (stackSize < 0) {
            throw new IllegalArgumentException("stackSize must be greater or equal to 0: " + stackSize);
        }
        this.stackSize = stackSize;
        if (weight < 0) {
            throw new IllegalArgumentException("weight must be greater or equal to 0: " + weight);
        }
        this.weight = weight;
    }
}
