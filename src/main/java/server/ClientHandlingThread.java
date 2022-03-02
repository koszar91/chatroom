package server;

import util.color.Color;
import util.color.ColorPrinter;
import util.message.Message;
import util.message.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Collection;

public class ClientHandlingThread extends Thread {

    private final SynchronizedClientRegister clientsRegister;
    private final Socket socket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private String username;

    public ClientHandlingThread(Socket socket, SynchronizedClientRegister clientsRegister) throws IOException {
        this.socket = socket;
        this.clientsRegister = clientsRegister;
        this.username = "New user";
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
        ColorPrinter.printInColor(Color.GREEN, "Client " + socket.getPort() + " connected");
    }

    private void processMessage(Message message) throws IOException {
        switch (message.getType()) {
            case REGISTER -> handleRegistration(message.getText());
            case TEXT -> broadcastMessage(message);
            case LIST -> sendList();
        }
    }

    @Override
    public void run() {
        try {
            while (socket.isConnected()) {
                processMessage(this.receiveMessage());
            }
        } catch (Exception e) {
            ColorPrinter.printInColor(Color.RED, "Client " + socket.getPort() + " (" + username + ") disconnected.");
            clientsRegister.removeClient(username);
        } finally {
            shutdown();
        }
    }

    private void handleRegistration(String name) throws IOException {
        try {
            clientsRegister.addClient(name, new ClientRecord(name, out, in));
            username = name;
            System.out.println("Client " + socket.getPort() + " registered as " + name + ".");
            sendMessage(new Message(MessageType.REGISTER_OK, ""));
        } catch (IllegalArgumentException e) {
            sendMessage(new Message(MessageType.REGISTER_NOT_OK, ""));
        }
    }

    private void broadcastMessage(Message message) {
        message.setAuthor(username);
        for (ClientRecord client : clientsRegister.toCollection()) {
            if (client.name().equals(username)) {
                continue;
            }
            try {
                client.out().writeObject(message);
            } catch (IOException e) {
                System.err.println("IO exception broadcasting message " + message);
            }
        }
    }

    private void sendList() throws IOException {
        Collection<ClientRecord> clients = clientsRegister.toCollection();

        String usernames = clients
                .stream()
                .map(client -> " " + client.name() + " ")
                .reduce("", String::concat);

        String text = Color.YELLOW.ansiCode + "[" + usernames + "]" + Color.RESET.ansiCode;
        sendMessage(new Message(MessageType.LIST, text, "server"));
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

}
