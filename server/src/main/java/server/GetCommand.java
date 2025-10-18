package server;

import com.google.gson.*;

public class GetCommand implements Command {

    private final JsonDatabase database;
    private final JsonElement keyElement;

    public GetCommand(JsonDatabase database, JsonElement keyElement) {
        this.database = database;
        this.keyElement = keyElement;
    }

    @Override
    public JsonObject execute() {
        JsonObject response = new JsonObject();

        try {
            String[] keys = parseKey(keyElement);
            JsonElement value = database.get(keys);

            if (value == null || value.isJsonNull()) {
                response.addProperty("response", "ERROR");
                response.addProperty("reason", "No such key");
            } else {
                response.addProperty("response", "OK");
                response.add("value", value);
            }
        } catch (Exception e) {
            response.addProperty("response", "ERROR");
            response.addProperty("reason", e.getMessage());
        }

        return response;
    }

    private String[] parseKey(JsonElement keyElement) {
        if (keyElement.isJsonArray()) {
            JsonArray arr = keyElement.getAsJsonArray();
            String[] keys = new String[arr.size()];
            for (int i = 0; i < arr.size(); i++) {
                keys[i] = arr.get(i).getAsString();
            }
            return keys;
        } else {
            return new String[]{ keyElement.getAsString() };
        }
    }
}
