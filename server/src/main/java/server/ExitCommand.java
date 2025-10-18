package server;

import com.google.gson.JsonObject;

public class ExitCommand implements Command {

    @Override
    public JsonObject execute() {
        JsonObject response = new JsonObject();
        response.addProperty("response", "OK");
        response.addProperty("info", "Server shutting down");
        return response;
    }
}
