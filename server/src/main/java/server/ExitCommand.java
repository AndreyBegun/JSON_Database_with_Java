package server;

import com.google.gson.JsonObject;
import com.google.gson.Gson;

public class ExitCommand implements Command {
    private static final Gson GSON = new Gson();
    @Override
    public String execute() {
        JsonObject r = new JsonObject();
        r.addProperty("response", "OK");
        return GSON.toJson(r);
    }
}
