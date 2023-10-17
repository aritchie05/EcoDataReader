package com.apex.model.serialize;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class TableNameAdapter extends TypeAdapter<String> {
    @Override
    public void write(JsonWriter writer, String s) throws IOException {
        writer.beginObject().name("craftingTable").value("getCraftingTableByNameID('" + s + "')").endObject();
    }

    @Override
    public String read(JsonReader reader) throws IOException {
        return null;
    }
}
