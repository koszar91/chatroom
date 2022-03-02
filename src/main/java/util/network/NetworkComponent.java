package util.network;

import util.message.Message;

import java.io.IOException;

public interface NetworkComponent {
    Message receiveMessage() throws IOException, ClassNotFoundException;
    void sendMessage(Message message) throws IOException;
    void shutdown();
}
