package util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.channels.SocketChannel;

public record TCPConnection(
        SocketChannel socket,
        ObjectInputStream in,
        ObjectOutputStream out
) {
    public Message receiveMessage() throws IOException, ClassNotFoundException {
        return (Message) in.readObject();
    }

    public void sendMessage(Message message) throws IOException {
        out.writeObject(message);
    }

    public void shutdown() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ignored) { }
    }
}
