package com.apex.service;

import com.apex.model.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

public class EcoServerFileService {

    private static final Logger LOGGER = LogManager.getLogger(EcoServerFileService.class);

    private static final String ITEMS_LOCATION = "AutoGen\\";

    private static final String TAGS_LOCATION = "Systems\\TagDefinitions.cs";

    private static final List<String> ITEM_FOLDERS = Arrays.asList("Block", "Clothing", "Fertilizer", "Food",
            "Item", "PluginModule", "Seed", "Tool", "Vehicle", "WorldObject");

    private static final List<String> RECIPE_FOLDERS = Arrays.asList("Block", "Clothing", "Fertilizer", "Food",
            "Item", "PluginModule", "Recipe", "Seed", "Tool", "Vehicle", "WorldObject");

    private static final List<String> CRAFTING_TABLE_FOLDERS = Collections.singletonList("WorldObject");

    private static final List<String> SKILL_FOLDERS = Collections.singletonList("Tech");

    private final String ecoServerPath;

    public EcoServerFileService(String ecoServerModsCorePath) {
        ecoServerPath = ecoServerModsCorePath;
    }

    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();

        for (String folder : ITEM_FOLDERS) {
            Collection<File> files = FileUtils.listFiles(new File(ecoServerPath + ITEMS_LOCATION + folder), new String[]{"cs"}, false);

            for (File file : files) {
                try {
                    String fileContents = FileUtils.readFileToString(file, UTF_8);
                    String name = getNameFromCSFileContents(fileContents);
                    if (name != null) {
                        String itemNameID = getItemNameIDFromCSFileContents(fileContents);
                        items.add(Item.builder().name(name).itemNameID(itemNameID).imageFile("UI_Icons_06.png").xPos(0).yPos(0).build());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        items.sort(Comparator.comparing(Item::getItemNameID));

        return items;
    }

    public List<Item> getAllTags() {
        List<Item> tags = new ArrayList<>();

        try {
            String fileContents = FileUtils.readFileToString(new File(ecoServerPath + TAGS_LOCATION), UTF_8);

            //Ignore hidden tags in tag definitions file
            fileContents = fileContents.substring(0, fileContents.indexOf("Hidden"));

            String tagRegex = "new TagDefinition\\(\"([\\w\\s]+)\"";
            Pattern pattern = Pattern.compile(tagRegex);
            Matcher matcher = pattern.matcher(fileContents);
            while (matcher.find()) {
                String tagName = matcher.group(1);
                tagName = tagName.replaceAll(" ", "");
                tagName = StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(tagName), " ");
                tags.add(Item.builder().itemNameID(tagName.replaceAll(" ", "")).name(tagName).tag(true).build());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        tags.sort(Comparator.comparing(Item::getItemNameID));

        return tags;
    }

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();

        for (String folder : RECIPE_FOLDERS) {
            Collection<File> files;
            try {
                 files = FileUtils.listFiles(new File(ecoServerPath + ITEMS_LOCATION + folder), new String[]{"cs"}, false);
            } catch (IllegalArgumentException e) {
                continue;
            }

            for (File file : files) {
                try {
                    String fileContents = FileUtils.readFileToString(file, UTF_8);
                    Recipe recipe = getRecipeFromCSFileContents(fileContents, file.getName());
                    if (recipe != null) {
                        recipes.add(recipe);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        recipes.sort(Comparator.comparing(Recipe::getNameID));

        return recipes;
    }

    public List<CraftingTable> getAllCraftingTables() {
        List<CraftingTable> craftingTables = new ArrayList<>();

        for (String folder : CRAFTING_TABLE_FOLDERS) {
            Collection<File> files = FileUtils.listFiles(new File(ecoServerPath + ITEMS_LOCATION + folder), new String[]{"cs"}, false);

            for (File file : files) {
                try {
                    String fileContents = FileUtils.readFileToString(file, UTF_8);
                    CraftingTable craftingTable = getCraftingTableFromCSFileContents(fileContents, file.getName());
                    if (craftingTable != null) {
                        craftingTables.add(craftingTable);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        craftingTables.sort(Comparator.comparing(CraftingTable::getCraftingTableNameID));

        return craftingTables;
    }

    public List<Skill> getAllSkills() {
        List<Skill> skills = new ArrayList<>();

        for (String folder : SKILL_FOLDERS) {
            Collection<File> files = FileUtils.listFiles(new File(ecoServerPath + ITEMS_LOCATION + folder), new String[]{"cs"}, false);

            for (File file : files) {
                try {
                    String fileContents = FileUtils.readFileToString(file, UTF_8);
                    Skill skill = getSkillFromCSFileContents(fileContents, file.getName());
                    if (skill != null) {
                        skills.add(skill);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        skills.sort(Comparator.comparing(Skill::getNameID));

        return skills;
    }

    @SuppressWarnings("squid:S3776")
    private static Recipe getRecipeFromCSFileContents(String contents, String fileName) {
        //Recipe display name (e.g. Butcher Bison)
        String recipeNameRegex = "displayName:\\s*Localizer\\.DoStr\\(\"([\\w\\s]+)\"\\)";
        Pattern pattern = Pattern.compile(recipeNameRegex);
        Matcher matcher = pattern.matcher(contents);
        String recipeName = "";
        if (matcher.find()) {
            recipeName = matcher.group(1);
        } else  {
            LOGGER.warn("Could not find recipe name for file %s".formatted(fileName));
            return null;
        }

        //Recipe name ID (e.g. ButcherBison)
        String recipeNameIDRegex = "recipe.Init\\(\\n*\\s*name:\\s*\"(\\w+)\"";
        pattern = Pattern.compile(recipeNameIDRegex);
        matcher = pattern.matcher(contents);
        String recipeNameID = "";
        if (matcher.find()) {
            recipeNameID = matcher.group(1);
        } else {
            LOGGER.warn("Could not find recipe name ID for recipe %s".formatted(recipeName));
            return null;
        }

        List<Ingredient> ingredients = new ArrayList<>();

        //Recipe ingredients - Specific items (non-tag)
        String recipeIngredientRegex = "new IngredientElement\\(typeof\\( *(\\w+)\\), (\\d+(?:\\.\\d+)?)f?, (\\w+)";
        pattern = Pattern.compile(recipeIngredientRegex);
        matcher = pattern.matcher(contents);
        while (matcher.find()) {
            String ingredientNameID = matcher.group(1);
            BigDecimal quantity = new BigDecimal(matcher.group(2));
            boolean reducible = false;
            if (matcher.group(3).equalsIgnoreCase("typeof")) {
                reducible = true;
            }
            ingredients.add(Ingredient.builder()
                    .itemNameID(ingredientNameID)
                    .quantity(quantity)
                    .reducible(reducible)
                    .tag(false)
                    .build());
        }

        //Recipe ingredients - tags
        String tagRecipeIngredientsRegex = "new IngredientElement\\(\"([\\w\\s]+)\", (\\d+), (\\w+)";
        pattern = Pattern.compile(tagRecipeIngredientsRegex);
        matcher = pattern.matcher(contents);
        while (matcher.find()) {
            String tagIngredientNameID = matcher.group(1).replaceAll(" ", "");
            BigDecimal quantity = new BigDecimal(matcher.group(2));
            boolean reducible = matcher.group(3).equalsIgnoreCase("typeof");
            ingredients.add(Ingredient.builder()
                    .itemNameID(tagIngredientNameID)
                    .quantity(quantity)
                    .reducible(reducible)
                    .tag(true)
                    .build());
        }

        if (ingredients.isEmpty()) {
            LOGGER.warn("Could not find ingredients for recipe %s".formatted(recipeName));
        }

        List<Output> outputs = new ArrayList<>();

        //Recipe outputs
        String recipeOutputsRegex = "new CraftingElement<(\\w+)>\\((\\d*\\.?\\d*)f?\\)";
        pattern = Pattern.compile(recipeOutputsRegex);
        matcher = pattern.matcher(contents);
        int outputCount = 0;
        while (matcher.find()) {
            String outputItemNameID = matcher.group(1);
            String quantityString = matcher.group(2);
            BigDecimal quantity = new BigDecimal(1);
            if (!quantityString.isBlank()) {
                quantity = new BigDecimal(quantityString);
            }
            Output output = Output.builder().itemNameID(outputItemNameID).quantity(quantity).reducible(false).build();
            if (outputCount == 0) {
                output.setPrimary(true);
            }
            outputs.add(output);

            outputCount++;
        }

        if (outputs.isEmpty()) {
            LOGGER.warn("Could not find outputs for recipe %s".formatted(recipeName));
        }

        //Recipe outputs - secondary (e.g. Tailings, Slag, Barrel)
        String recipeOutputsWasteRegex = "new CraftingElement<(\\w+)>\\(typeof\\(\\w+\\), (\\d+)(,?)";
        pattern = Pattern.compile(recipeOutputsWasteRegex);
        matcher = pattern.matcher(contents);
        while (matcher.find()) {
            String outputItemNameID = matcher.group(1);
            BigDecimal quantity = new BigDecimal(matcher.group(2));
            boolean reducible = !matcher.group(3).isBlank() || outputItemNameID.contains("Tailings") || outputItemNameID.contains("Slag");
            outputs.add(Output.builder().itemNameID(outputItemNameID).quantity(quantity).reducible(reducible).build());
        }


        //Skill and level
        String skillLevelRegex = "\\[RequiresSkill\\(typeof\\((\\w+)\\), (\\d)";
        pattern = Pattern.compile(skillLevelRegex);
        matcher = pattern.matcher(contents);
        String skillNameID = "";
        int level = 0;
        if (matcher.find()) {
            skillNameID = matcher.group(1);
            level = Integer.parseInt(matcher.group(2));
        } else {
            LOGGER.warn("Could not find skill and level for recipe %s".formatted(recipeName));
            return null;
        }

        //Labor cost
        String laborRegex = "CreateLaborInCaloriesValue\\((\\d+)";
        pattern = Pattern.compile(laborRegex);
        matcher = pattern.matcher(contents);
        int labor = 0;
        if (matcher.find()) {
            labor = Integer.parseInt(matcher.group(1));
        } else {
            LOGGER.warn("Could not find labor cost for recipe %s".formatted(recipeName));
        }

        //Crafting table
        String craftingTableRegex = "CraftingComponent\\.AddRecipe\\(tableType:\\s*typeof\\((\\w+)\\)";
        pattern = Pattern.compile(craftingTableRegex);
        matcher = pattern.matcher(contents);
        String craftingTableNameID = "";
        if (matcher.find()) {
            craftingTableNameID = matcher.group(1);
        } else {
            LOGGER.warn("Could not find crafting table for recipe %s".formatted(recipeName));
        }

        return Recipe.builder()
                .name(recipeName)
                .nameID(recipeNameID)
                .ingredients(ingredients)
                .outputs(outputs)
                .skillNameID(skillNameID)
                .level(level)
                .labor(labor)
                .craftingTableNameID(craftingTableNameID)
                .build();
    }

    private static String getNameFromCSFileContents(String contents) {
        String nameSearchRegex = "LocDisplayName\\(\"([\\w\\s]+)\"";
        Pattern pattern = Pattern.compile(nameSearchRegex);
        Matcher matcher = pattern.matcher(contents);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private static String getItemNameIDFromCSFileContents(String contents) {
        String nameSearchRegex = "public partial class (\\w+Item)";
        Pattern pattern = Pattern.compile(nameSearchRegex);
        Matcher matcher = pattern.matcher(contents);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private static Skill getSkillFromCSFileContents(String fileContents, String fileName) {
        String skillSearchRegex = "Tag\\(\"Specialty\"\\)";
        Pattern pattern = Pattern.compile(skillSearchRegex);
        Matcher matcher = pattern.matcher(fileContents);
        if (!matcher.find()) {
            return null;
        }

        String nameIDSearchRegex = "public partial class (\\w+Skill) : Skill";
        pattern = Pattern.compile(nameIDSearchRegex);
        matcher = pattern.matcher(fileContents);
        String nameID;
        if (matcher.find()) {
            nameID = matcher.group(1);
        } else {
            LOGGER.warn("Could not find skill name ID for file %s".formatted(fileName));
            return null;
        }

        String name = fileName.replace(".cs", "");
        name = StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(name), " ");

        return Skill.builder().name(name).nameID(nameID).build();
    }

    private static CraftingTable getCraftingTableFromCSFileContents(String fileContents, String fileName) {
        String craftingSearchRegex = "RequireComponent\\(typeof\\(CraftingComponent";
        Pattern pattern = Pattern.compile(craftingSearchRegex);
        Matcher matcher = pattern.matcher(fileContents);
        if (!matcher.find()) {
            return null;
        }

        String nameSearchRegex = "DisplayName\\s+=>\\s+Localizer\\.DoStr\\(\"([\\w\\s]+)\"\\)";
        pattern = Pattern.compile(nameSearchRegex);
        matcher = pattern.matcher(fileContents);
        String name;
        if (matcher.find()) {
            name = matcher.group(1);
        } else {
            LOGGER.warn("Could not find crafting table name for file %s".formatted(fileName));
            return null;
        }

        String nameIDSearchRegex = "public partial class (\\w+Object)";
        pattern = Pattern.compile(nameIDSearchRegex);
        matcher = pattern.matcher(fileContents);
        String nameID;
        if (matcher.find()) {
            nameID = matcher.group(1);
        } else {
            LOGGER.warn("Could not find nameID for crafting table %s".formatted(name));
            return null;
        }

        String upgradeModuleSearchRegex = "AllowPluginModules\\(Tags = new\\[] \\{ \"([\\w]+)";
        pattern = Pattern.compile(upgradeModuleSearchRegex);
        matcher = pattern.matcher(fileContents);
        String upgradeModule = null;
        if (matcher.find()) {
            upgradeModule = matcher.group(1);
        }

        return CraftingTable.builder()
                .craftingTableName(name)
                .craftingTableNameID(nameID)
                .upgradeModuleTag(upgradeModule)
                .build();
    }

}
