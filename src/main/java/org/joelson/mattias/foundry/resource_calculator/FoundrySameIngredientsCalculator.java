package org.joelson.mattias.foundry.resource_calculator;

import org.joelson.mattias.foundry.resource_calculator.model.CalculatorConfig;
import org.joelson.mattias.foundry.resource_calculator.model.CalculatorGoals;
import org.joelson.mattias.foundry.resource_calculator.model.ProductionGraph;
import org.joelson.mattias.foundry.resource_calculator.model.SameIngredientsCalculator;

import java.io.IOException;
import java.nio.file.Path;

public class FoundrySameIngredientsCalculator {

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            exitWithError(String.format("""
                            Usage:
                            %s calculator_config.json calculator_goals.json
                            
                            calculator_config.json  File containing items, recipes and makers in the Foundry game
                            calculator_goals.json   File containing production goals.""",
                    FoundrySameIngredientsCalculator.class));
        }

        CalculatorConfig calculatorConfig = CalculatorConfig.fromPath(Path.of(args[0]));
        CalculatorGoals calculatorGoals = CalculatorGoals.fromPath(Path.of(args[1]), calculatorConfig);

        new FoundrySameIngredientsCalculator().calculateSameIngredients(calculatorConfig, calculatorGoals);
    }

    private static void exitWithError(String msg) {
        System.err.println(msg);
        System.exit(-1);
    }

    private void calculateSameIngredients(CalculatorConfig calculatorConfig, CalculatorGoals calculatorGoals) {

        ProductionGraph productionGraph = ProductionGraph.from(calculatorConfig, calculatorGoals);
        new SameIngredientsCalculator(calculatorConfig, productionGraph).calculateSameIngredients();
    }
}
