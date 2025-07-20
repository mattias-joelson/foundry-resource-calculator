package org.joelson.mattias.foundry.resource_calculator.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joelson.mattias.foundry.resource_calculator.util.StringUtil;

class JsonRobot extends JsonItem {

    private final int salesPrice;
    private final String category;

    @JsonCreator
    JsonRobot(
            @JsonProperty(value = "name", required = true) String name,
            @JsonProperty(value = "gameName", required = true) String gameName,
            @JsonProperty(value = "description", required = true) String description,
            @JsonProperty(value = "stackSize", required = true) int stackSize,
            @JsonProperty(value = "weight", required = true) float weight,
            @JsonProperty(value = "salesPrice", required = true) int salesPrice,
            @JsonProperty(value = "category", required = true) String category) {
        super(name, gameName, description, stackSize, weight);
        if (salesPrice <= 0) {
            throw new IllegalArgumentException("Sales price " + salesPrice + " is <= 0.");
        }
        this.salesPrice = salesPrice;
        this.category = StringUtil.requireNotNullAndNotEmpty(category, "category is null or empty");

    }

    public int getSalesPrice() {
        return salesPrice;
    }

    public String getCategory() {
        return category;
    }
}
