package org.joelson.mattias.foundry.resource_calculator.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joelson.mattias.foundry.resource_calculator.util.ListUtil;

import java.util.List;

record JsonCalculatorGoals(
        List<JsonChosenRecipe> chosenRecipes,
        List<JsonChosenMaker> chosenMakers,
        List<JsonItemAmount> productionGoals,
        JsonProductionTable productionTable) {

    @JsonCreator
    public JsonCalculatorGoals(
            @JsonProperty(value = "chosenRecipes", required = true) List<JsonChosenRecipe> chosenRecipes,
            @JsonProperty(value = "chosenMakers", required = true) List<JsonChosenMaker> chosenMakers,
            @JsonProperty(value = "productionGoals", required = true) List<JsonItemAmount> productionGoals,
            @JsonProperty(value = "productionTable") JsonProductionTable productionTable) {
        this.chosenRecipes = ListUtil.requireUniqueMembers(chosenRecipes, JsonChosenRecipe::itemName);
        this.chosenMakers = ListUtil.requireUniqueMembers(chosenMakers, JsonChosenMaker::makerGroupName);
        this.productionGoals = ListUtil.requireUniqueMembers(productionGoals, JsonItemAmount::itemName);
        this.productionTable = productionTable;
    }
}
