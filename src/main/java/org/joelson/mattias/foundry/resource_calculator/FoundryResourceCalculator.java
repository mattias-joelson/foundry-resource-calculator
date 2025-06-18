package org.joelson.mattias.foundry.resource_calculator;

import org.joelson.mattias.foundry.resource_calculator.model.JsonCalculatorConfig;
import org.joelson.mattias.foundry.resource_calculator.model.JsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

public class FoundryResourceCalculator {

    private static final Logger logger = LoggerFactory.getLogger(FoundryResourceCalculator.class);

    private static final int ERROR_EXIT_STATUS = 1;

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            exitWithError(String.format("""
                            Usage:
                            %s calculator_config.json goals.json
                            
                            calculator_config.json  File containing items, recipes and makers in the Foundry game
                            goals.json        File containing production goals.""",
                    FoundryResourceCalculator.class));
        }

        JsonReader reader = new JsonReader();
        JsonCalculatorConfig jsonCalculatorConfig = reader.readJsonCalculatorConfig(Path.of(args[0]));
        logger.info(jsonCalculatorConfig.toString());
    }

    private static void exitWithError(String msg) {
        logger.error(msg);
        System.exit(ERROR_EXIT_STATUS);
    }
}
