package org.joelson.mattias.foundry.resource_calculator.model;

public class Item {

    private final String name;
    private final String gameName;
    private final int stackSize;
    private final float weight;

    public Item(String name, String gameName, int stackSize, float weight) {
        this.name = name;
        this.gameName = gameName;
        this.stackSize = stackSize;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public String getGameName() {
        return gameName;
    }

    public int getStackSize() {
        return stackSize;
    }

    public float getWeight() {
        return weight;
    }
}
