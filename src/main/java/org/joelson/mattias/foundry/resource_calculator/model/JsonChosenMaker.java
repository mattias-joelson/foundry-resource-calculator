package org.joelson.mattias.foundry.resource_calculator.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joelson.mattias.foundry.resource_calculator.util.StringUtil;

record JsonChosenMaker(
        String makerGroupName,
        String makerName) {

    @JsonCreator
    public JsonChosenMaker(
            @JsonProperty(value = "makerGroupName", required = true) String makerGroupName,
            @JsonProperty(value = "makerName", required = true) String makerName) {
        this.makerGroupName = StringUtil.requireNotNullAndNotEmpty(makerGroupName, "makerGroupName is null or empty");
        this.makerName = StringUtil.requireNotNullAndNotEmpty(makerName, "makerGroupName is null or empty");
    }
}
