package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private final int port;
    private final ServerSocket serverSocket;
    private final SynchronizedClientRegister clientsRegister;

    public Server(int port) throws IOException {
        this.port = port;
        this.clientsRegister = new SynchronizedClientRegister();
        this.serverSocket = new ServerSocket(port);
    }

    public void run() {
        System.out.printf("Running server on port %d.\n", port);
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                try {
                    Thread thread = new ClientHandlingThread(socket, clientsRegister);
                    thread.start();
                } catch (IOException e) {
                    System.err.printf("Could not create thread for client %s.\n", socket.getPort());
                }
            }
        } catch (IOException e) {
            System.err.println("IO Exception while waiting for a connection.");
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.err.println("IO Exception while closing the server socket.");
            }
        }
    }

    public static void main(String[] args) {

        int port = 12345;

        Server server;

        try {
            server = new Server(port);
        } catch (IOException e) {
            System.err.printf("Could not open server socket on port %d.\n", port);
            return;
        }

        server.run();

    }
}
