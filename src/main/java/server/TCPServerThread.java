package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

public class TCPServerThread extends Thread {

    private final ServerSocketChannel serverSocket;
    private final SynchronizedClientRegister clientsRegister;
    private final List<TCPClientHandler> clientHandlingThreads;

    public TCPServerThread(int port, SynchronizedClientRegister clientsRegister) throws IOException {
        this.serverSocket = ServerSocketChannel.open();
        this.serverSocket.socket().bind(new InetSocketAddress(port));
        this.clientsRegister = clientsRegister;
        this.clientHandlingThreads = new ArrayList<>(3);
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                SocketChannel socket = serverSocket.accept();
                 var clientThread = new TCPClientHandler(socket, clientsRegister);
                 clientHandlingThreads.add(clientThread);
                 clientThread.start();
            } catch (ClosedByInterruptException e) {
                break;
            } catch (IOException e) {
                System.err.println("IO Exception on serverSocket.accept()");
                break;
            }
        }
        try {
            closeClientHandlers();
            serverSocket.close();
        } catch (IOException e) {
            System.err.println("IO Exception while closing the TCP server socket.");
        }
    }

    private void closeClientHandlers() {
        clientHandlingThreads.forEach(TCPClientHandler::cancel);
        for (Thread thread : clientHandlingThreads) {
            try {
                thread.join();
            } catch (InterruptedException ignored) {}
        }
    }

    public void cancel() {
        if (isAlive()) {
            interrupt();
        }
    }
}
