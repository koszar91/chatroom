package client;

import common.message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ServerListenerThread extends Thread {

    private final ObjectInputStream in;

    public ServerListenerThread(ObjectInputStream in) {
        super();
        this.in = in;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Message message = (Message) in.readObject();
                System.out.println(message.getAuthor() + ": " + message.getText());
            } catch (IOException e) {
                System.err.print("Exception while receiving message.\n" +
                        "Type" + e.getClass() + "\n" +
                        "Cause: " + e.getMessage() + "\n" +
                        "Stack trace: "
                );
                e.printStackTrace();
                break;
            } catch (ClassNotFoundException e) {
                System.err.println("Received unknown message.");
                break;
            }
        }
    }
}
