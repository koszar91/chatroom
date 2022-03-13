package client;

import util.Message;
import util.UDPMessage;
import util.UDPUtils;

import java.io.IOException;
import java.nio.channels.DatagramChannel;

public class UDPListeningThread  extends Thread {

    private final DatagramChannel socket;

    public UDPListeningThread(DatagramChannel socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                UDPMessage udpMessage = UDPUtils.receiveMessage(socket);
                MessageDisplayer.displayMessage(deleteUDPMarker(udpMessage.message()));
            } catch (IOException e) {
                System.err.println("Disconnecting.");
                System.exit(0);
            }
        }
    }

    private Message deleteUDPMarker(Message message) {
        String text = message.getText().replaceAll("-U ", "").replaceAll("-M ", "");
        return new Message(message.getType(), text, message.getAuthor());
    }

    public void cancel() {
        if (isAlive()) {
            interrupt();
        }
    }
}
