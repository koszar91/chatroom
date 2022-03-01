package server;

import java.util.*;

public class SynchronizedClientRegister {

    private final Map<String, ClientData> clients;

    public SynchronizedClientRegister() {
        this.clients = new HashMap<>();
    }

    public synchronized void addClient(String name, ClientData data) throws IllegalArgumentException {
        if (clients.containsKey(name)) {
            throw new IllegalArgumentException("Client with this name already exists.");
        } else {
            clients.put(name, data);
        }
    }

    public synchronized void removeClient(String name) {
        clients.remove(name);
    }

    public synchronized Collection<ClientData> toCollection() {
        return clients.values();
    }
}
