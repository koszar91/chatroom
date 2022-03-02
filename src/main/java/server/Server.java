package server;

import java.io.*;
import java.util.Scanner;

public class Server {

    private final Scanner console = new Scanner(System.in);
    private final TCPServerThread tcpServerThread;
    private final SynchronizedClientRegister clientsRegister;

    public Server(int port) throws IOException {
        this.clientsRegister = new SynchronizedClientRegister();
        this.tcpServerThread = new TCPServerThread(port, clientsRegister);
    }

    public void run() {
        tcpServerThread.start();
        System.out.println("Server running.");
        while (!Thread.currentThread().isInterrupted()) {
            if (console.nextLine().contains("--stop")) { break; }
        }
        tcpServerThread.cancel();
        try {
            tcpServerThread.join();
        } catch (InterruptedException ignored) {}
        System.out.println("Server shut down.");
    }

    public static void main(String[] args) {
        int port = 12345;
        try {
            Server server = new Server(port);
            server.run();
        } catch (IOException e) {
            System.err.printf("Could not start server on port %d.\n", port);
        }
    }
}
