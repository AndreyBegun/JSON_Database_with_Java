package server;

import com.google.gson.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Main {

    private static final int PORT = 23456;
    private static final String DB_FILE_PATH = "data/db.json";

    public static void main(String[] args) {
        JsonDatabase database = new JsonDatabase(DB_FILE_PATH);

        try (ExecutorService executor = Executors.newFixedThreadPool(4)) {
            System.out.println("Server started!");

            try (ServerSocket serverSocket = new ServerSocket(PORT)) {

                while (true) {
                    Socket socket = serverSocket.accept();
                    executor.submit(() -> handleClient(socket, database, executor));
                }

            } catch (IOException e) {
                System.out.println("Server error: " + e.getMessage());
            }
        }
    }

    private static void handleClient(Socket socket, JsonDatabase database, ExecutorService executor) {
        try (DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {

            String received = input.readUTF();
            JsonObject request = JsonParser.parseString(received).getAsJsonObject();

            Command command = CommandFactory.createCommand(database, request);
            JsonObject response = command.execute();

            output.writeUTF(response.toString());
            output.flush();

            if ("exit".equals(request.get("type").getAsString())) {
                output.writeUTF(response.toString());
                output.flush();

                System.out.println("Exit command received. Shutting down...");
                executor.shutdownNow();
                System.exit(0);
            }

        } catch (IOException e) {
            System.out.println("Client connection error: " + e.getMessage());
        }
    }
}
