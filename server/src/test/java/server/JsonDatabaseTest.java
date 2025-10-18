package server;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.*;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class JsonDatabaseTest {

    private static Path tempDb;
    private JsonDatabase db;

    @BeforeAll
    static void beforeAll() throws Exception {
        // create temp file for DB (unique per test run)
        tempDb = Files.createTempFile("db_test", ".json");
        // ensure empty content
        Files.writeString(tempDb, "{}");
    }

    @AfterAll
    static void afterAll() throws Exception {
        Files.deleteIfExists(tempDb);
    }

    @BeforeEach
    void setUp() {
        // construct JsonDatabase pointing to the temp file
        db = new JsonDatabase(tempDb.toString());
    }

    @AfterEach
    void tearDown() {
        // reset file content between tests
        try {
            Files.writeString(tempDb, "{}");
        } catch (Exception ignored) {}
    }

    @Test
    void testSetAndGetSimpleValue() {
        db.set(new String[] {"name"}, new JsonPrimitive("John"));
        JsonElement val = db.get(new String[] {"name"});
        assertNotNull(val);
        assertEquals("John", val.getAsString());
    }

    @Test
    void testSetAndGetNestedValue() {
        db.set(new String[] {"person", "surname"}, new JsonPrimitive("Smith"));
        db.set(new String[] {"person", "age"}, new JsonPrimitive(30));

        JsonElement surname = db.get(new String[] {"person", "surname"});
        JsonElement age = db.get(new String[] {"person", "age"});

        assertNotNull(surname);
        assertEquals("Smith", surname.getAsString());
        assertNotNull(age);
        assertEquals(30, age.getAsInt());
    }

    @Test
    void testDynamicObjectCreation() {
        db.set(new String[] {"a","b","c"}, new JsonPrimitive("deep"));
        JsonElement deep = db.get(new String[] {"a","b","c"});
        assertNotNull(deep);
        assertEquals("deep", deep.getAsString());

        // parent objects should exist
        JsonElement parent = db.get(new String[] {"a","b"});
        assertTrue(parent.isJsonObject());
        assertTrue(parent.getAsJsonObject().has("c"));
    }

    @Test
    void testDeleteNestedField() {
        db.set(new String[] {"person", "name"}, new JsonPrimitive("Alice"));
        db.set(new String[] {"person", "surname"}, new JsonPrimitive("Doe"));

        boolean deleted = db.delete(new String[] {"person","surname"});
        assertTrue(deleted);

        JsonElement remaining = db.get(new String[] {"person","name"});
        assertNotNull(remaining);
        assertEquals("Alice", remaining.getAsString());

        assertNull(db.get(new String[] {"person","surname"}));
    }

    @Test
    void testDeleteNonExistent() {
        boolean deleted = db.delete(new String[] {"not","exists"});
        assertFalse(deleted);
    }

    @Test
    void testGetNonExistent() {
        assertNull(db.get(new String[] {"foo"}));
    }

    @Test
    void testPersistenceAcrossInstances() {
        // set value with one instance
        db.set(new String[] {"persistent","value"}, new JsonPrimitive("yes"));

        // create new instance reading same temp file
        JsonDatabase db2 = new JsonDatabase(tempDb.toString());
        JsonElement val = db2.get(new String[] {"persistent","value"});
        assertNotNull(val);
        assertEquals("yes", val.getAsString());
    }

    @Test
    void testComplexJsonValue() {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", 12);
        obj.addProperty("name", "X");
        db.set(new String[] {"complex"}, obj);

        JsonElement loaded = db.get(new String[] {"complex"});
        assertNotNull(loaded);
        assertTrue(loaded.isJsonObject());
        assertEquals(12, loaded.getAsJsonObject().get("id").getAsInt());
    }
}
