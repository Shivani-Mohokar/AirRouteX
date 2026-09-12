package com.airroutex.utils;

/**
 * JsonUtil.java
 * ------------------------------------------------------------------
 * Tiny hand-written JSON helper - deliberately NOT a full JSON
 * library (no org.json / Gson / Jackson dependency), because this
 * project only ever needs to (a) build small, known-shape JSON
 * response objects and (b) read simple form/query parameters from
 * requests (handled directly via HttpServletRequest.getParameter(),
 * so no JSON *parsing* is needed at all - see servlets/ package).
 *
 * JsonObject and JsonArray (in this same package) do the actual
 * building; this class just holds the shared string-escaping logic
 * they both call, since valid JSON requires quotes, backslashes,
 * and control characters inside strings to be escaped.
 * ------------------------------------------------------------------
 */
public class JsonUtil {

    private JsonUtil() {
        // utility class - no instances
    }

    /** Escapes a string so it can be safely placed between double quotes in JSON. */
    public static String escape(String value) {
        if (value == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n");  break;
                case '\r': sb.append("\\r");  break;
                case '\t': sb.append("\\t");  break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }

    /** Serializes any supported value type to its JSON representation. */
    static String serialize(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof JsonObject) {
            return ((JsonObject) value).toJson();
        }
        if (value instanceof JsonArray) {
            return ((JsonArray) value).toJson();
        }
        if (value instanceof String) {
            return "\"" + escape((String) value) + "\"";
        }
        if (value instanceof Number || value instanceof Boolean) {
            return String.valueOf(value);
        }
        // Fallback: treat anything else (e.g. an enum) as its string form
        return "\"" + escape(String.valueOf(value)) + "\"";
    }
}
