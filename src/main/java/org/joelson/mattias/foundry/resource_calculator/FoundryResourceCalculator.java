package org.joelson.mattias.foundry.resource_calculator;

import org.joelson.mattias.foundry.resource_calculator.model.JsonItem;
import org.joelson.mattias.foundry.resource_calculator.model.JsonMaker;
import org.joelson.mattias.foundry.resource_calculator.model.JsonReader;
import org.joelson.mattias.foundry.resource_calculator.model.JsonRecipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class FoundryResourceCalculator {

    private static final Logger logger = LoggerFactory.getLogger(FoundryResourceCalculator.class);

    private static final int ERROR_EXIT_STATUS = 1;

    public static void main(String[] args) throws IOException {
        if (args.length != 6) {
            exitWithError(String.format("""
                            Usage:
                            %s recipes.json jsonItems.json jsonMakers.json recipes.json goal_makers.json goal_recipe.json goals.json
                            
                            jsonItems.json        File containing item types in Foundry game.
                            jsonMakers.json       File containing jsonMakers for Foundry game jsonItems.
                            recipes.json      File containing recipes for Foundry game jsonItems.
                            goal_makers.json  File containing which kind of jsonMakers to use.
                            goal_recipe.json  File containing which recipe to use for jsonItems having multiple recipes.
                            goals.json        File containing production goals.""",
                    FoundryResourceCalculator.class));
        }

        JsonReader reader = new JsonReader();

        Map<String, JsonItem> jsonItems = reader.readJsonItems(Path.of(args[0]));
        Map<String, JsonMaker> jsonMakers = reader.readJsonMakers(Path.of(args[1]));
        Map<String, JsonRecipe> jsonRecipes = reader.readJsonRecipes(Path.of(args[2]));
    }

    private static void exitWithError(String msg) {
        logger.error(msg);
        System.exit(ERROR_EXIT_STATUS);
    }
}
