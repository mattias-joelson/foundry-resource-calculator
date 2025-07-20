package org.joelson.mattias.foundry.resource_calculator.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class SameIngredientsCalculator {

    private static final String[] ITEMS_TO_CHECK = {
            "concrete", "jetpack-fuel", "construction-material",
            "power-line", "explosive-charge",
            "building-block", "elevator", "walkway", "shipping-pad-medium", "shipping-pad-medium-assembly-line",
            "conveyor-1", "conveyor-2", "conveyor-3", "conveyor-4",
            "conveyor-slope-1", "conveyor-slope-2", "conveyor-slope-3", "conveyor-slope-4",
            "conveyor-balancer-1", "conveyor-balancer-2", "conveyor-balancer-3", "conveyor-balancer-4",
            "freight-elevator-1", "freight-elevator-2", "freight-elevator-3", "freight-elevator-4",
            "pipe", "pipeline",
            "cargo-shuttle-start-pad", "cargo-shuttle-target-pad", "liquids-cargo-shuttle-start-pad",
            "liquids-cargo-shuttle-target-pad",
            "loader-1", "loader-2", "loader-3", "filter-loader",
            "boiler", "steam-turbine", "solar-panel-small", "solar-panel-large", "battery-small", "battery-large",
            "power-pole-1", "power-pole-2", "transformer-small", "transformer-large",
            "drone-miner-1", "drone-miner-2", "pumpjack-1", "ore-vein-miner",
            "assembler-1", "assembler-2", "assembler-3", "fluid-assembler-1", "advanced-smelter",
            "electric-arc-furnace",
            "small-smelter", "lava-smelter-1", "lava-smelter-2",
            "crusher-1", "crusher-2", "chemical-processor", "distillation-column", "casting-machine", "flare-stack",
            "logistic-container-1", "logistic-container-2", "logistic-container-3", "tank",
            "transport-ship-port", "construction-ship-port", "construction-warehouse",
            "assembly-line-rail", "assembly-line-splitter", "assembly-line-merger", "assembly-line-start",
            "assembly-line-producer", "assembly-line-painter",
            "data-cable", "data-evaluator", "data-memory-cell", "data-processor", "data-source", "data-source-button",
            "data-source-lever",
            "long-range-scanner-base", "radio-tower-base", "modular-storage-tank-base", "fracking-tower-base",
            "air-intake-base", "hot-air-stove-base", "blast-furnace-base"
    };

    private static final Map<String, String> PREV_PART = new HashMap<>();

    static {
        PREV_PART.put("conveyor-2", "conveyor-1");
        PREV_PART.put("conveyor-3", "conveyor-2");
        PREV_PART.put("conveyor-4", "conveyor-3");
        PREV_PART.put("conveyor-slope-1", "conveyor-1");
        PREV_PART.put("conveyor-slope-2", "conveyor-slope-1");
        PREV_PART.put("conveyor-slope-3", "conveyor-slope-2");
        PREV_PART.put("conveyor-slope-4", "conveyor-slope-3");
        PREV_PART.put("conveyor-balancer-1", "conveyor-1");
        PREV_PART.put("conveyor-balancer-2", "conveyor-balancer-1");
        PREV_PART.put("conveyor-balancer-3", "conveyor-balancer-2");
        PREV_PART.put("conveyor-balancer-4", "conveyor-balancer-3");
        PREV_PART.put("freight-elevator-2", "freight-elevator-1");
        PREV_PART.put("freight-elevator-3", "freight-elevator-2");
        PREV_PART.put("freight-elevator-4", "freight-elevator-3");
        PREV_PART.put("drone-miner-2", "drone-miner-1");
        PREV_PART.put("assembler-2", "assembler-1");
        PREV_PART.put("assembler-3", "assembler-2");
        PREV_PART.put("lava-smelter-2", "lava-smelter-1");
        PREV_PART.put("crusher-2", "crusher-1");
    }

    private final CalculatorConfig calculatorConfig;
    private final ProductionGraph productionGraph;

    public SameIngredientsCalculator(CalculatorConfig calculatorConfig, ProductionGraph productionGraph) {
        this.calculatorConfig = Objects.requireNonNull(calculatorConfig);
        this.productionGraph = Objects.requireNonNull(productionGraph);
    }

    public void calculateSameIngredients() {
        Map<Set<String>, Set<String>> itemsWithSameIngredients = new HashMap<>();
        Set<String> itemsToCheck = Arrays.stream(ITEMS_TO_CHECK).collect(Collectors.toSet());
        for (String itemName : itemsToCheck) {
            if (calculatorConfig.getItem(itemName) == null) {
                throw new IllegalArgumentException("Unknown item with name " + itemName);
            }
        }

        Set<String> uniqueIngredients = new HashSet<>();

        next_item:
        for (Item item : calculatorConfig.getItems()) {
            if (!itemsToCheck.contains(item.getName())) {
                continue;
            }
            Set<String> itemIngredients = ingredientsFor(productionGraph, item);
            uniqueIngredients.addAll(itemIngredients);
//            if (PREV_PART.containsKey(item.name())) {
//                itemIngredients.remove(PREV_PART.get(item.name()));
//            }
            for (Map.Entry<Set<String>, Set<String>> ingredientsItemsEntry : itemsWithSameIngredients.entrySet()) {
                if (ingredientsItemsEntry.getKey().equals(itemIngredients)) {
                    ingredientsItemsEntry.getValue().add(item.getName());
                    continue next_item;
                }
            }
            Set<String> itemNames = new HashSet<>();
            itemNames.add(item.getName());
            itemsWithSameIngredients.put(itemIngredients, itemNames);
        }

        List<String> orderedIngredients = uniqueIngredients.stream().toList().stream().sorted().toList();

        System.out.printf("%d different items to check.%n", itemsToCheck.size());
        System.out.printf("%d unique ingredients.%n", uniqueIngredients.size());
        System.out.printf("%d different ingredients combinations.%n", itemsWithSameIngredients.size());

        int maximumIngredients = Integer.MIN_VALUE;
        for (Map.Entry<Set<String>, Set<String>> ingredientsEntry : itemsWithSameIngredients.entrySet()) {
            maximumIngredients = Math.max(maximumIngredients, ingredientsEntry.getKey().size());
        }
        System.out.printf("%d maximum number of ingredients.%n", maximumIngredients);

        ArrayList<Set<Map.Entry<Set<String>, Set<String>>>> list = new ArrayList<>(maximumIngredients + 1);
        for (int i = 0; i <= maximumIngredients; i += 1) {
            list.add(new HashSet<>());
        }
        for (Map.Entry<Set<String>, Set<String>> ingredientsEntry : itemsWithSameIngredients.entrySet()) {
            int ingredients = ingredientsEntry.getKey().size();
            list.get(ingredients).add(ingredientsEntry);
        }

        System.out.print("recipe");
        for (String ingredientName : orderedIngredients) {
            System.out.printf(";%s", ingredientName);
        }
        System.out.println();
        for (Set<Map.Entry<Set<String>, Set<String>>> group : list) {
            for (Map.Entry<Set<String>, Set<String>> sameIngredients : group) {
                Set<String> ingredients = sameIngredients.getKey();
                for (String recipe : sameIngredients.getValue()) {
                    System.out.print(recipe);
                    for (String ingredientName : orderedIngredients) {
                        System.out.print(";");
                        if (ingredients.contains(ingredientName)) {
                            System.out.print(1);
                        }
                    }
                    System.out.println();
                }
            }
        }


    }

    private Set<String> ingredientsFor(ProductionGraph productionGraph, Item item) {
        ProductionGraphNode productionGraphNode = productionGraph.getProductionGraphNode(item);
        if (productionGraphNode == null) {
            return Collections.emptySet();
        }
        Recipe recipe = productionGraphNode.getRecipe();
        if (recipe == null) {
            return Collections.emptySet();
        }
        Set<String> ingredientNames = new HashSet<>();
        for (Item ingredient : recipe.ingredientAmounts().keySet()) {
            ingredientNames.add(ingredient.getName());
        }
        return ingredientNames;
    }
}
