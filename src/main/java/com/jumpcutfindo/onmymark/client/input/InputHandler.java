package com.jumpcutfindo.onmymark.client.input;

import net.minecraft.client.MinecraftClient;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;

public abstract class InputHandler {
    protected final MinecraftClient client;

    private final Deque<Instant> delayDeque;

    public InputHandler(MinecraftClient client) {
        this.client = client;

        this.delayDeque = new ArrayDeque<>();
    }

    private void updateDelayDeque() {
        Instant currTime = Instant.now();

        // Attempt to remove delay blockers
        while (!delayDeque.isEmpty() && currTime.toEpochMilli() - delayDeque.peek().toEpochMilli() > this.inputDelayMs()) {
            delayDeque.pop();
        }
    }

    protected void addDelay() {
        delayDeque.push(Instant.now());
    }

    public boolean canExecute() {
        this.updateDelayDeque();
        return delayDeque.size() < maxConcurrentInputs();
    }

    public boolean execute() {
        this.addDelay();
        return true;
    }

    public abstract int maxConcurrentInputs();
    public abstract long inputDelayMs();
}
