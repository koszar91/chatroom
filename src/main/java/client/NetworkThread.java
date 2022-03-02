package client;

import util.color.Color;
import util.color.ColorPrinter;
import util.message.Message;
import util.network.NetworkComponent;
import java.io.IOException;

public class NetworkThread extends Thread {

    private final NetworkComponent networkComponent;

    public NetworkThread(NetworkComponent networkComponent) {
        this.networkComponent = networkComponent;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                Message message = networkComponent.receiveMessage();
                displayMessage(message);
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

    private void displayMessage(Message message) {
        Color color = switch(message.getType()) {
            case LIST                   -> Color.YELLOW;
            case USER_NEW, USER_LEFT    -> Color.CYAN;
            default                     -> Color.WHITE;
        };
        if (message.getAuthor().equals("server")) {
            ColorPrinter.printInColor(color, message.getText());
        } else {
            ColorPrinter.printInColor(color, message.getAuthor() + ": " + message.getText());
        }
    }
}
