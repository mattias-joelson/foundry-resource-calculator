package org.joelson.mattias.foundry.resource_calculator.model;

public class Robot extends Item {

    private final int salesPrice;
    private final String category;

    public Robot(String name, String gameName, int stackSize, float weight, int salesPrice, String category) {
        super(name, gameName, stackSize, weight);
        this.salesPrice = salesPrice;
        this.category = category;
    }

    public int getSalesPrice() {
        return salesPrice;
    }

    public String getCategory() {
        return category;
    }
}
