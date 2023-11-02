package com.apex.service;

import com.apex.model.Item;
import com.apex.model.Recipe;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.apex.model.serialize.JsonTypeScriptProcessor.processTypeScriptToJson;
import static com.apex.service.GsonService.getGson;
import static java.nio.charset.StandardCharsets.UTF_8;

public class CraftingToolFileService {

    private static final Logger LOGGER = LogManager.getLogger(CraftingToolFileService.class);

    private static final String RECIPES_FILE = "recipes.ts";
    private static final String WHITE_TIGER_RECIPES_FILE = "white-tiger\\white-tiger-recipes.ts";
    private static final String ITEMS_FILE = "items.ts";

    private final String craftingToolDataPath;

    public CraftingToolFileService(String craftingToolDataPath) {
        this.craftingToolDataPath = craftingToolDataPath;
    }

    public List<Recipe> readRecipesFromTool() throws IOException {
        return readObjectsFromTool("Recipe[] =", new TypeToken<ArrayList<Recipe>>(){}.getType(), RECIPES_FILE);
    }

    public List<Recipe> readWhiteTigerRecipesFromTool() throws IOException {
        return readObjectsFromTool("Recipe[] =", new TypeToken<ArrayList<Recipe>>(){}.getType(), WHITE_TIGER_RECIPES_FILE);
    }

    public List<Item> readItemsFromTool() throws IOException {
        return readObjectsFromTool("Item[] =", new TypeToken<ArrayList<Item>>(){}.getType(), ITEMS_FILE);
    }

    private List readObjectsFromTool(String searchString, Type listType, String fileName) throws IOException {
        String fileContents = FileUtils.readFileToString(new File(craftingToolDataPath + fileName), UTF_8);
        fileContents = fileContents.replaceAll("\\R", "");

        int index = fileContents.indexOf(searchString);
        int startIndex = index + searchString.length();
        fileContents = fileContents.substring(startIndex, fileContents.length() - 1);

        String objectJson = processTypeScriptToJson(fileContents);

        return getGson().fromJson(objectJson, listType);
    }

}
