package org.joelson.mattias.foundry.resource_calculator.model;

public record Item(
        String name,
        String gameName,
        int stackSize,
        float weight,
        float fuelValue) {
}
