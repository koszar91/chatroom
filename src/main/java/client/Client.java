package client;

import util.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Client {

    private final DatagramChannel multicastSocket;
    private final UDPListeningThread multicastListeningThread;
    private String username = "New user";
    private final int serverPort;
    private final TCPConnection tcpConnection;
    private final TCPListeningThread tcpListeningThread;
    private final String multicastAddress;
    private final UDPListeningThread udpListeningThread;
    private final DatagramChannel udpSocket;

    public Client(String serverAddress, int port, String multicastAddress) throws IOException, ClassNotFoundException {
        this.serverPort = port;

        // setup tcp
        var tcpSocket = SocketChannel.open();
        tcpSocket.connect(new InetSocketAddress(serverAddress, port));
        var in = new ObjectInputStream(tcpSocket.socket().getInputStream());
        var out = new ObjectOutputStream(tcpSocket.socket().getOutputStream());
        this.tcpConnection = new TCPConnection(tcpSocket, in, out);
        this.tcpListeningThread = new TCPListeningThread(tcpConnection);

        // setup udp
        this.udpSocket = DatagramChannel.open().bind(null);
        this.udpListeningThread = new UDPListeningThread(udpSocket);

        // setup udp multicast
        NetworkInterface ni = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
        InetAddress group = InetAddress.getByName(multicastAddress);
        this.multicastAddress = multicastAddress;
        this.multicastSocket = DatagramChannel.open(StandardProtocolFamily.INET)
                .setOption(StandardSocketOptions.SO_REUSEADDR, true)
                .bind(new InetSocketAddress(serverPort))
                .setOption(StandardSocketOptions.IP_MULTICAST_IF, ni);
        this.multicastSocket.join(group, ni);
        this.multicastListeningThread = new UDPListeningThread(multicastSocket);

        register();
    }

    public void run() {
        Scanner console = new Scanner(System.in);
        tcpListeningThread.start();
        udpListeningThread.start();
        multicastListeningThread.start();

        boolean shouldExit = false;
        while (!shouldExit) {
            try {
                String input = console.nextLine();
                if (input.contains("--exit")) {
                    shouldExit = true;
                } else if (input.contains("--list")) {
                    tcpConnection.sendMessage(new Message(MessageType.LIST, "", username));
                } else if (input.contains("-U")) {
                    sendUDPMessage(new Message(MessageType.TEXT, input, username), false);
                } else if (input.contains("-M")) {
                    sendUDPMessage(new Message(MessageType.TEXT, input, username), true);
                 }else if (!input.isEmpty()) {
                    tcpConnection.sendMessage(new Message(MessageType.TEXT, input, username));
                }
            } catch (IOException e) {
                System.err.println("Unable to send the message.");
                shouldExit = true;
            }
        }

        tcpListeningThread.cancel();
        udpListeningThread.cancel();
        multicastListeningThread.cancel();
        try {
            tcpListeningThread.join();
            udpListeningThread.join();
            multicastListeningThread.join();
        } catch (InterruptedException ignored) {}
        tcpConnection.shutdown();
        shutdownUDP();
    }

    private void sendUDPMessage(Message message, boolean multicast) {
        SocketAddress destinationAddress = multicast ?
                new InetSocketAddress(multicastAddress, serverPort) :
                new InetSocketAddress("localhost", serverPort);

        UDPMessage udpMessage = new UDPMessage(destinationAddress, message);
        try {
            UDPUtils.sendMessage(udpSocket, udpMessage);
        } catch (IOException e) {
            System.err.println("IO Exception while sending UDP message to server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void register() throws IOException, ClassNotFoundException {
        // register using tcp
        boolean registered = false;
        do {
            Scanner console = new Scanner(System.in);
            System.out.print("Enter username: ");
            String potentialName = console.nextLine();
            tcpConnection.sendMessage(new Message(MessageType.REGISTER, potentialName));
            Message reply = tcpConnection.receiveMessage();
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

        // register udp
        sendUDPMessage(new Message(MessageType.REGISTER, ".", username), false);
    }

    private void shutdownUDP() {
        try {
            udpSocket.close();
            multicastSocket.close();
        } catch (IOException ignored) { }
    }

    public static void main(String[] args) {
        String address = "localhost";
        int port = 12345;
        String multicastAddress = "239.1.2.3";
        try {
            Client client = new Client(address, port, multicastAddress);
            client.run();
        } catch (BindException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.printf("Server not responding.\n");
        } catch (ClassNotFoundException e) {
            System.err.println("Communication failed (send wrong message object).");
        }
    }
}
