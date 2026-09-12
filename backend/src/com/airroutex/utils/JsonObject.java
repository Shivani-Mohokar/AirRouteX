package com.airroutex.utils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * JsonObject.java
 * ------------------------------------------------------------------
 * A tiny, fluent builder for a JSON object, e.g.:
 *
 *   new JsonObject()
 *       .put("username", "admin")
 *       .put("role", "ADMIN")
 *       .put("age", 30)
 *       .toJson();
 *
 *   -> {"username":"admin","role":"ADMIN","age":30}
 *
 * Uses a LinkedHashMap so keys are serialized in insertion order,
 * which makes servlet responses predictable and easy to read/debug.
 * ------------------------------------------------------------------
 */
public class JsonObject {

    private final Map<String, Object> fields = new LinkedHashMap<>();

    public JsonObject put(String key, Object value) {
        fields.put(key, value);
        return this;
    }

    public String toJson() {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            if (!first) {
                sb.append(",");
            }
            first = false;
            sb.append("\"").append(JsonUtil.escape(entry.getKey())).append("\":");
            sb.append(JsonUtil.serialize(entry.getValue()));
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public String toString() {
        return toJson();
    }
}
