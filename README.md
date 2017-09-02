Description of Stream project to keep connected to server and show stream of
JSON objects being sent.

1. LineStreamConnection
  A thread connected to the server with open http connection.
  It reads data line by line and posts it to a Listener i.e. MainActivity
  Handles exceptions and error cases

2. MainActivity
  sets up the UI, adapter etc.
  Registers for network connectivity broadcast
  On network availability,
    1. opens connection described above
    2. Starts a timer to schedule UI update, in a batch, every 1 second

3. ListAdapter
  Manages the data to be shown and works with the UI widgets
  fills any text views etc.

4.  Model
  Represents the data coming from the wire to be used across various widgets
  Parses the JSON strings into usable model classes.
