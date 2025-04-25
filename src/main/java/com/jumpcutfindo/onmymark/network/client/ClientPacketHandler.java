package com.jumpcutfindo.onmymark.network.client;

/**
 * Interface for handling packets received by the client
 * @param <T> Type of the packet being handled
 */
public interface ClientPacketHandler<T> {
    void handle(T payload, ClientPacketContext context);
}
