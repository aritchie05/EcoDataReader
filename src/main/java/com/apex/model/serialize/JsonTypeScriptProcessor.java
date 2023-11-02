package com.apex.model.serialize;

import org.apache.commons.lang3.StringEscapeUtils;

public class JsonTypeScriptProcessor {

    public static String processJsonToTypeScript(String json) {
        String result  = json.replaceAll("\"", "'")
                .replaceAll("'item':('\\w+')", "'item':getItemByNameID($1)")
                .replaceAll("'skill':('\\w+')", "'skill':getSkillByNameID($1)")
                .replaceAll("'craftingTable':('\\w+')", "'craftingTable':getCraftingTableByNameID($1)");
        return StringEscapeUtils.unescapeJava(result);
    }

    public static String processTypeScriptToJson(String typescript) {
        return typescript.replaceAll("'item': getItemByNameID\\('(\\w+)'\\)", "'item':'$1'")
                .replaceAll("'skill': getSkillByNameID\\('(\\w+)'\\)", "'skill':'$1'")
                .replaceAll("'craftingTable': getCraftingTableByNameID\\('(\\w+)'\\)", "'craftingTable':'$1'")
                .replaceAll("'", "\"");
    }
}
