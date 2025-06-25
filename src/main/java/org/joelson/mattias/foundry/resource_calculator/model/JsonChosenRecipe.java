package org.joelson.mattias.foundry.resource_calculator.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joelson.mattias.foundry.resource_calculator.util.StringUtil;

record JsonChosenRecipe(
        String itemName,
        String recipeName) {

    @JsonCreator
    JsonChosenRecipe(
            @JsonProperty(value = "itemName", required = true) String itemName,
            @JsonProperty(value = "recipeName", required = true) String recipeName) {
        this.itemName = StringUtil.requireNotNullAndNotEmpty(itemName, "itemName is null or empty");
        this.recipeName = StringUtil.requireNotNullAndNotEmpty(recipeName, "recipeName is null or empty");
    }
}
