package org.joelson.mattias.foundry.resource_calculator.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joelson.mattias.foundry.resource_calculator.util.StringUtil;

record JsonChosenConveyor(
        String conveyorItemName,
        int conveyorThroughput) {

    @JsonCreator
    JsonChosenConveyor(
            @JsonProperty(value = "conveyorItemName", required = true) String conveyorItemName,
            @JsonProperty(value = "conveyorThroughput", required = true) int conveyorThroughput) {
        this.conveyorItemName = StringUtil.requireNotNullAndNotEmpty(conveyorItemName,
                "conveyorItemName is null or empty");
        if (conveyorThroughput < 1) {
            throw new IllegalArgumentException("conveyorThroughput is below 1: " + conveyorThroughput);
        }
        this.conveyorThroughput = conveyorThroughput;
    }
}
