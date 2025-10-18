package server;

import com.google.gson.*;
import org.junit.jupiter.api.*;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class CommandFactoryAndCommandsTest {

    static Path tempDb;
    JsonDatabase db;

    @BeforeAll
    static void setupAll() throws Exception {
        tempDb = Files.createTempFile("cmd_db_test", ".json");
        Files.writeString(tempDb, "{}");
    }

    @AfterAll
    static void tearDownAll() throws Exception {
        Files.deleteIfExists(tempDb);
    }

    @BeforeEach
    void setUp() {
        db = new JsonDatabase(tempDb.toString());
    }

    @AfterEach
    void clean() throws Exception {
        Files.writeString(tempDb, "{}");
    }

    @Test
    void setAndGetViaCommands() {
        // prepare request JSON for SET
        JsonObject reqSet = new JsonObject();
        reqSet.addProperty("type", "set");
        JsonArray keyArr = new JsonArray();
        keyArr.add("person");
        keyArr.add("name");
        reqSet.add("key", keyArr);
        reqSet.add("value", new JsonPrimitive("Elon"));

        Command setCmd = CommandFactory.createCommand(db, reqSet);
        JsonObject setResp = setCmd.execute();
        assertEquals("OK", setResp.get("response").getAsString());

        // now GET
        JsonObject reqGet = new JsonObject();
        reqGet.addProperty("type", "get");
        reqGet.add("key", keyArr);

        Command getCmd = CommandFactory.createCommand(db, reqGet);
        JsonObject getResp = getCmd.execute();
        assertEquals("OK", getResp.get("response").getAsString());
        assertEquals("Elon", getResp.get("value").getAsString());
    }

    @Test
    void deleteViaCommand() {
        db.set(new String[] {"x"}, new JsonPrimitive(1));

        JsonObject reqDel = new JsonObject();
        reqDel.addProperty("type", "delete");
        reqDel.add("key", new JsonArray());
        reqDel.getAsJsonArray("key").add("x");

        Command delCmd = CommandFactory.createCommand(db, reqDel);
        JsonObject delResp = delCmd.execute();
        assertEquals("OK", delResp.get("response").getAsString());

        assertNull(db.get(new String[] {"x"}));
    }

    @Test
    void invalidCommandTest() {
        JsonObject reqInvalid = new JsonObject();
        reqInvalid.addProperty("type", "nonexistent");

        Command invalidCmd = CommandFactory.createCommand(db, reqInvalid);
        JsonObject resp = invalidCmd.execute();

        assertEquals("ERROR", resp.get("response").getAsString());
        assertEquals("Unknown command type: nonexistent", resp.get("reason").getAsString());
    }

    @Test
    void exitCommandTest() {
        JsonObject reqExit = new JsonObject();
        reqExit.addProperty("type", "exit");

        Command exitCmd = CommandFactory.createCommand(db, reqExit);
        JsonObject resp = exitCmd.execute();

        assertEquals("OK", resp.get("response").getAsString());
    }
}
