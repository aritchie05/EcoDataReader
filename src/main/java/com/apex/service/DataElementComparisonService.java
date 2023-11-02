package com.apex.service;

import com.apex.model.Item;
import com.apex.model.Recipe;
import com.apex.model.comparison.ItemComparisonResult;
import com.apex.model.comparison.RecipeComparisonResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static com.apex.model.Recipe.recipesAreEqual;

public class DataElementComparisonService {

    private static final Logger LOGGER = LogManager.getLogger(DataElementComparisonService.class);

    public DataElementComparisonService() {
    }

    public RecipeComparisonResult analyzeRecipeDifferences(List<Recipe> recipesFromCraftingTool, List<Recipe> recipesFromEcoServer) {
        List<Recipe> updatedRecipes = new ArrayList<>();

        for (Recipe oldRecipe : recipesFromCraftingTool) {
            Recipe matchingNewRecipe = null;
            for (Recipe newRecipe : recipesFromEcoServer) {
                if (oldRecipe.getNameID().equals(newRecipe.getNameID())) {
                    matchingNewRecipe = newRecipe;
                    break;
                }
            }

            if (matchingNewRecipe != null) {
                if (recipesAreEqual(oldRecipe, matchingNewRecipe)) {
                    updatedRecipes.add(matchingNewRecipe);
                }
            }
        }

        List<Recipe> newRecipes = recipesFromEcoServer.stream().filter(
                ecoServerRecipe -> recipesFromCraftingTool.stream().noneMatch(
                        craftingToolRecipe -> ecoServerRecipe.getName().equals(craftingToolRecipe.getName())
                )
        ).toList();

        List<Recipe> removedRecipes = recipesFromCraftingTool.stream().filter(
                craftingToolRecipe -> recipesFromEcoServer.stream().noneMatch(
                        ecoServerRecipe -> craftingToolRecipe.getName().equals(ecoServerRecipe.getName())
                )
        ).toList();

        return RecipeComparisonResult.builder()
                .updatedRecipes(updatedRecipes)
                .newRecipes(newRecipes)
                .removedRecipes(removedRecipes)
                .build();
    }

    public ItemComparisonResult analyzeItemDifferences(List<Item> itemsFromCraftingTool, List<Item> itemsFromEcoServer) {
        List<Item> newItems = itemsFromEcoServer.stream().filter(
                ecoServerItem -> itemsFromCraftingTool.stream().noneMatch(
                        craftingToolItem -> Item.itemsAreEqual(ecoServerItem, craftingToolItem)
                )
        ).toList();

        List<Item> removedItems = itemsFromCraftingTool.stream().filter(
                craftingToolItem -> itemsFromCraftingTool.stream().noneMatch(
                        ecoServerItem -> Item.itemsAreEqual(craftingToolItem, ecoServerItem)
                )
        ).toList();

        return ItemComparisonResult.builder()
                .newItems(newItems)
                .removedItems(removedItems)
                .build();
    }
}
