package org.joelson.mattias.foundry.resource_calculator.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joelson.mattias.foundry.resource_calculator.util.ListUtil;

import java.util.List;

record JsonCalculatorConfig(
        List<JsonMaker> makers,
        List<JsonMakerGroup> makerGroups,
        List<JsonItem> items,
        List<JsonRecipe> recipes) {

    @JsonCreator
    JsonCalculatorConfig(
            @JsonProperty(value = "makers", required = true) List<JsonMaker> makers,
            @JsonProperty(value = "makerGroups", required = true) List<JsonMakerGroup> makerGroups,
            @JsonProperty(value = "items", required = true) List<JsonItem> items,
            @JsonProperty(value = "recipes", required = true) List<JsonRecipe> recipes) {
        this.makers = ListUtil.requireUniqueMembers(makers, JsonMaker::name);
        this.makerGroups = ListUtil.requireUniqueMembers(makerGroups, JsonMakerGroup::groupName);
        this.items = ListUtil.requireUniqueMembers(items, JsonItem::name);
        this.recipes = ListUtil.requireUniqueMembers(recipes, JsonRecipe::name);
    }
}
