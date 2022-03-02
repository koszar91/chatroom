package client;

import util.message.Message;
import util.message.MessageType;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Scanner;

public class UserInputThread extends Thread {

    private final Thread mainThread;
    private final ObjectOutputStream out;
    private final Scanner consoleScanner;

    public UserInputThread(Thread mainThread, ObjectOutputStream out, Scanner consoleScanner) {
        super();
        this.mainThread = mainThread;
        this.out = out;
        this.consoleScanner = consoleScanner;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                String input = consoleScanner.nextLine();
                if (input.contains("--exit")) {
                    mainThread.interrupt();
                    return;
                } else if (input.contains("--list")) {
                    out.writeObject(new Message(MessageType.LIST, ""));
                } else if (!input.isEmpty()) {
                    out.writeObject(new Message(MessageType.TEXT, input));
                }
            } catch (IOException e) {
                System.err.println("Unable to send the message.");
                break;
            }
        }
    }
}
