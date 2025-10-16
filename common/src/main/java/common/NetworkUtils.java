package common;

import java.io.*;
import java.net.Socket;

public class NetworkUtils {

    public static void sendJson(Socket socket, String json) throws IOException {
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        out.writeUTF(json);
        out.flush();
    }

    public static String receiveJson(Socket socket) throws IOException {
        DataInputStream in = new DataInputStream(socket.getInputStream());
        return in.readUTF();
    }
}
