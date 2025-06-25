package org.joelson.mattias.foundry.resource_calculator.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joelson.mattias.foundry.resource_calculator.util.StringUtil;

record JsonItemAmount(
        String itemName,
        int amount) {

    @JsonCreator
    JsonItemAmount(
            @JsonProperty(value = "itemName", required = true) String itemName,
            @JsonProperty(value = "amount", required = true) int amount) {
        this.itemName = StringUtil.requireNotNullAndNotEmpty(itemName, "itemName is null or empty");
        if (amount < 1) {
            throw new IllegalArgumentException("amount be greater or equal to 1: " + amount);
        }
        this.amount = amount;
    }
}
