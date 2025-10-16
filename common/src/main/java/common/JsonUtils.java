package common;

import com.google.gson.*;

public class JsonUtils {
    private static final Gson GSON = new Gson();

    public static JsonObject parseAsObject(String json) {
        return JsonParser.parseString(json).getAsJsonObject();
    }

    public static JsonElement parse(String json) {
        return JsonParser.parseString(json);
    }

    public static String toJson(Object o) {
        return GSON.toJson(o);
    }
}
