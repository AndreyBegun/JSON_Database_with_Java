package server;

import com.google.gson.*;
import common.NetworkUtils;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Main {
    private static final Gson GSON = new Gson();
    public static void main(String[] args) {
        System.out.println("Server started!");
        String dbPath = System.getProperty("user.dir") + "/src/server/data/db.json";
        Database db = new Database(dbPath);
        CommandFactory factory = new CommandFactory(db);

        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        try (ServerSocket ss = new ServerSocket(common.Constants.PORT)) {
            while (!Thread.currentThread().isInterrupted()) {
                Socket s = ss.accept();
                pool.submit(() -> handleClient(s, factory));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            pool.shutdown();
        }
    }

    private static void handleClient(Socket socket, CommandFactory factory) {
        try (Socket s = socket) {
            String reqJson = NetworkUtils.receiveJson(s);
            JsonObject req = JsonParser.parseString(reqJson).getAsJsonObject();
            Command cmd = factory.create(req);
            String resp = cmd.execute();
            NetworkUtils.sendJson(s, resp);

            // if exit requested, attempt graceful shutdown of entire JVM
            if (req.has("type") && "exit".equals(req.get("type").getAsString())) {
                System.exit(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
