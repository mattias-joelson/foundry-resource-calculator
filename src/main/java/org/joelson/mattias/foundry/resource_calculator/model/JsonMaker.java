package org.joelson.mattias.foundry.resource_calculator.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joelson.mattias.foundry.resource_calculator.util.StringUtil;

record JsonMaker(
        String name,
        String gameName,
        String description,
        float speedMultiplier) {

    @JsonCreator
    public JsonMaker(
            @JsonProperty(value = "name", required = true) String name,
            @JsonProperty(value = "gameName", required = true) String gameName,
            @JsonProperty(value = "description", required = true) String description,
            @JsonProperty(value = "speedMultiplier", required = true) float speedMultiplier) {
        this.name = StringUtil.requireNotNullAndNotEmpty(name, "name is null or empty");
        this.gameName = StringUtil.requireNotNullAndNotEmpty(gameName, "gameName is null or empty");
        this.description = StringUtil.requireNotNull(description, "description is null");
        if (speedMultiplier <= 0) {
            throw new IllegalArgumentException("speedMultiplier must be larger than 0: " + speedMultiplier);
        }
        this.speedMultiplier = speedMultiplier;
    }
}
