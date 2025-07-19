package org.joelson.mattias.foundry.resource_calculator.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joelson.mattias.foundry.resource_calculator.util.StringUtil;

record JsonItem(
        String name,
        String gameName,
        String description,
        int stackSize,
        float weight,
        float fuelValue,
        String residual) {

    @JsonCreator
    JsonItem(
            @JsonProperty(value = "name", required = true) String name,
            @JsonProperty(value = "gameName", required = true) String gameName,
            @JsonProperty(value = "description", required = true) String description,
            @JsonProperty(value = "stackSize") int stackSize,
            @JsonProperty(value = "weight") float weight,
            @JsonProperty(value = "fuelValue") float fuelValue,
            @JsonProperty(value = "residual") String residual) {
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
        if (fuelValue < 0) {
            throw new IllegalArgumentException("fuelValue must be greater or equal to 0:" + fuelValue);
        }
        this.fuelValue = fuelValue;
        this.residual = StringUtil.requireNullOrNotEmpty(residual, "residual is empty");
    }
}
