package com.apex.model;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Data
@Builder
public class Recipe {
    private static final Logger LOGGER = LogManager.getLogger(Recipe.class);
    
    private String name;
    private String nameID;

    @SerializedName("skill")
    private String skillNameID;

    private int level;
    private int labor;

    @SerializedName("craftingTable")
    private String craftingTableNameID;
    private boolean hidden;
    private List<Ingredient> ingredients;
    private List<Output> outputs;

    public String toReadableString() {
        StringBuilder result = new StringBuilder(name + " (" + nameID + ")\n");
        result.append(labor).append(" calories of level ").append(level).append(" ").append(skillNameID.replace("Skill", ""))
                .append(" labor at ").append(craftingTableNameID.replace("Object", "")).append("\n");
        result.append("Ingredients:\n");
        for (Ingredient ingredient : ingredients) {
            result.append(ingredient).append("\n");
        }
        result.append("Produces:\n");
        for (Output output : outputs) {
            result.append(output).append("\n");
        }

        return result.toString();
    }

    public static boolean recipesAreEqual(Recipe oldRecipe, Recipe newRecipe) {
        if (!oldRecipe.equals(newRecipe)) {
            if (!oldRecipe.name.equals(newRecipe.name)) {
                logDiff("name", oldRecipe.name, newRecipe.name, newRecipe.name);
            }
            if (!oldRecipe.skillNameID.equals(newRecipe.skillNameID)) {
                logDiff("skill", oldRecipe.skillNameID, newRecipe.skillNameID, newRecipe.name);
            }
            if (oldRecipe.level != newRecipe.level) {
                logDiff("level", oldRecipe.level, newRecipe.level, newRecipe.name);
            }
            if (oldRecipe.labor != newRecipe.labor) {
                logDiff("labor", oldRecipe.labor, newRecipe.labor, newRecipe.name);
            }
            if (!oldRecipe.craftingTableNameID.equals(newRecipe.craftingTableNameID)) {
                logDiff("craftingTable", oldRecipe.craftingTableNameID, newRecipe.craftingTableNameID, newRecipe.name);
            }
            oldRecipe.ingredients.sort(Comparator.comparing(Ingredient::getItemNameID));
            newRecipe.ingredients.sort(Comparator.comparing(Ingredient::getItemNameID));
            if (!oldRecipe.ingredients.equals(newRecipe.ingredients)) {
                List<String> oldIngredients = oldRecipe.ingredients.stream().map(Ingredient::toReadableString).toList();
                List<String> newIngredients = newRecipe.ingredients.stream().map(Ingredient::toReadableString).toList();
                logDiff("ingredients", oldIngredients, newIngredients, newRecipe.name);
            }
            oldRecipe.outputs.sort(Comparator.comparing(Output::getItemNameID));
            newRecipe.outputs.sort(Comparator.comparing(Output::getItemNameID));
            if (!oldRecipe.outputs.equals(newRecipe.outputs)) {
                List<String> oldOutputs = oldRecipe.outputs.stream().map(Output::toReadableString).toList();
                List<String> newOutputs = newRecipe.outputs.stream().map(Output::toReadableString).toList();
                logDiff("outputs", oldOutputs, newOutputs, newRecipe.name);
            }
            return true;
        }

        return false;
    }

    private static void logDiff(String propName, List<?> old, List<?> newer, String recipeName) {
        LOGGER.info(recipeName + " " + propName + ": " + Arrays.toString(old.toArray()) + " -> " + Arrays.toString(newer.toArray()));
    }

    private static void logDiff(String propName, Object old, Object newer, String recipeName) {
        LOGGER.info(recipeName + " " + propName + ": " + old + " -> " + newer);
    }


}
