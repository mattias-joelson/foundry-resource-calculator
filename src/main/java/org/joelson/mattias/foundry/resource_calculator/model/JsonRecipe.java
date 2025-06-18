package org.joelson.mattias.foundry.resource_calculator.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

record JsonRecipe(
        String name,
        String gameName,
        String description,
        String itemName,
        Map<String, Integer> ingredients,
        int itemsProduced,
        int time,
        List<String> makerNames) {

    @JsonCreator
    public JsonRecipe(
            @JsonProperty(value = "name", required = true) String name,
            @JsonProperty(value = "gameName", required = true) String gameName,
            @JsonProperty(value = "description", required = true) String description,
            @JsonProperty(value = "itemName", required = true) String itemName,
            @JsonProperty(value = "ingredients", required = true) Map<String, Integer> ingredients,
            @JsonProperty(value = "itemsProduced", required = true) int itemsProduced,
            @JsonProperty(value = "time", required = true) int time,
            @JsonProperty(value = "makerNames", required = true) List<String> makerNames) {
        this.name = name;
        this.gameName = gameName;
        this.description = description;
        this.itemName = itemName;
        this.ingredients = ingredients;
        this.itemsProduced = itemsProduced;
        this.time = time;
        this.makerNames = makerNames;
    }
}
