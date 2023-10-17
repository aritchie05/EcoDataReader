package com.apex.model.serialize;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class SkillNameAdapter extends TypeAdapter<String> {

    @Override
    public void write(JsonWriter writer, String s) throws IOException {
        writer.beginObject();
        writer.name("skill");
        writer.value("getSkillByNameID('" + s + "')");
        writer.endObject();
    }

    @Override
    public String read(JsonReader reader) throws IOException {
        return null;
    }
}
