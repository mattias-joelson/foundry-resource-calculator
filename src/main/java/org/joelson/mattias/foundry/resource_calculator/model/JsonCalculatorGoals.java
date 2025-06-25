package org.joelson.mattias.foundry.resource_calculator.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joelson.mattias.foundry.resource_calculator.util.ListUtil;

import java.util.List;
import java.util.Objects;

record JsonCalculatorGoals(
        List<JsonChosenRecipe> chosenRecipes,
        List<JsonChosenMaker> chosenMakers,
        JsonChosenConveyor chosenConveyor,
        List<JsonProductionGoal> productionGoals,
        JsonProductionTable productionTable) {

    @JsonCreator
    JsonCalculatorGoals(
            @JsonProperty(value = "chosenRecipes", required = true) List<JsonChosenRecipe> chosenRecipes,
            @JsonProperty(value = "chosenMakers", required = true) List<JsonChosenMaker> chosenMakers,
            @JsonProperty(value = "chosenConveyor", required = true) JsonChosenConveyor chosenConveyor,
            @JsonProperty(value = "productionGoals", required = true) List<JsonProductionGoal> productionGoals,
            @JsonProperty(value = "productionTable") JsonProductionTable productionTable) {
        this.chosenRecipes = ListUtil.requireUniqueMembers(chosenRecipes, JsonChosenRecipe::itemName);
        this.chosenMakers = ListUtil.requireUniqueMembers(chosenMakers, JsonChosenMaker::makerGroupName);
        this.chosenConveyor = Objects.requireNonNull(chosenConveyor);
        this.productionGoals = ListUtil.requireUniqueMembers(productionGoals, JsonProductionGoal::itemName);
        this.productionTable = productionTable;
    }
}
