package org.joelson.mattias.foundry.resource_calculator.model;

import java.util.Map;
import java.util.Set;

record Recipe(
        String name,
        String gameName,
        Item item,
        Map<Item, Integer> ingredientAmounts,
        int itemsProduced,
        float time,
        Set<Maker> makers) {
}
