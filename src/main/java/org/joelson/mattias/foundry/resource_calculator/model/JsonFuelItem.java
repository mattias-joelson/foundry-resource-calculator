package org.joelson.mattias.foundry.resource_calculator.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joelson.mattias.foundry.resource_calculator.util.StringUtil;

class JsonFuelItem extends JsonItem {

    private final float fuelValue;
    private final String residualItem;

    @JsonCreator
    JsonFuelItem(
            @JsonProperty(value = "name", required = true) String name,
            @JsonProperty(value = "gameName", required = true) String gameName,
            @JsonProperty(value = "description", required = true) String description,
            @JsonProperty(value = "stackSize", required = true) int stackSize,
            @JsonProperty(value = "weight") float weight,
            @JsonProperty(value = "fuelValue", required = true) float fuelValue,
            @JsonProperty(value = "residualItem") String residualItem) {
        super(name, gameName, description, stackSize, weight);
        if (fuelValue < 0) {
            throw new IllegalArgumentException("fuelValue must be greater or equal to 0:" + fuelValue);
        }
        this.fuelValue = fuelValue;
        this.residualItem = StringUtil.requireNullOrNotEmpty(residualItem, "residual is empty");
    }

    public float getFuelValue() {
        return fuelValue;
    }

    public String getResidualItem() {
        return residualItem;
    }
}
