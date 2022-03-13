package client;

import util.Message;
import util.color.Color;
import util.color.ColorPrinter;

public class MessageDisplayer {

    public static void displayMessage(Message message) {
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
