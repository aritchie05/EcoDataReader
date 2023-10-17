package com.apex;

import com.apex.locale.Translator;
import com.apex.model.Item;
import com.apex.model.Recipe;
import com.apex.model.locale.LocaleData;
import com.apex.model.serialize.JsonToTypeScriptProcessor;
import com.apex.service.FileService;
import com.apex.service.GsonService;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Main {

    private static final String ECO_SERVER_PATH = "D:\\Eco Servers\\EcoServerPC_v0.10.0.0-beta-staging-2770\\Mods\\__core__\\";

    public static void main(String[] args) throws IOException {
        List<Item> items = getItemsFromFiles();
        List<Recipe> recipes = getRecipesFromFiles();
        writeItemsToFile();
        writeRecipesToFile();
        String recipesStr = generateNewRecipesString();
        String itemsStr = generateNewItemsString();
        getLocaleJson();
    }

    public static String getLocaleJson() throws IOException {
        Translator translator = new Translator(new FileService(ECO_SERVER_PATH));
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

        Gson gson = GsonService.getGson();
        String recipeJson = gson.toJson(newRecipes);

        //Convert the skill, table, item name IDs into the proper TS method calls
        String tsString = JsonToTypeScriptProcessor.processJsonToTypeScript(recipeJson);

        //Remove [] from the ends of the json
        tsString = tsString.substring(1, tsString.length() - 1);
        return tsString;
    }
    
    public static String generateNewItemsString() throws IOException {
        List<Item> newItems = getItemsFromFiles();

        Gson gson = GsonService.getGson();
        String itemJson = gson.toJson(newItems);

        //Convert the skill, table, item name IDs into the proper TS method calls
        String tsString = JsonToTypeScriptProcessor.processJsonToTypeScript(itemJson);

        //Remove [] from the ends of the json
        tsString = tsString.substring(1, tsString.length() - 1);
        return tsString;
    }

    private static List<Recipe> getRecipesFromFiles() throws IOException {
        FileService fileService = new FileService(ECO_SERVER_PATH);
        List<Recipe> recipes = fileService.getAllRecipes();
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
        FileService fileService = new FileService(ECO_SERVER_PATH);
        List<Item> items = fileService.getAllItems();
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
}
