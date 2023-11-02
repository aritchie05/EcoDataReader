package com.apex.locale;

import com.apex.model.CraftingTable;
import com.apex.model.Item;
import com.apex.model.Recipe;
import com.apex.model.Skill;
import com.apex.model.locale.DefaultStringsHeaderMap;
import com.apex.model.locale.LocaleData;
import com.apex.model.locale.LocaleEntry;
import com.apex.service.EcoServerFileService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Translator {
    public static final String LOCALE_FOLDER = "src/main/resources/locale/";

    private final EcoServerFileService fileService;

    public Translator(EcoServerFileService ecoServerFileService) {
        this.fileService = ecoServerFileService;
    }

    public List<LocaleData> generateLocaleData() throws IOException {
        List<String> rawTranslationInfo = readDefaultStringsCsv();
        DefaultStringsHeaderMap headerMap = analyzeDefaultStringsCsv(rawTranslationInfo);

        List<Item> items = fileService.getAllItems();
        items.addAll(getMissingItems());
        items.addAll(fileService.getAllTags());
        items.addAll(getMissingTags());
        List<Recipe> recipes = fileService.getAllRecipes();
        this.adjustRecipeNames(recipes);
        List<CraftingTable> tables = fileService.getAllCraftingTables();
        List<Skill> skills = fileService.getAllSkills();

        LocaleData itemsLocaleData = LocaleData.builder().type("items").entries(new ArrayList<>()).build();
        LocaleData recipesLocaleData = LocaleData.builder().type("recipes").entries(new ArrayList<>()).build();
        LocaleData skillsLocaleData = LocaleData.builder().type("skills").entries(new ArrayList<>()).build();
        LocaleData tablesLocaleData = LocaleData.builder().type("tables").entries(new ArrayList<>()).build();
        LocaleData upgradesLocaleData = LocaleData.builder().type("upgrades").entries(new ArrayList<>()).build();
        List<String> upgradeNames = Arrays.asList("Advanced Upgrade 1", "Advanced Upgrade 2", "Advanced Upgrade 3", "Advanced Upgrade 4",
                "Basic Upgrade 1", "Basic Upgrade 2", "Basic Upgrade 3", "Basic Upgrade 4", "Modern Upgrade 1", "Modern Upgrade 2",
                "Modern Upgrade 3", "Modern Upgrade 4");

        //Loop through the lines in defaultStrings.csv and insert them as LocaleEntries in the correct LocaleData object
        for (int i = 1; i < rawTranslationInfo.size(); i++) {
            String[] translationLine = rawTranslationInfo.get(i).split(",");
            if (translationLine.length < headerMap.getTr()) {
                continue;
            }
            String englishStr = translationLine[headerMap.getEn()];

            List<Item> itemFiltered = items.stream()
                    .filter(item -> item.getName().equals(englishStr.trim()))
                    .toList();
            if (!itemFiltered.isEmpty()) {
                for (Item item : itemFiltered) {
                    LocaleEntry localeEntry = buildLocaleEntry(headerMap, translationLine, englishStr, item.getItemNameID());
                    itemsLocaleData.getEntries().add(localeEntry);
                }

            }

            List<Recipe> recipeFiltered = recipes.stream()
                    .filter(recipe -> recipe.getName().equalsIgnoreCase(englishStr.trim()))
                    .toList();
            if (!recipeFiltered.isEmpty()) {
                LocaleEntry localeEntry = buildLocaleEntry(headerMap, translationLine, englishStr, recipeFiltered.get(0).getNameID());
                recipesLocaleData.getEntries().add(localeEntry);
            }

            List<Skill> skillFiltered = skills.stream()
                    .filter(skill -> skill.getName().equals(englishStr.trim()))
                    .toList();
            if (!skillFiltered.isEmpty()) {
                LocaleEntry localeEntry = buildLocaleEntry(headerMap, translationLine, englishStr, skillFiltered.get(0).getNameID());
                skillsLocaleData.getEntries().add(localeEntry);
            }

            List<CraftingTable> tableFiltered = tables.stream()
                    .filter(table -> table.getCraftingTableName().equals(englishStr.trim()))
                    .toList();
            if (!tableFiltered.isEmpty()) {
                LocaleEntry localeEntry = buildLocaleEntry(headerMap, translationLine, englishStr, tableFiltered.get(0).getCraftingTableNameID());
                tablesLocaleData.getEntries().add(localeEntry);
            }

            List<String> upgradeFiltered = upgradeNames.stream()
                    .filter(upgrade -> upgrade.equalsIgnoreCase(englishStr.trim()))
                    .toList();
            if (!upgradeFiltered.isEmpty()) {
                LocaleEntry localeEntry = buildLocaleEntry(headerMap, translationLine, englishStr, StringUtils.remove(upgradeFiltered.get(0), " "));
                upgradesLocaleData.getEntries().add(localeEntry);
            }
        }

        return Arrays.asList(itemsLocaleData, recipesLocaleData, skillsLocaleData, tablesLocaleData, upgradesLocaleData);
    }

    public List<Item> getMissingItems() throws IOException {
        List<Item> missingItems = new ArrayList<>();

        List<String> itemLines = FileUtils.readLines(new File("src/main/resources/locale/missing-items.txt"), StandardCharsets.UTF_8);
        for (String itemLine : itemLines) {
            String[] nameAndId = itemLine.split(":");
            Item item = Item.builder().name(nameAndId[0]).itemNameID(nameAndId[1]).build();
            missingItems.add(item);
        }

        return missingItems;
    }

    public void adjustRecipeNames(List<Recipe> recipes) {
        for (Recipe recipe : recipes) {
            switch(recipe.getNameID()) {
                case "TailoringUpgrade" -> recipe.setNameID("TailoringBasicUpgrade");
                case "Latrine" -> recipe.setNameID("WoodenLatrine");
                case "PlanterPotRound" -> recipe.setNameID("RoundPot");
                case "PlanterPotSquare" -> recipe.setNameID("SquarePot");
            }
        }
    }

    public List<Item> getMissingTags() throws IOException {
        List<Item> missingTagItems = new ArrayList<>();

        List<String> tagIds = FileUtils.readLines(new File("src/main/resources/locale/missing-tags.txt"), StandardCharsets.UTF_8);
        for (String tagId : tagIds) {
            String name = StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(tagId), " ");
            if (name.equals("Wood Board")) {
                name = "Board";
            }
            Item item = Item.builder()
                    .itemNameID(tagId)
                    .name(name)
                    .tag(true)
                    .build();

            missingTagItems.add(item);
        }

        return missingTagItems;
    }

    private static LocaleEntry buildLocaleEntry(DefaultStringsHeaderMap headerMap, String[] translationLine,
                                                String englishStr, String nameID) {
        return LocaleEntry.builder()
                .id(nameID)
                .en(englishStr)
                .fr(translationLine[headerMap.getFr()])
                .es(translationLine[headerMap.getEs()])
                .de(translationLine[headerMap.getDe()])
                .pt(translationLine[headerMap.getPt()])
                .it(translationLine[headerMap.getIt()])
                .tr(translationLine[headerMap.getTr()])
                .pl(translationLine[headerMap.getPl()])
                .ru(translationLine[headerMap.getRu()])
                .uk(translationLine[headerMap.getUk()])
                .ko(translationLine[headerMap.getKo()])
                .zh(translationLine[headerMap.getZh()])
                .ja(translationLine[headerMap.getJa()])
                .build();
    }

    public DefaultStringsHeaderMap analyzeDefaultStringsCsv(List<String> fileLines) {
        String headerStr = fileLines.get(0);
        String[] headers = headerStr.split(",");
        DefaultStringsHeaderMap headerMap = new DefaultStringsHeaderMap();
        for (int i = 0; i < headers.length; i++) {
            switch (headers[i]) {
                case "English" -> headerMap.setEn(i);
                case "French" -> headerMap.setFr(i);
                case "Spanish" -> headerMap.setEs(i);
                case "German" -> headerMap.setDe(i);
                case "Korean" -> headerMap.setKo(i);
                case "BrazilianPortuguese" -> headerMap.setPt(i);
                case "SimplifedChinese" -> headerMap.setZh(i);
                case "Russian" -> headerMap.setRu(i);
                case "Italian" -> headerMap.setIt(i);
                case "Japanese" -> headerMap.setJa(i);
                case "Polish" -> headerMap.setPl(i);
                case "Ukrainian" -> headerMap.setUk(i);
                case "Turkish" -> headerMap.setTr(i);
            }
        }

        return headerMap;
    }

    public List<String> readDefaultStringsCsv() throws IOException {
        List<String> lines = FileUtils.readLines(new File(LOCALE_FOLDER + "defaultStrings.csv"), StandardCharsets.UTF_8);
        lines.replaceAll(s -> s.replaceAll("\"", ""));
        int correctCommasCount = StringUtils.countMatches(lines.get(0), ',');
        for (int i = 1; i < lines.size(); i++) {
            if (i < lines.size()) {
                String line = lines.get(i);
                int commaCount = StringUtils.countMatches(line, ',');
                if (commaCount < correctCommasCount) {
                    line = line.replace("\n", "");
                    line += lines.get(i + 1);
                    lines.set(i, line);
                    lines.remove(i + 1);
                }
            }
        }

        return lines;
    }


    public void writeFilesForTranslation() throws IOException {
        List<Item> items = fileService.getAllItems();
        FileUtils.writeLines(new File(LOCALE_FOLDER + "id/items.txt"), items.stream().map(Item::getItemNameID).toList());
        FileUtils.writeLines(new File(LOCALE_FOLDER + "en/items.en.txt"), items.stream().map(Item::getName).toList());

        List<Recipe> recipes = fileService.getAllRecipes();
        FileUtils.writeLines(new File(LOCALE_FOLDER + "id/recipes.txt"), recipes.stream().map(Recipe::getNameID).toList());
        FileUtils.writeLines(new File(LOCALE_FOLDER + "en/recipes.en.txt"), recipes.stream().map(Recipe::getName).toList());

        List<CraftingTable> tables = fileService.getAllCraftingTables();
        FileUtils.writeLines(new File(LOCALE_FOLDER + "id/tables.txt"), tables.stream().map(CraftingTable::getCraftingTableNameID).toList());
        FileUtils.writeLines(new File(LOCALE_FOLDER + "en/tables.en.txt"), tables.stream().map(CraftingTable::getCraftingTableName).toList());
    }
}
