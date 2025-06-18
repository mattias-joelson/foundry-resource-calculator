package org.joelson.mattias.foundry.resource_calculator.model;

import java.util.Map;
import java.util.Set;

public record Recipe(
        String name,
        String gameName,
        Item item,
        Map<Item, Integer> ingredients,
        int itemsProduced,
        int time,
        Set<Maker> makers) {
}
