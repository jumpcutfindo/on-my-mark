package com.jumpcutfindo.onmymark.party;

import java.util.Objects;
import java.util.UUID;

public abstract class PartyMember {
    protected UUID id;
    protected String displayName;

    protected PartyMember.State state;

    protected int color;

    public PartyMember(UUID playerId, String displayName, int color, PartyMember.State initialState) {
        this.id = playerId;
        this.displayName = displayName;

        this.color = color;
        this.state = initialState;
    }

    public UUID id() {
        return this.id;
    }

    public String displayName() {
        return this.displayName;
    }

    public int color() {
        return this.color;
    }

    public PartyMember.State state() {
        return state;
    }

    public boolean isInParty() {
        return this.state == PartyMember.State.IN_PARTY;
    }

    public boolean isOffline() {
        return this.state == PartyMember.State.OFFLINE;
    }

    public abstract boolean isPartyLeader();

    public abstract Party<? extends PartyMember> currentParty();

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PartyMember that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public enum State {
        AVAILABLE, IN_PARTY, OFFLINE
    }
}
