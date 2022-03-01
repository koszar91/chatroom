package server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ClientData {

    private final String name;
    private final long threadID;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    public ClientData(String name, long threadID, ObjectOutputStream out, ObjectInputStream in) {
        this.name = name;
        this.threadID = threadID;
        this.out = out;
        this.in = in;
    }

    public String getName() {
        return name;
    }

    public long getThreadID() {
        return threadID;
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public ObjectInputStream getIn() {
        return in;
    }
}
