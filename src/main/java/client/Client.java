package client;

import common.message.Message;
import common.message.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    public Client(String address, int port) throws IOException {
        this.socket = new Socket(address, port);
        this.in = new ObjectInputStream(socket.getInputStream());
        this.out = new ObjectOutputStream(socket.getOutputStream());
        registerAtServer();
    }

    public static void main(String[] args) {

        String address = "localhost";
        int port = 12345;

        Client client;

        try {
            client = new Client(address, port);
        } catch (IOException e) {
            System.err.printf("Could not create socket on address %s on port %d\n", address, port);
            return;
        }

        client.run();

    }

    public void run() {
        Thread userInputThread = new UserInputThread(out, new Scanner(System.in));
        userInputThread.start();

        Thread serverListenerThread = new ServerListenerThread(in);
        serverListenerThread.start();

        try {
            userInputThread.join();
            serverListenerThread.join();
        } catch (InterruptedException e) {
            System.out.println("Exiting...");
            shutdown();
        }
    }

    private String promptUsername() {
        Scanner consoleScanner = new Scanner(System.in);
        System.out.println("Enter username: ");
        return consoleScanner.nextLine();
    }

    private void registerAtServer() throws IOException {
        boolean registered = false;
        do {
            String potentialName = promptUsername();
            sendMessage(new Message(MessageType.REGISTER, potentialName));

            Message reply = receiveMessage();
            if (reply.getType() == MessageType.REGISTER_ACK) {
                System.out.println("Successfully registered.");
                registered = true;
            }

        } while (!registered);
    }

    private Message receiveMessage() throws IOException {
        Message msg = null;
        try {
            msg = (Message) in.readObject();
        } catch (ClassNotFoundException e) {
            System.err.println("Received unknown message!");
            e.printStackTrace();
            System.exit(-1);
        }
        return msg;
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
