package server;

import common.message.Message;
import common.message.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class ClientHandlingThread extends Thread {

    private String username;
    private final SynchronizedClientRegister clientsRegister;
    private final Socket socket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;

    public ClientHandlingThread(Socket socket, SynchronizedClientRegister clientsRegister) throws IOException {
        this.socket = socket;
        this.clientsRegister = clientsRegister;
        this.username = "New user";
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
        System.out.println("Client " + socket.getPort() + " connected");
    }

    private void processMessage(Message message) throws IOException {
        switch (message.getType()) {
            case REGISTER: handleRegistration(message.getText()); break;
            case TEXT: broadcastMessage(message);
            case LIST: sendList(message);
        }
    }

    @Override
    public void run() {
        try {
            while (socket.isConnected()) {
                processMessage(this.receiveMessage());
            }
        } catch (SocketException e) {
            System.out.println(username + " disconnected.");
            clientsRegister.removeClient(username);
        } catch (Exception e) {
            System.err.println("Exception in client handler!\n" +
                    "Client: " + socket + "\n" +
                    "Exception type: " + e.getClass() + "\n" +
                    "Exception cause: " + e.getMessage() + "\n"
            );
        }
        shutdown();
    }

    private Message receiveMessage() throws IOException, ClassNotFoundException {
        return (Message) in.readObject();
    }

    private void sendMessage(Message message) throws IOException {
        out.writeObject(message);
    }

    private void shutdown() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Unable to close " + socket);
        }
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            System.err.println("Unable to close " + in);
        }
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            System.err.println("Unable to close " + out);
        }
    }

    private void handleRegistration(String name) throws IOException {
        try {
            clientsRegister.addClient(name, new ClientData(name, super.getId(), out, in));
            username = name;
            System.out.println("Client " + socket.getPort() + " registered as " + name + ".");
            sendMessage(new Message(MessageType.REGISTER_ACK, "0"));
        } catch (IllegalArgumentException e) {
            sendMessage(new Message(MessageType.REGISTER_NACK, "0"));
        }
    }

    private void broadcastMessage(Message message) {
        message.setAuthor(username);
        for (ClientData clientData : clientsRegister.toCollection()) {
            if (clientData.getName().equals(username)) {
                continue;
            }
            try {
                clientData.getOut().writeObject(message);
            } catch (IOException e) {
                System.err.println("IO exception broadcasting message " + message);
            }
        }
    }

    private void sendList(Message message) {

    }
}
