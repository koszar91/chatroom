package server;

import util.MessageType;
import util.UDPMessage;
import util.UDPUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;

public class UDPServerThread extends Thread {

    private final SynchronizedClientRegister clientsRegister;
    private final DatagramChannel socket;

    public UDPServerThread(int localPort, SynchronizedClientRegister clientsRegister) throws IOException {
        this.clientsRegister = clientsRegister;
        this.socket = DatagramChannel.open();
        this.socket.bind(new InetSocketAddress("localhost", localPort));
    }

    @Override
    public void run() {
        while (!interrupted()) {
            try {
                UDPMessage udpMessage = UDPUtils.receiveMessage(socket);
                switch(udpMessage.message().getType()) {
                    case REGISTER -> updateUDPClientInfo(udpMessage);
                    case TEXT -> broadcast(udpMessage);
                    default -> { /* ignore */ }
                }
            } catch (IOException ignored) {
                // ignore, client probably disconnected
            }
        }
        shutdown();
    }

    private void updateUDPClientInfo(UDPMessage udpMessage) {
        clientsRegister.setClientUDPAddress(udpMessage.message().getAuthor(), udpMessage.address());
    }

    private void broadcast(UDPMessage udpMessage) {
        clientsRegister.toCollection().stream()
                .filter(clientRecord -> clientRecord.udpAddress() != null)
                .filter(clientRecord -> !clientRecord.udpAddress().equals(udpMessage.address()))
                .forEach(clientRecord -> {
                    try {
                        UDPUtils.sendMessage(
                                socket,
                                new UDPMessage(clientRecord.udpAddress(), udpMessage.message())
                        );
                    } catch (IOException e) {
                        System.err.println("IO exception broadcasting UDP message " + udpMessage.message());
                    }
                });
    }

    public void cancel() {
        if (isAlive()) {
            interrupt();
        }
    }

    private void shutdown() {
        try {
            socket.close();
        } catch (IOException ignored) { }
    }
}
