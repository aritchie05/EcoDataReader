package com.apex.model;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Item {
    private String name;
    @SerializedName("nameID")
    private String itemNameID;
    private boolean tag;
    private String imageFile = "UI_Icons_06.png";
    private int xPos = 0;
    private int yPos = 0;
}
