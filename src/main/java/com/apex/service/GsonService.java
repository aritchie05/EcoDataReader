package com.apex.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonService {

    public static Gson getGson() {
        GsonBuilder builder = new GsonBuilder();
        return builder.create();
    }
}
