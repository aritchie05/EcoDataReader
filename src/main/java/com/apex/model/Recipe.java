package com.apex.model;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Recipe {
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
}
