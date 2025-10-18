package server;

import com.google.gson.JsonObject;

/**
 * Represents an invalid or unrecognized command.
 * Returned when the "type" field in the request is missing or not supported.
 */
public class InvalidCommand implements Command {

    private final String message;

    public InvalidCommand(String message) {
        this.message = message;
    }

    @Override
    public JsonObject execute() {
        JsonObject response = new JsonObject();
        response.addProperty("response", "ERROR");
        response.addProperty("reason", message != null ? message : "Invalid request");
        return response;
    }
}
