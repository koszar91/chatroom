package util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Arrays;

public class UDPUtils {

    private static final int BUFFER_SIZE = 2048;

    public static UDPMessage receiveMessage(DatagramChannel socket) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        var address = socket.receive(buffer);
        buffer.flip();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return new UDPMessage(address, bytesToMessage(bytes));
    }

    public static void sendMessage(DatagramChannel socket, UDPMessage udpMessage) throws IOException {
        byte[] bytes = messageToBytes(udpMessage.message());
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        socket.send(buffer, udpMessage.address());
    }

    private static Message bytesToMessage(byte[] bytes) {
        String[] fields = new String(bytes).split(";");
        MessageType type = MessageType.values()[Integer.parseInt(fields[0])];
        String author = fields[1];
        String text = fields[2];
        return new Message(type, text, author);
    }

    private static byte[] messageToBytes(Message message) {
        int typeInt = -1;
        for (int i = 0; i < MessageType.values().length; i++) {
            if (message.getType() == MessageType.values()[i]) {
                typeInt = i;
                break;
            }
        }
        return (typeInt + ";" + message.getAuthor() + ";" + message.getText()).getBytes();
    }
}
