# About
Chatroom application using multithreading and client-server architecture created for Distributed Systems lessons at AGH UST.

Software is splitted into two parts:
- Server, listenieng for client connections and serving them in separate threads
- Client, able to register user's nickname and enter the chatroom to communicate with other using console

Communications is handled using TCP sockets. They are working at localhost, at port 1234.

# Setup
Tested with Java 17

# Usage
- Server (need to be run first) prints informations to the console and can be closed by typing --stop
- Clients can type and send messages. Also, they can use commands:
  - --exit (leave the chat)
  - --list (obtain the list of all currently online users)


