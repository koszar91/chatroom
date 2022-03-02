package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {

    private final ServerSocket serverSocket;
    private final SynchronizedClientRegister clientsRegister;

    public Server(int port) throws IOException {
        this.clientsRegister = new SynchronizedClientRegister();
        this.serverSocket = new ServerSocket(port);
    }

    public void run() {
        runInputThread();
        System.out.printf("Running server on port %d.\n", serverSocket.getLocalPort());
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                try {
                    new ClientHandlingThread(socket, clientsRegister).start();
                } catch (IOException e) {
                    System.err.printf("Could not create thread for client %s.\n", socket.getPort());
                }
            }
        } catch (Exception e) {
            System.err.println("IO Exception while waiting for a connection.");
        } finally {
            shutdown();
        }
    }

    private void runInputThread() {
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                var scanner = new Scanner(System.in);
                if (scanner.nextLine().contains("--stop")) {
                    shutdown();
                }
            }
        }).start();
    }

    private void shutdown() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.err.println("IO Exception while closing the server socket.");
        }
        System.out.println("Server shut down.");
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
