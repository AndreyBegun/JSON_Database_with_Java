package server;

import com.google.gson.JsonObject;
import com.google.gson.Gson;

public class InvalidCommand implements Command {
    private static final Gson GSON = new Gson();
    @Override
    public String execute() {
        JsonObject r = new JsonObject();
        r.addProperty("response", "ERROR");
        r.addProperty("reason", "Invalid request");
        return GSON.toJson(r);
    }
}
