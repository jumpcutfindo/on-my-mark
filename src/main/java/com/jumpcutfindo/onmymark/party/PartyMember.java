package com.jumpcutfindo.onmymark.party;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Objects;

public class PartyMember {
    private Party currentParty;
    private ServerPlayerEntity player;
    private PartyMember.State state;

    public PartyMember(ServerPlayerEntity player) {
        this.player = player;
        this.state = State.AVAILABLE;
    }

    public String displayName() {
        return player.getDisplayName().getString();
    }

    public Party currentParty() {
        return currentParty;
    }

    public void setCurrentParty(Party party) {
        this.currentParty = party;
        this.state = State.IN_PARTY;
    }

    public void removeCurrentParty() {
        this.currentParty = null;
        this.state = State.AVAILABLE;
    }

    public ServerPlayerEntity player() {
        return player;
    }

    public PartyMember.State state() {
        return state;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PartyMember that)) return false;
        return Objects.equals(player, that.player);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(player);
    }

    public enum State {
        // TODO: Add handling for "OFFLINE" state
        AVAILABLE, IN_PARTY
    }
}
