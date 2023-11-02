package com.apex.model;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

import static com.apex.model.Item.nameIDsMatch;

@Data
@Builder
public class Output {
    @SerializedName("item")
    private String itemNameID;
    private BigDecimal quantity;
    private boolean reducible;
    private boolean primary;

    public String toReadableString() {
        String reduce = "";
        String prim = "";
        if (reducible) {
            reduce = " (R)";
        }
        if (primary) {
            prim = " (P)";
        }
        return quantity + " " + itemNameID + reduce + prim;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Output output = (Output) o;

        if (reducible != output.reducible) return false;
        if (primary != output.primary) return false;
        if (!nameIDsMatch(this.itemNameID, output.itemNameID)) return false;
        return quantity.equals(output.quantity);
    }

    @Override
    public int hashCode() {
        int result = itemNameID.hashCode();
        result = 31 * result + quantity.hashCode();
        result = 31 * result + (reducible ? 1 : 0);
        result = 31 * result + (primary ? 1 : 0);
        return result;
    }
}
