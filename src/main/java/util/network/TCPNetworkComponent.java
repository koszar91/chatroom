package util.network;

import util.message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.channels.SocketChannel;

public record TCPNetworkComponent(
        SocketChannel socket,
        ObjectInputStream in,
        ObjectOutputStream out
) implements NetworkComponent {

    @Override
    public Message receiveMessage() throws IOException, ClassNotFoundException {
        return (Message) in.readObject();
    }

    @Override
    public void sendMessage(Message message) throws IOException {
        out.writeObject(message);
    }

    @Override
    public void shutdown() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Unable to close " + socket);
        }
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            System.err.println("Unable to close " + in);
        }
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            System.err.println("Unable to close " + out);
        }
    }
}
