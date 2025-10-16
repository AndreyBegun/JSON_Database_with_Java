package server;

import com.google.gson.*;

public class CommandFactory {
    private final Database db;

    public CommandFactory(Database db) {
        this.db = db;
    }

    public Command create(JsonObject request) {
        String type = request.has("type") ? request.get("type").getAsString() : "";
        JsonArray key = null;
        if (request.has("key")) {
            JsonElement keyEl = request.get("key");
            if (keyEl.isJsonArray()) key = keyEl.getAsJsonArray();
            else {
                key = new JsonArray();
                key.add(keyEl.getAsString());
            }
        }
        JsonElement value = request.has("value") ? request.get("value") : null;

        switch (type) {
            case "get": return new GetCommand(db, key);
            case "set": return new SetCommand(db, key, value);
            case "delete": return new DeleteCommand(db, key);
            case "exit": return new ExitCommand();
            default: return new InvalidCommand();
        }
    }
}
