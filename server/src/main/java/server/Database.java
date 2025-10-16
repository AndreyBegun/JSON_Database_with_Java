package server;

import com.google.gson.*;
import java.io.*;
import java.nio.file.*;
import java.util.concurrent.locks.*;

public class Database {
    private final Path dbPath;
    private JsonObject root;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final ReadWriteLock rw = new ReentrantReadWriteLock();
    private final Lock r = rw.readLock();
    private final Lock w = rw.writeLock();

    public Database(String path) {
        this.dbPath = Paths.get(path);
        ensureFile();
        load();
    }

    private void ensureFile() {
        try {
            if (!Files.exists(dbPath)) {
                Files.createDirectories(dbPath.getParent());
                Files.writeString(dbPath, "{}");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void load() {
        w.lock();
        try {
            String s = Files.readString(dbPath);
            if (s.isBlank()) root = new JsonObject();
            else root = JsonParser.parseString(s).getAsJsonObject();
        } catch (IOException e) {
            root = new JsonObject();
        } finally {
            w.unlock();
        }
    }

    private void save() {
        w.lock();
        try (Writer writer = Files.newBufferedWriter(dbPath)) {
            gson.toJson(root, writer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            w.unlock();
        }
    }

    // get by path: keys is JsonArray of strings
    public JsonElement get(JsonArray keys) {
        r.lock();
        try {
            JsonElement cur = root;
            if (keys == null) return null;
            for (JsonElement k : keys) {
                if (!cur.isJsonObject()) return null;
                JsonObject obj = cur.getAsJsonObject();
                String key = k.getAsString();
                if (!obj.has(key)) return null;
                cur = obj.get(key);
            }
            return cur.deepCopy();
        } finally {
            r.unlock();
        }
    }

    // set value at path; create intermediate objects if needed
    public void set(JsonArray keys, JsonElement value) {
        w.lock();
        try {
            JsonObject cur = root;
            for (int i = 0; i < keys.size() - 1; i++) {
                String k = keys.get(i).getAsString();
                if (!cur.has(k) || !cur.get(k).isJsonObject()) {
                    JsonObject o = new JsonObject();
                    cur.add(k, o);
                    cur = o;
                } else {
                    cur = cur.getAsJsonObject(k);
                }
            }
            String last = keys.get(keys.size() - 1).getAsString();
            cur.add(last, value);
            save();
        } finally {
            w.unlock();
        }
    }

    // delete a key at path
    public boolean delete(JsonArray keys) {
        w.lock();
        try {
            JsonObject cur = root;
            for (int i = 0; i < keys.size() - 1; i++) {
                String k = keys.get(i).getAsString();
                if (!cur.has(k) || !cur.get(k).isJsonObject()) return false;
                cur = cur.getAsJsonObject(k);
            }
            String last = keys.get(keys.size() - 1).getAsString();
            if (cur.has(last)) {
                cur.remove(last);
                save();
                return true;
            }
            return false;
        } finally {
            w.unlock();
        }
    }
}
