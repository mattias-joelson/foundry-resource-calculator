package org.joelson.mattias.foundry.resource_calculator.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

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
        this.name = name;
        this.gameName = gameName;
        this.description = description;
        this.speedMultiplier = speedMultiplier;
    }
}
