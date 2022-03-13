package server;

import util.TCPConnection;

import java.net.SocketAddress;

public class ClientRecord{
    private final String name;
    private SocketAddress udpAddress;
    private final TCPConnection tcpConnection;

    public ClientRecord(String name, TCPConnection tcpConnection) {
        this.name = name;
        this.tcpConnection = tcpConnection;
    }

    public void setUdpAddress(SocketAddress address) {
        this.udpAddress = address;
    }

    public String name() {
        return name;
    }

    public SocketAddress udpAddress() {
        return udpAddress;
    }

    public TCPConnection tcpConnection() {
        return tcpConnection;
    }
}
