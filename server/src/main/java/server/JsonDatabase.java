package server;

import com.google.gson.*;
import java.io.*;
import java.nio.file.*;
import java.util.concurrent.locks.*;

public class JsonDatabase {

    private final Path dbPath;
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock(true);
    private final Lock readLock = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();
    private JsonObject database;


    public JsonDatabase(String dbFilePath) {
        this.dbPath = Paths.get(dbFilePath);
        load();
    }

    /** Loads the database from file (or creates an empty one if missing). */
    private void load() {
        writeLock.lock();
        try {
            if (Files.exists(dbPath)) {
                String content = Files.readString(dbPath);
                if (!content.isBlank()) {
                    database = JsonParser.parseString(content).getAsJsonObject();
                } else {
                    database = new JsonObject();
                }
            } else {
                Files.createDirectories(dbPath.getParent());
                Files.createFile(dbPath);
                database = new JsonObject();
                save(); // persist empty file
            }
        } catch (IOException e) {
            e.printStackTrace();
            database = new JsonObject();
        } finally {
            writeLock.unlock();
        }
    }

    /** Saves current database to file (with write lock). */
    private void save() {
        writeLock.lock();
        try (Writer writer = Files.newBufferedWriter(dbPath)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(database, writer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writeLock.unlock();
        }
    }

    /** Gets a value by nested key. Returns null if not found. */
    public JsonElement get(String[] keys) {
        readLock.lock();
        try {
            JsonElement current = database;
            for (String key : keys) {
                if (!(current instanceof JsonObject) || !((JsonObject) current).has(key)) {
                    return null;
                }
                current = ((JsonObject) current).get(key);
            }
            return current;
        } finally {
            readLock.unlock();
        }
    }

    /** Sets a nested value, creating missing parent objects if needed. */
    public void set(String[] keys, JsonElement value) {
        writeLock.lock();
        try {
            JsonObject current = database;
            for (int i = 0; i < keys.length - 1; i++) {
                String key = keys[i];
                if (!current.has(key) || !current.get(key).isJsonObject()) {
                    JsonObject newObj = new JsonObject();
                    current.add(key, newObj);
                }
                current = current.getAsJsonObject(key);
            }
            current.add(keys[keys.length - 1], value);
            save();
        } finally {
            writeLock.unlock();
        }
    }

    /** Deletes a value by nested key. Returns true if deleted. */
    public boolean delete(String[] keys) {
        writeLock.lock();
        try {
            JsonObject current = database;
            for (int i = 0; i < keys.length - 1; i++) {
                String key = keys[i];
                if (!current.has(key) || !current.get(key).isJsonObject()) {
                    return false;
                }
                current = current.getAsJsonObject(key);
            }
            boolean removed = current.remove(keys[keys.length - 1]) != null;
            if (removed) {
                save();
            }
            return removed;
        } finally {
            writeLock.unlock();
        }
    }

}
