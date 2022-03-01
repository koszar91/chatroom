package client;

import common.message.Message;
import common.message.MessageType;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Scanner;

public class UserInputThread extends Thread {

    private final ObjectOutputStream out;
    private final Scanner consoleScanner;

    public UserInputThread(ObjectOutputStream out, Scanner consoleScanner) {
        super();
        this.out = out;
        this.consoleScanner = consoleScanner;
    }

    @Override
    public void run() {
        while (true) {
            try {
                String input = consoleScanner.nextLine();
                if (input.contains("--exit")) {
                    return;
                } else if (input.contains("--list")) {
                    out.writeObject(new Message(MessageType.LIST, ""));
                } else {
                    out.writeObject(new Message(MessageType.TEXT, input));
                }
            } catch (IOException e) {
                System.err.println("Could not send message :(");
                break;
            }

        }
    }
}
