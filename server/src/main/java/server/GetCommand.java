package server;

import com.google.gson.*;

public class GetCommand implements Command {
    private final Database db;
    private final JsonArray key;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public GetCommand(Database db, JsonArray key) {
        this.db = db;
        this.key = key;
    }

    @Override
    public String execute() {
        JsonElement el = db.get(key);
        JsonObject r = new JsonObject();
        if (el != null) {
            r.addProperty("response", "OK");
            r.add("value", el);
        } else {
            r.addProperty("response", "ERROR");
            r.addProperty("reason", "No such key");
        }
        return GSON.toJson(r);
    }
}
