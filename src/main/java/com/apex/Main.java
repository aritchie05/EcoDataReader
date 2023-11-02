package com.apex;

import com.apex.locale.Translator;
import com.apex.model.Item;
import com.apex.model.Recipe;
import com.apex.model.comparison.ItemComparisonResult;
import com.apex.model.comparison.RecipeComparisonResult;
import com.apex.model.locale.LocaleData;
import com.apex.model.serialize.JsonTypeScriptProcessor;
import com.apex.service.CraftingToolFileService;
import com.apex.service.DataElementComparisonService;
import com.apex.service.EcoServerFileService;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.apex.service.GsonService.getGson;

public class Main {

    private static final String ECO_SERVER_PATH = "D:\\Eco Servers\\EcoServerPC_v0.10.0.0-beta-staging-2785\\Mods\\__core__\\";
    private static final String WHITE_TIGER_PATH = "D:\\Eco Servers\\WhiteTiger-10Playtest\\";

    private static final String TOOL_PATH = "C:\\Users\\aritc\\IdeaProjects\\EcoCraftingTool\\src\\assets\\data\\";

    public static void main(String[] args) throws IOException {

    }

    public static void compareWhiteTigerRecipes() throws IOException {
        EcoServerFileService ecoServerFileService = new EcoServerFileService(WHITE_TIGER_PATH);
        List<Recipe> wtRecipes = ecoServerFileService.getAllRecipes();
        List<Recipe> toolWtRecipes = getWhiteTigerRecipesFromTool();
        DataElementComparisonService comparisonService = new DataElementComparisonService();
        RecipeComparisonResult recipeComparisonResult = comparisonService.analyzeRecipeDifferences(toolWtRecipes, wtRecipes);
    }

    public static void compareItemsAndRecipes() throws IOException {
        EcoServerFileService ecoServerFileService = new EcoServerFileService(ECO_SERVER_PATH);
        List<Recipe> recipes = ecoServerFileService.getAllRecipes();
        List<Item> items = ecoServerFileService.getAllItems();
        List<Recipe> oldRecipes = getRecipesFromTool();
        List<Item> oldItems = getItemsFromTool();
        DataElementComparisonService comparisonService = new DataElementComparisonService();
        RecipeComparisonResult recipeComparisonResult = comparisonService.analyzeRecipeDifferences(oldRecipes, recipes);
        ItemComparisonResult itemComparisonResult = comparisonService.analyzeItemDifferences(oldItems, items);
        String newRecipeTs = JsonTypeScriptProcessor.processJsonToTypeScript(getGson().toJson(recipeComparisonResult.getNewRecipes()));
        String newItemTs = JsonTypeScriptProcessor.processJsonToTypeScript(getGson().toJson(itemComparisonResult.getNewItems()));
    }


    public static String getLocaleJson() throws IOException {
        Translator translator = new Translator(new EcoServerFileService(ECO_SERVER_PATH));
        List<LocaleData> localeData = translator.generateLocaleData();
        String json = new Gson().toJson(localeData);
        return json;
    }



    public static void writeItemsToFile() throws IOException {
        List<Item> currentItems = getItemsFromFiles();

        List<String> names = currentItems.stream().map(Item::getName).toList();
        FileUtils.writeLines(new File("src/main/resources/newest-items.txt"), names);
    }
    
    public static void writeRecipesToFile() throws IOException {
        List<Recipe> currentRecipes = getRecipesFromFiles();

        List<String> names = currentRecipes.stream().map(Recipe::getName).toList();
        FileUtils.writeLines(new File("src/main/resources/newest-recipes.txt"), names);
    }

    public static String generateNewRecipesString() throws IOException {
        List<Recipe> newRecipes = getRecipesFromFiles();

        Gson gson = getGson();
        String recipeJson = gson.toJson(newRecipes);

        //Convert the skill, table, item name IDs into the proper TS method calls
        String tsString = JsonTypeScriptProcessor.processJsonToTypeScript(recipeJson);

        //Remove [] from the ends of the json
        tsString = tsString.substring(1, tsString.length() - 1);
        return tsString;
    }
    
    public static String generateNewItemsString() throws IOException {
        List<Item> newItems = getItemsFromFiles();

        Gson gson = getGson();
        String itemJson = gson.toJson(newItems);

        //Convert the skill, table, item name IDs into the proper TS method calls
        String tsString = JsonTypeScriptProcessor.processJsonToTypeScript(itemJson);

        //Remove [] from the ends of the json
        tsString = tsString.substring(1, tsString.length() - 1);
        return tsString;
    }

    private static List<Recipe> getRecipesFromFiles() throws IOException {
        EcoServerFileService ecoServerFileService = new EcoServerFileService(ECO_SERVER_PATH);
        List<Recipe> recipes = ecoServerFileService.getAllRecipes();
        recipes.sort(Comparator.comparing(Recipe::getName));

        List<Recipe> newRecipes = new ArrayList<>();
        for (Recipe recipe : recipes) {
            if (matchesNewRecipes(recipe)) {
                newRecipes.add(recipe);
            }
        }
        return newRecipes;
    }

    private static List<Item> getItemsFromFiles() throws IOException {
        EcoServerFileService ecoServerFileService = new EcoServerFileService(ECO_SERVER_PATH);
        List<Item> items = ecoServerFileService.getAllItems();
        items.sort(Comparator.comparing(Item::getName));

        List<Item> newItems = new ArrayList<>();
        for (Item item : items) {
            if (matchesNewItems(item)) {
                newItems.add(item);
            }
        }
        return newItems;
    }

    public static boolean matchesNewItems(Item item) throws IOException {
        List<String> itemNames = FileUtils.readLines(new File("src/main/resources/current-items.txt"),
                StandardCharsets.UTF_8);
        return itemNames.stream().noneMatch(i -> i.equalsIgnoreCase(item.getName()));
    }

    public static boolean matchesNewRecipes(Recipe recipe) throws IOException {

        List<String> recipeNames = FileUtils.readLines(new File("src/main/resources/current-recipes.txt"),
                StandardCharsets.UTF_8);
        return recipeNames.stream().noneMatch(r -> r.equalsIgnoreCase(recipe.getName()));
    }

    public static List<Recipe> getRecipesFromTool() throws IOException {
        CraftingToolFileService craftingToolFileService = new CraftingToolFileService(TOOL_PATH);
        return craftingToolFileService.readRecipesFromTool();
    }

    public static List<Recipe> getWhiteTigerRecipesFromTool() throws IOException {
        CraftingToolFileService craftingToolFileService = new CraftingToolFileService(TOOL_PATH);
        return craftingToolFileService.readWhiteTigerRecipesFromTool();
    }

    public static List<Item> getItemsFromTool() throws IOException {
        CraftingToolFileService craftingToolFileService = new CraftingToolFileService(TOOL_PATH);
        return craftingToolFileService.readItemsFromTool();
    }
}
