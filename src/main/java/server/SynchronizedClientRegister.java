package server;

import java.net.SocketAddress;
import java.util.*;

public class SynchronizedClientRegister {

    private final Map<String, ClientRecord> clients;

    public SynchronizedClientRegister() {
        this.clients = new HashMap<>();
    }

    public synchronized void addClient(String name, ClientRecord data) throws IllegalArgumentException {
        if (clients.containsKey(name)) {
            throw new IllegalArgumentException("Client with this name already exists.");
        } else {
            clients.put(name, data);
        }
    }

    public synchronized void removeClient(String name) {
        clients.remove(name);
    }

    public synchronized void setClientUDPAddress(String name, SocketAddress address) {
        clients.get(name).setUdpAddress(address);
    }

    public synchronized Collection<ClientRecord> toCollection() {
        return clients.values();
    }
}
