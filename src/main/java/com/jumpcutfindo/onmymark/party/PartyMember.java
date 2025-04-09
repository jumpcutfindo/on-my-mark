package com.jumpcutfindo.onmymark.party;

import net.minecraft.entity.player.PlayerEntity;

import java.util.Objects;

public class PartyMember {
    private Party currentParty;
    private PlayerEntity player;
    private PartyMember.State state;

    public PartyMember(PlayerEntity player) {
        this.player = player;
        this.state = State.AVAILABLE;
    }

    public String displayName() {
        return player.getDisplayName().getString();
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
