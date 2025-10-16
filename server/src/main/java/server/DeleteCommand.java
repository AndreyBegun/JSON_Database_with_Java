package server;

import com.google.gson.*;

public class DeleteCommand implements Command {
    private final Database db;
    private final JsonArray key;
    private static final Gson GSON = new Gson();

    public DeleteCommand(Database db, JsonArray key) {
        this.db = db;
        this.key = key;
    }

    @Override
    public String execute() {
        boolean ok = db.delete(key);
        JsonObject r = new JsonObject();
        if (ok) r.addProperty("response", "OK");
        else {
            r.addProperty("response", "ERROR");
            r.addProperty("reason", "No such key");
        }
        return GSON.toJson(r);
    }
}
