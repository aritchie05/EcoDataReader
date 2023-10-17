package com.apex.model;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CraftingTable {

    @SerializedName("name")
    private String craftingTableName;
    @SerializedName("nameID")
    private String craftingTableNameID;
    @SerializedName("upgradeModuleType")
    private String upgradeModuleTag;

    private boolean hidden;
}
