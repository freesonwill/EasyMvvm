package com.xcjh.app.websocket.so;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonConvert {


    public  <T> T jsonToObject2(String jsonStr, Class<T> cls) {
        try {
              Gson gson = new Gson();
            TypeToken<T> typeToken = TypeToken.get(cls);
            return gson.fromJson(jsonStr, typeToken.getType());
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> List<T> jsonToList(String jsonStr, Class<T> cls) {
        List<T> list = new ArrayList<>();
        try {
            JsonArray jsonArray = new JsonParser().parse(jsonStr).getAsJsonArray();
            for (JsonElement jsonElement : jsonArray) {
                list.add(new Gson().fromJson(jsonElement, cls));
            }
        } catch (Exception e) {
            // 异常处理
        }

        return list;
    }

//    public static <T> List<T> jsonToList(String jsonStr, Type type) {
//        List<T> list = new ArrayList<>();
//        try {
//            JsonArray jsonArray = new JsonParser().parse(jsonStr).getAsJsonArray();
//            for (JsonElement jsonElement : jsonArray) {
//                T item = new Gson().fromJson(jsonElement, type);
//                list.add(item);
//            }
//        } catch (Exception e) {
//            // 异常处理
//        }
//
//        return list;
//    }

//    public static <T> List<T> jsonToList(String jsonStr, Class<T> cls) {
//        List<T> list = new ArrayList<>();
//        try {
//            JsonArray jsonArray = new JsonParser().parse(jsonStr).getAsJsonArray();
//            for (JsonElement jsonElement : jsonArray) {
//                T item = new Gson().fromJson(jsonElement, cls);
//                list.add(item);
//            }
//        } catch (Exception e) {
//            // 异常处理
//        }
//
//        return list;
//    }
}
