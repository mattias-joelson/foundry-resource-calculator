package org.joelson.mattias.foundry.resource_calculator.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joelson.mattias.foundry.resource_calculator.util.ListUtil;

import java.util.List;

record JsonCalculatorConfig(
        List<JsonMaker> makers,
        List<JsonMakerGroup> makerGroups,
        List<JsonItem> items,
        List<JsonFuelItem> fuelItems,
        List<JsonHandheld> handhelds,
        List<JsonRecipe> recipes) {

    @JsonCreator
    JsonCalculatorConfig(
            @JsonProperty(value = "makers", required = true) List<JsonMaker> makers,
            @JsonProperty(value = "makerGroups", required = true) List<JsonMakerGroup> makerGroups,
            @JsonProperty(value = "items", required = true) List<JsonItem> items,
            @JsonProperty(value = "fuelItems", required = true) List<JsonFuelItem> fuelItems,
            @JsonProperty(value = "handhelds", required = true) List<JsonHandheld> handhelds,
            @JsonProperty(value = "recipes", required = true) List<JsonRecipe> recipes) {
        this.makers = ListUtil.requireUniqueMembers(makers, JsonMaker::name);
        this.makerGroups = ListUtil.requireUniqueMembers(makerGroups, JsonMakerGroup::groupName);
        this.items = ListUtil.requireUniqueMembers(items, JsonItem::getName);
        this.fuelItems = ListUtil.requireUniqueMembers(fuelItems, JsonItem::getName);
        this.handhelds = ListUtil.requireUniqueMembers(handhelds, JsonItem::getName);
        this.recipes = ListUtil.requireUniqueMembers(recipes, JsonRecipe::name);
    }
}
