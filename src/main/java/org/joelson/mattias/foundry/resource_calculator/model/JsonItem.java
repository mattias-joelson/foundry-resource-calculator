package org.joelson.mattias.foundry.resource_calculator.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joelson.mattias.foundry.resource_calculator.util.StringUtil;

class JsonItem {

    private final String name;
    private final String gameName;
    private final String description;
    private final int stackSize;
    private final float weight;

    @JsonCreator
    JsonItem(
            @JsonProperty(value = "name", required = true) String name,
            @JsonProperty(value = "gameName", required = true) String gameName,
            @JsonProperty(value = "description", required = true) String description,
            @JsonProperty(value = "stackSize") int stackSize,
            @JsonProperty(value = "weight") float weight) {
        this.name = StringUtil.requireNotNullAndNotEmpty(name, "name is null or empty");
        this.gameName = StringUtil.requireNotNullAndNotEmpty(gameName, "gameName is null or empty");
        this.description = StringUtil.requireNotNull(description, "description is null");
        if (stackSize < 0) {
            throw new IllegalArgumentException("stackSize must be greater or equal to 0: " + stackSize);
        }
        this.stackSize = stackSize;
        if (weight < 0) {
            throw new IllegalArgumentException("weight must be greater or equal to 0: " + weight);
        }
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public String getGameName() {
        return gameName;
    }

    public String getDescription() {
        return description;
    }

    public int getStackSize() {
        return stackSize;
    }

    public float getWeight() {
        return weight;
    }
}
