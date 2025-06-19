package org.joelson.mattias.foundry.resource_calculator;

import org.joelson.mattias.foundry.resource_calculator.model.CalculatorConfig;
import org.joelson.mattias.foundry.resource_calculator.model.CalculatorGoals;
import org.joelson.mattias.foundry.resource_calculator.model.ProductionGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

public class FoundryResourceCalculator {

    private static final Logger logger = LoggerFactory.getLogger(FoundryResourceCalculator.class);

    private static final int ERROR_EXIT_STATUS = 1;

    private final CalculatorConfig calculatorConfig;
    private final CalculatorGoals calculatorGoals;

    public FoundryResourceCalculator(CalculatorConfig calculatorConfig, CalculatorGoals calculatorGoals) {
        this.calculatorConfig = Objects.requireNonNull(calculatorConfig);
        this.calculatorGoals = Objects.requireNonNull(calculatorGoals);
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            exitWithError(String.format("""
                            Usage:
                            %s calculator_config.json goals.json
                            
                            calculator_config.json  File containing items, recipes and makers in the Foundry game
                            calculator_goals.json   File containing production goals.""",
                    FoundryResourceCalculator.class));
        }

        CalculatorConfig calculatorConfig = CalculatorConfig.fromPath(Path.of(args[0]));
        CalculatorGoals calculatorGoals = CalculatorGoals.fromPath(Path.of(args[1]), calculatorConfig);

        new FoundryResourceCalculator(calculatorConfig, calculatorGoals).calculate();
    }

    private static void exitWithError(String msg) {
        logger.error(msg);
        System.exit(ERROR_EXIT_STATUS);
    }

    private void calculate() {
        ProductionGraph productionGraph = ProductionGraph.from(calculatorConfig);

        productionGraph.addGoals(calculatorGoals.getProductionGoals());
        productionGraph.calculateProduction();
    }
}
