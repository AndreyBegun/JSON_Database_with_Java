package server;

import com.google.gson.*;

public class CommandFactory {

    public static Command createCommand(JsonDatabase db, JsonObject request) {
        String type = request.get("type").getAsString();

        JsonElement keyElement = request.has("key") ? request.get("key") : null;
        JsonElement valueElement = request.has("value") ? request.get("value") : null;

        if (type == null) {
            return new InvalidCommand("Missing command type");
        }

        return switch (type) {
            case "get" -> new GetCommand(db, keyElement);
            case "set" -> new SetCommand(db, keyElement, valueElement);
            case "delete" -> new DeleteCommand(db, keyElement);
            case "exit" -> new ExitCommand();
            default -> new InvalidCommand("Unknown command type: " + type);
        };
    }
}
