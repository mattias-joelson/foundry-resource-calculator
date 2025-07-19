package org.joelson.mattias.foundry.resource_calculator.model;

public class FuelItem extends Item {

    private final float fuelValue;
    private final Item residualItem;

    public FuelItem(String name, String gameName, int stackSize, float weight, float fuelValue, Item residualItem) {
        super(name, gameName, stackSize, weight);
        this.fuelValue = fuelValue;
        this.residualItem = residualItem;
    }

    public float getFuelValue() {
        return fuelValue;
    }

    public Item getResidualItem() {
        return residualItem;
    }
}
