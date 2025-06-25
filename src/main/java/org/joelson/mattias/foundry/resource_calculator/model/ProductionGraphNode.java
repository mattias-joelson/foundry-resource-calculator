package org.joelson.mattias.foundry.resource_calculator.model;

import java.util.Objects;

class ProductionGraphNode {

    private final Item item;
    private final Recipe recipe;
    private final Maker maker;
    private float itemsPerMinute = 0;

    ProductionGraphNode(Item item, Recipe recipe, Maker maker) {
        this.item = Objects.requireNonNull(item);
        this.recipe = recipe;
        this.maker = maker;

        if (recipe != null) {
            Objects.requireNonNull(maker);
        } else if (maker != null) {
            throw new NullPointerException();
        }
    }

    public Item getItem() {
        return item;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public Maker getMaker() {
        return maker;
    }

    public float getItemsPerMinute() {
        return itemsPerMinute;
    }

    public void addItemsPerMinute(float itemsPerMinute) {
        this.itemsPerMinute += itemsPerMinute;
    }
}
