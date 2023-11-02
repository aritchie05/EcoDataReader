package com.apex.service;

import com.apex.model.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CraftingToolFileServiceTest {

    private static final String PATH = "C:\\Users\\aritc\\IdeaProjects\\EcoCraftingTool\\src\\assets\\data\\";

    private CraftingToolFileService craftingToolFileService;

    @BeforeEach
    void setUp() {
        craftingToolFileService = new CraftingToolFileService(PATH);
    }

    @Test
    void readsRecipes() throws Exception {
        List<Recipe> recipes = craftingToolFileService.readRecipesFromTool();
        assertThat(recipes).isNotEmpty();
        assertThat(recipes.get(0)).isNotNull();
        assertThat(recipes.get(0).getSkillNameID()).doesNotContain("getSkillByNameID");
    }
}
