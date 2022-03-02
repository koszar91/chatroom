package client;

import util.message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ServerListenerThread extends Thread {

    private final Thread mainThread;
    private final ObjectInputStream in;

    public ServerListenerThread(Thread mainThread, ObjectInputStream in) {
        super();
        this.mainThread = mainThread;
        this.in = in;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                Message message = (Message) in.readObject();
                System.out.println(message.getAuthor() + ": " + message.getText());
            } catch (IOException e) {
                System.err.println("Server went down. Disconnecting.");
                mainThread.interrupt();
                return;
            } catch (ClassNotFoundException e) {
                System.err.println("Received unknown message.");
                break;
            }
        }
    }
}
