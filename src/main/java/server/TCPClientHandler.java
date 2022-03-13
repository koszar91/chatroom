package server;

import util.color.Color;
import util.color.ColorPrinter;
import util.Message;
import util.MessageType;
import util.TCPConnection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Collection;

public class TCPClientHandler extends Thread {

    private final SynchronizedClientRegister clientsRegister;
    private final TCPConnection tcpConnection;
    private final SocketAddress remoteAddress;
    private String username = "New user";

    public TCPClientHandler(SocketChannel socket, SynchronizedClientRegister clientsRegister) throws IOException {
        this.clientsRegister = clientsRegister;
        var out = new ObjectOutputStream(socket.socket().getOutputStream());
        var in = new ObjectInputStream(socket.socket().getInputStream());
        this.tcpConnection = new TCPConnection(
                socket,
                in,
                out
        );
        this.remoteAddress = socket.getRemoteAddress();
        ColorPrinter.printInColor(Color.GREEN, "New client connected (" + remoteAddress + ")");
    }

    @Override
    public void run() {
        while (!interrupted()) {
            try {
                Message message = tcpConnection.receiveMessage();
                switch (message.getType()) {
                    case REGISTER   -> handleRegisterMessage(message);
                    case TEXT       -> handleTextMessage(message);
                    case LIST       -> handleListMessage();
                    default         -> { } // ignore
                }
            } catch (IOException e) {
                ColorPrinter.printInColor(Color.RED, "Client " + remoteAddress + " disconnected");
                broadcast(new Message(MessageType.USER_LEFT, username + " left the chat.", "server"));
                break;
            } catch (ClassNotFoundException e) {
                System.err.println("Received unknown message from client " + remoteAddress);
            }
        }
        clientsRegister.removeClient(username);
        tcpConnection.shutdown();
    }

    public void cancel() {
        if (isAlive()) {
            interrupt();
        }
    }

    private void handleRegisterMessage(Message message) throws IOException {
        try {
            String name = message.getText();
            if (name.equals("server")) {
                throw new IllegalArgumentException();
            }
            clientsRegister.addClient(
                    name,
                    new ClientRecord(name, tcpConnection)
            );
            username = name;
            System.out.println("Client " + remoteAddress + " registered as " + name + ".");
            tcpConnection.sendMessage(new Message(MessageType.REGISTER_OK, ""));
            broadcast(new Message(MessageType.USER_NEW, name + " joined the chat.", "server"));
        } catch (IllegalArgumentException e) {
            tcpConnection.sendMessage(new Message(MessageType.REGISTER_NOT_OK, ""));
        }
    }

    private void handleTextMessage(Message message) {
        message.setAuthor(username);
        broadcast(message);
    }


    private void handleListMessage() throws IOException {
        Collection<ClientRecord> clients = clientsRegister.toCollection();

        String usernames = "[" + clients
                .stream()
                .map(client -> " " + client.name() + " ")
                .reduce("", String::concat) + "]";

        tcpConnection.sendMessage(new Message(MessageType.LIST, usernames, "server"));
    }

    private void broadcast(Message message) {
        for (ClientRecord client : clientsRegister.toCollection()) {
            if (client.name().equals(username)) {
                continue;
            }
            try {
                client.tcpConnection().sendMessage(message);
            } catch (IOException ignored) { }
        }
    }


}
