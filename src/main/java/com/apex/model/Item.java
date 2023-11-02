package com.apex.model;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.Arrays;
import java.util.List;

@Data
@Builder
public class Item {

    public static final List<ImmutablePair<String, String>> equivalentItemNamesIDs = Arrays.asList(
            new ImmutablePair<>("WoodBoard", "BoardItem"),
            new ImmutablePair<>("Oil", "OilItem"),
            new ImmutablePair<>("AshlarStone", "AshlarBasaltItem"),
            new ImmutablePair<>("AshlarStone", "AshlarGneissItem"),
            new ImmutablePair<>("AshlarStone", "AshlarGraniteItem"),
            new ImmutablePair<>("AshlarStone", "AshlarLimestoneItem"),
            new ImmutablePair<>("AshlarStone", "AshlarSandstoneItem"),
            new ImmutablePair<>("AshlarStone", "AshlarShaleItem"),
            new ImmutablePair<>("HewnLog", "HewnLogItem"),
            new ImmutablePair<>("CompositeLumber", "CompositeLumberItem"),
            new ImmutablePair<>("Lumber", "LumberItem")
    );

    private String name;
    @SerializedName("nameID")
    private String itemNameID;
    private boolean tag;
    private String imageFile = "UI_Icons_06.png";
    private int xPos = 0;
    private int yPos = 0;

    public static boolean nameIDsMatch(String itemNameID, String otherItemNameID) {
        return itemNameID.equals(otherItemNameID) || equivalentItemNamesIDs.stream().anyMatch(pair ->
            pair.left.equals(itemNameID) && pair.right.equals(otherItemNameID) || pair.right.equals(itemNameID) && pair.left.equals(otherItemNameID));
    }

    public static boolean itemsAreEqual(Item oldItem, Item newItem) {
        return oldItem.name.equals(newItem.name) && oldItem.itemNameID.equals(newItem.itemNameID);
    }
}
