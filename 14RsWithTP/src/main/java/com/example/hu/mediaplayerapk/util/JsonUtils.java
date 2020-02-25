package com.example.hu.mediaplayerapk.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/28.
 */

public class JsonUtils {

    public static String listToJson(List values) {
        Gson gson = new Gson();
        return gson.toJson(values);
    }

    public static String beanToJson(Object values) {
        Gson gson = new Gson();
        return gson.toJson(values);
    }

    public static <T> List<T> jsonToList(String jsonData, Class<T> type) {
        List<T> list = new ArrayList<T>();
        try {
            Gson gson = new Gson();
            JsonArray arry = new JsonParser().parse(jsonData).getAsJsonArray();
            for (JsonElement jsonElement : arry) {
                list.add(gson.fromJson(jsonElement, type));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return list;

  /*    Gson gson = new Gson();
        List<T> result = gson.fromJson(jsonData, new TypeToken<List<T>>() {
        }.getType());
        return result;*/
    }

    public static <T> T jsonToBean(String jsonData, Class<T> type) {
        Gson gson = new Gson();
        T result = gson.fromJson(jsonData, type);
        return result;
    }

    public static Map<String, String> jsonToStringMap(String jsonData) {
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        return gson.fromJson(jsonData, new TypeToken<Map<String, String>>() {
        }.getType());
    }

    public static String StringMapToJson(Map<String, String> map) {
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        return gson.toJson(map);
    }

    public static Map<String, Integer> jsonToIntMap(String jsonData) {
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        return gson.fromJson(jsonData, new TypeToken<Map<String, Integer>>() {
        }.getType());
    }

    public static String IntMapToJson(Map<String, Integer> map) {
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        return gson.toJson(map);
    }
}
