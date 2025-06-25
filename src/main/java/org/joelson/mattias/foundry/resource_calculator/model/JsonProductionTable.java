package org.joelson.mattias.foundry.resource_calculator.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joelson.mattias.foundry.resource_calculator.util.ListUtil;

import java.util.List;

record JsonProductionTable(
        List<String> itemNameColumns,
        List<String> itemNameRows) {

    @JsonCreator
    JsonProductionTable(
            @JsonProperty(value = "itemNameColumns", required = true) List<String> itemNameColumns,
            @JsonProperty(value = "itemNameRows", required = true) List<String> itemNameRows) {
        this.itemNameColumns = ListUtil.requireUniqueMembers(itemNameColumns);
        this.itemNameRows = ListUtil.requireUniqueMembers(itemNameRows);
    }
}
