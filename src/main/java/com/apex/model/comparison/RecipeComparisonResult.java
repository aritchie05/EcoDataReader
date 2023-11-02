package com.apex.model.comparison;

import com.apex.model.Recipe;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RecipeComparisonResult {
    private List<Recipe> updatedRecipes;
    private List<Recipe> newRecipes;
    private List<Recipe> removedRecipes;
}
