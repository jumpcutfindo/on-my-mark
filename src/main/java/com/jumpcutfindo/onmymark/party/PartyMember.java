package com.jumpcutfindo.onmymark.party;

import net.minecraft.entity.player.PlayerEntity;

import java.util.Objects;
import java.util.UUID;

public class PartyMember {
    private UUID partyMemberId;
    private Party currentParty;
    private String displayName;
    private PlayerEntity player;
    private PartyMember.State state;

    public PartyMember(PlayerEntity player) {
        this.player = player;

        this.partyMemberId = player.getUuid();
        this.displayName = player.getDisplayName().getString();

        this.state = State.AVAILABLE;
    }

    public PartyMember(UUID playerId, String displayName) {
        this.partyMemberId = playerId;
        this.displayName = displayName;

        this.state = State.IN_OTHER_WORLD;
    }

    public String displayName() {
        return this.displayName;
    }

    public Party currentParty() {
        return currentParty;
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

    public boolean isPartyLeader() {
        return this.currentParty != null && this.currentParty.isPartyLeader(this);
    }

    public PlayerEntity player() {
        return player;
    }

    public PartyMember.State state() {
        return state;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PartyMember that)) return false;
        return Objects.equals(partyMemberId, that.partyMemberId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(partyMemberId);
    }

    public enum State {
        // TODO: Add handling for "OFFLINE" state
        AVAILABLE, IN_PARTY, IN_OTHER_WORLD
    }
}
