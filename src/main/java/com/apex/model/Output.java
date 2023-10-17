package com.apex.model;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Output {
    @SerializedName("item")
    private String itemNameID;
    private int quantity;
    private boolean reducible;
    private boolean primary;

    @Override
    public String toString() {
        String reduce = "";
        String prim = "";
        if (reducible) {
            reduce = " (Reducible)";
        }
        if (primary) {
            prim = " (Primary)";
        }
        return quantity + " " + itemNameID + reduce + prim;
    }
}
