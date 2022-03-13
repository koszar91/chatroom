package client;

import util.Message;
import util.TCPConnection;
import java.io.IOException;

public class TCPListeningThread extends Thread {

    private final TCPConnection tcpConnection;

    public TCPListeningThread(TCPConnection tcpConnection) {
        this.tcpConnection = tcpConnection;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                Message message = tcpConnection.receiveMessage();
                MessageDisplayer.displayMessage(message);
            } catch (IOException e) {
                System.err.println("Disconnecting.");
                System.exit(0);
            } catch (ClassNotFoundException e) {
                System.err.println("Received unknown message.");
                break;
            }
        }
    }

    public void cancel() {
        if (isAlive()) {
            interrupt();
        }
    }
}
