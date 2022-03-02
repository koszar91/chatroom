package server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public record ClientRecord(String name, ObjectOutputStream out, ObjectInputStream in) { }
