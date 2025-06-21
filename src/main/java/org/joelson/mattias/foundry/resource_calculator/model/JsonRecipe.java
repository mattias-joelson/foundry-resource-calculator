package org.joelson.mattias.foundry.resource_calculator.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joelson.mattias.foundry.resource_calculator.util.ListUtil;
import org.joelson.mattias.foundry.resource_calculator.util.StringUtil;

import java.util.List;

record JsonRecipe(
        String name,
        String gameName,
        String description,
        String itemName,
        List<JsonItemAmount> ingredients,
        int itemsProduced,
        float time,
        String makerGroupName) {

    @JsonCreator
    public JsonRecipe(
            @JsonProperty(value = "name", required = true) String name,
            @JsonProperty(value = "gameName", required = true) String gameName,
            @JsonProperty(value = "description", required = true) String description,
            @JsonProperty(value = "itemName", required = true) String itemName,
            @JsonProperty(value = "ingredientAmounts", required = true) List<JsonItemAmount> ingredients,
            @JsonProperty(value = "itemsProduced", required = true) int itemsProduced,
            @JsonProperty(value = "time", required = true) float time,
            @JsonProperty(value = "makerGroupName", required = true) String makerGroupName) {
        this.name = StringUtil.requireNotNullAndNotEmpty(name, "name is null or empty");
        this.gameName = StringUtil.requireNotNullAndNotEmpty(gameName, "gameName is null or empty");
        this.description = StringUtil.requireNotNull(description, "description is null");
        this.itemName = StringUtil.requireNotNullAndNotEmpty(itemName, "itemName is null or empty");
        this.ingredients = ListUtil.requireUniqueMembers(ingredients, JsonItemAmount::itemName);
        if (itemsProduced < 1) {
            throw new IllegalArgumentException("itemsProduced be greater or equal to 1: " + itemsProduced);
        }
        this.itemsProduced = itemsProduced;
        if (time <= 0) {
            throw new IllegalArgumentException("time be greater than 0: " + time);
        }
        this.time = time;
        this.makerGroupName = StringUtil.requireNotNullAndNotEmpty(makerGroupName, "makerGroupName is null or empty");
    }
}
