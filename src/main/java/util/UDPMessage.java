package util;

import java.net.SocketAddress;

public record UDPMessage(SocketAddress address, Message message) {}
