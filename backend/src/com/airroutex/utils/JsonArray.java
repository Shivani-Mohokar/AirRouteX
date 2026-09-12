package com.airroutex.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * JsonArray.java
 * ------------------------------------------------------------------
 * A tiny, fluent builder for a JSON array, e.g.:
 *
 *   JsonArray arr = new JsonArray();
 *   for (Airport a : airports) {
 *       arr.add(new JsonObject().put("code", a.getCode()));
 *   }
 *   arr.toJson();  -> [{"code":"DEL"},{"code":"BOM"}, ...]
 * ------------------------------------------------------------------
 */
public class JsonArray {

    private final List<Object> items = new ArrayList<>();

    public JsonArray add(Object value) {
        items.add(value);
        return this;
    }

    public String toJson() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(JsonUtil.serialize(items.get(i)));
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public String toString() {
        return toJson();
    }
}
