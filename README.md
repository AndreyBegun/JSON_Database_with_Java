<h3>🗄️ JSON Database (Client-Server Application in Java)</h3>

A multithreaded JSON-based key-value database built with Java and Gradle.
The project implements client-server communication over sockets, supporting commands like set, get, delete, and exit.
In the advanced stages, it supports nested JSON keys and path-based access.

<h3>🚀 Features</h3>

✅ Lightweight JSON database (stores and retrieves any JSON data) <br>
✅ Nested key access (e.g., ["person","name"]) <br>
✅ Multithreaded TCP server <br>
✅ Client-server communication with JSON messages <br>
✅ Supports set, get, delete, and exit commands <br>
✅ Configured as a multi-module Gradle project

<h3>📂 Project Structure</h3>
JSON_Database_with_Java/

├── common/   → shared JSON utilities & communication logic <br>
├── server/   → TCP server that manages the database <br>
└── client/   → CLI client that sends requests to the server

<h3>⚙️ How to Build and Run</h3>
🏗 Build the project <br>
./gradlew build

<h3>🖥 Start the Server</h3>

Run in one terminal:

./gradlew :server:run


Output:

Server started!

<h3>💬 Start the Client</h3>

In another terminal, run:

./gradlew :client:run --args="-t set -k text -v 'Hello World!'"


Output:

Client started!<br>
Sent: {<br>
"type":"set",<br>
"key":"text",<br>
"value":"Hello World!"<br>
}
Received: {"response":"OK"}

🧩 Example JSON Input (via file)

You can also use a .json input file:

./gradlew :client:run --args="-in getFile.json"

<h3>🧠 Example Commands</h3>
Command	Description	Example
set	Store a value	-t set -k text -v "Hello World!"
get	Retrieve a value	-t get -k text
delete	Remove a key	-t delete -k text
exit	Stop the server	-t exit
🧬 Nested JSON Example

Set nested object:

./gradlew :client:run --args='-in setPerson.json'


Contents of setPerson.json:

{
"type": "set", <br>
"key": "person", <br>
"value": { <br>
"name": "Elon Musk", <br>
"car": { "model": "Tesla Roadster", "year": "2018" }, <br>
"rocket": { "name": "Falcon 9", "launches": "87" } <br>
} <br>
}


Get nested field:

./gradlew :client:run --args='-in getSurname.json'


getSurname.json:

{<br>
"type": "get",<br>
"key": ["person","car","model"]<br>
}

<h3>🧰 Technologies Used</h3>

Java 17+

Gradle (multi-module build)

Gson (for JSON parsing)

ExecutorService (for concurrency)

<h3>🧑‍💻 Author</h3>

Andrei Biahun <br>

💻 GitHub: github.com/AndreyBegun
