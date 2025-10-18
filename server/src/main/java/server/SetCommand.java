package server;

import com.google.gson.*;

public class SetCommand implements Command {

    private final JsonDatabase database;
    private final JsonElement keyElement;
    private final JsonElement value;

    public SetCommand(JsonDatabase database, JsonElement keyElement, JsonElement value) {
        this.database = database;
        this.keyElement = keyElement;
        this.value = value;
    }

    @Override
    public JsonObject execute() {
        JsonObject response = new JsonObject();

        try {
            // Handle key: either single string or array of strings
            String[] keys = parseKey(keyElement);

            // Set the value in DB (can be any JSON type)
            database.set(keys, value);

            response.addProperty("response", "OK");
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
