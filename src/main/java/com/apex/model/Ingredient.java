package com.apex.model;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

import static com.apex.model.Item.nameIDsMatch;

@Data
@Builder
public class Ingredient {

    @SerializedName("item")
    private String itemNameID;
    private BigDecimal quantity;
    private boolean reducible;
    private transient boolean tag;

    public String toReadableString() {
        String reduce = "";
        if (reducible) {
            reduce = " (R)";
        }

        String tagStr = "";
        if (tag) {
            tagStr = " (T)";
        }

        return quantity + " " + itemNameID + reduce + tagStr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ingredient that = (Ingredient) o;

        if (reducible != that.reducible) return false;
        if (!nameIDsMatch(this.itemNameID, that.itemNameID)) return false;
        return quantity.equals(that.quantity);
    }

    @Override
    public int hashCode() {
        int result = itemNameID.hashCode();
        result = 31 * result + quantity.hashCode();
        result = 31 * result + (reducible ? 1 : 0);
        return result;
    }
}
