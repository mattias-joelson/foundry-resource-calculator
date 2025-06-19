package org.joelson.mattias.foundry.resource_calculator.model;

class ProductionGraphNode {

    private final Item item;
    private final Recipe recipe;
    private float itemsPerMinute = 0;

    public ProductionGraphNode(Item item, Recipe recipe) {
        this.item = item;
        this.recipe = recipe;
    }

    public Item getItem() {
        return item;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public float getItemsPerMinute() {
        return itemsPerMinute;
    }

    public void addItemsPerMinute(float itemsPerMinute) {
        this.itemsPerMinute += itemsPerMinute;
    }
}
