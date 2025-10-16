package server;

import com.google.gson.*;

public class SetCommand implements Command {
    private final Database db;
    private final JsonArray key;
    private final JsonElement value;
    private static final Gson GSON = new Gson();

    public SetCommand(Database db, JsonArray key, JsonElement value) {
        this.db = db;
        this.key = key;
        this.value = value;
    }

    @Override
    public String execute() {
        db.set(key, value);
        JsonObject r = new JsonObject();
        r.addProperty("response", "OK");
        return GSON.toJson(r);
    }
}
