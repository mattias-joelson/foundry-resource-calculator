package org.joelson.mattias.foundry.resource_calculator.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joelson.mattias.foundry.resource_calculator.util.StringUtil;

record JsonProductionGoal(
        String itemName,
        float amount) {

    @JsonCreator
    JsonProductionGoal(
            @JsonProperty(value = "itemName", required = true) String itemName,
            @JsonProperty(value = "amount", required = true) float amount) {
        this.itemName = StringUtil.requireNotNullAndNotEmpty(itemName, "itemName is null or empty");
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be greater than 0: " + amount);
        }
        this.amount = amount;
    }
}
