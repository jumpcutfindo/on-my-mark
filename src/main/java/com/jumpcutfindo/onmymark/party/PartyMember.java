package com.jumpcutfindo.onmymark.party;

import java.util.Objects;
import java.util.UUID;

public abstract class PartyMember {
    UUID id;
    String displayName;

    Party currentParty;
    PartyMember.State state;

    public PartyMember(UUID playerId, String displayName, PartyMember.State initialState) {
        this.id = playerId;
        this.displayName = displayName;

        this.state = initialState;
    }

    public UUID id() {
        return this.id;
    }

    public String displayName() {
        return this.displayName;
    }

    public PartyMember.State state() {
        return state;
    }

    public Party currentParty() {
        return currentParty;
    }

    public boolean isInParty() {
        return currentParty != null && currentParty.state() == Party.State.ACTIVE;
    }

    public int getPartyIndex() {
        return currentParty != null ? currentParty.partyMembers().indexOf(this) : -1;
    }

    public void setCurrentParty(Party party) {
        assert (party != null): "Use `PartyMember#removeCurrentParty` if you wish to remove the player's current party";

        this.currentParty = party;
        this.state = State.IN_PARTY;
    }

    public void removeCurrentParty() {
        this.currentParty = null;
        this.state = State.AVAILABLE;
    }

    public boolean isOffline() {
        return this.state == PartyMember.State.OFFLINE;
    }

    public abstract boolean isPartyLeader();

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
