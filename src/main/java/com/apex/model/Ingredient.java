package com.apex.model;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Ingredient {

    @SerializedName("item")
    private String itemNameID;
    private int quantity;
    private boolean reducible;
    private transient boolean tag;

    public String toReadableString() {
        String reduce = "";
        if (reducible) {
            reduce = " (Reducible)";
        }

        String tagStr = "";
        if (tag) {
            tagStr = " (Tag)";
        }

        return quantity + " " + itemNameID + reduce + tagStr;
    }
}
