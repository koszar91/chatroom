package client;

import util.message.Message;
import util.message.MessageType;
import util.network.NetworkComponent;
import util.network.TCPNetworkComponent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Client {

    private String username = "New user";
    private final NetworkComponent networkComponent;
    private final NetworkThread networkThread;

    public Client(String address, int port) throws IOException, ClassNotFoundException {
        var socket = SocketChannel.open();
        socket.connect(new InetSocketAddress(address, port));
        var in = new ObjectInputStream(socket.socket().getInputStream());
        var out = new ObjectOutputStream(socket.socket().getOutputStream());
        this.networkComponent = new TCPNetworkComponent(socket, in, out);
        this.networkThread = new NetworkThread(networkComponent);
        register();
    }

    public void run() {
        Scanner console = new Scanner(System.in);
        networkThread.start();

        boolean shouldExit = false;
        while (!shouldExit) {
            try {
                String input = console.nextLine();
                if (input.contains("--exit")) {
                    shouldExit = true;
                } else if (input.contains("--list")) {
                    networkComponent.sendMessage(new Message(MessageType.LIST, "", username));
                } else if (!input.isEmpty()) {
                    networkComponent.sendMessage(new Message(MessageType.TEXT, input, username));
                }
            } catch (IOException e) {
                System.err.println("Unable to send the message.");
                shouldExit = true;
            }
        }

        networkThread.cancel();
        try {
            networkThread.join();
        } catch (InterruptedException ignored) {}
        networkComponent.shutdown();
    }

    private void register() throws IOException, ClassNotFoundException {
        boolean registered = false;
        do {
            Scanner console = new Scanner(System.in);
            System.out.print("Enter username: ");
            String potentialName = console.nextLine();
            networkComponent.sendMessage(new Message(MessageType.REGISTER, potentialName));

            Message reply = networkComponent.receiveMessage();
            if (reply.getType() == MessageType.REGISTER_OK) {
                System.out.println("Successfully joined the chat.");
                this.username = potentialName;
                registered = true;
            } else if (reply.getType() == MessageType.REGISTER_NOT_OK) {
                System.out.println("Cannot choose this name. Try another one.");
            } else {
                System.out.println("Server rejected. Try again.");
            }

        } while (!registered);
    }

    public static void main(String[] args) {
        String address = "localhost";
        int port = 12345;
        try {
            Client client = new Client(address, port);
            client.run();
        } catch (IOException e) {
            System.err.printf("Server on address %s on port %d is not responding.\n", address, port);
        } catch (ClassNotFoundException e) {
            System.err.println("Communication failed (send wrong message object).");
        }
    }
}
