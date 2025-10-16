package client;

import com.google.gson.*;
import common.NetworkUtils;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Main {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void main(String[] args) {
        System.out.println("Client started!");
        String requestJson = buildRequest(args);
        try (Socket socket = new Socket(common.Constants.HOST, common.Constants.PORT)) {
            NetworkUtils.sendJson(socket, requestJson);
            System.out.println("Sent: " + requestJson);
            String response = NetworkUtils.receiveJson(socket);
            System.out.println("Received: " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String buildRequest(String[] args) {
        List<String> argList = Arrays.asList(args);
        int inIndex = argList.indexOf("-in");
        if (inIndex != -1 && inIndex + 1 < argList.size()) {
            String fileName = argList.get(inIndex + 1);
            String path = System.getProperty("user.dir") + "/src/client/data/" + fileName;
            try {
                return Files.readString(Path.of(path));
            } catch (IOException e) {
                throw new RuntimeException("Cannot read file: " + path, e);
            }
        }

        JsonObject req = new JsonObject();
        for (int i = 0; i < args.length; i++) {
            String a = args[i];
            if ("-t".equals(a) && i + 1 < args.length) req.addProperty("type", args[++i]);
            else if ("-k".equals(a) && i + 1 < args.length) {
                // if key looks like JSON array, try parse; else use string
                String k = args[++i];
                try {
                    JsonElement maybeArr = JsonParser.parseString(k);
                    if (maybeArr.isJsonArray()) req.add("key", maybeArr);
                    else req.addProperty("key", k);
                } catch (Exception ex) {
                    req.addProperty("key", k);
                }
            } else if ("-v".equals(a) && i + 1 < args.length) {
                String v = args[++i];
                try {
                    JsonElement parsed = JsonParser.parseString(v);
                    req.add("value", parsed);
                } catch (Exception ex) {
                    req.addProperty("value", v);
                }
            }
        }
        return GSON.toJson(req);
    }
}
