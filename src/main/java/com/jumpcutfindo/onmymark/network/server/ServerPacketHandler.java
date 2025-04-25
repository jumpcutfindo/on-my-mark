package com.jumpcutfindo.onmymark.network.server;

/**
 * Interface for handling packets received by the server
 * @param <T> Type of the packet being handled
 */
public interface ServerPacketHandler<T> {
    void handle(T payload, ServerPacketContext context);
}
