package com.jumpcutfindo.onmymark.network.server;

public interface ServerPacketHandler<T> {
    void handle(T payload, ServerPacketContext context);
}
