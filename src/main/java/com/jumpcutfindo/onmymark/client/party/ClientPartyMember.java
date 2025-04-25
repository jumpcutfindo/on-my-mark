package com.jumpcutfindo.onmymark.client.party;

import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.party.PartyMember;
import com.mojang.authlib.GameProfile;

import java.util.UUID;

public class ClientPartyMember extends PartyMember {
    private Party<ClientPartyMember> currentParty;
    private final boolean isPartyLeader;

    public ClientPartyMember(UUID playerId, String displayName, boolean isPartyLeader, PartyMember.State state) {
        super(playerId, displayName, state);

        this.isPartyLeader = isPartyLeader;
    }

    @Override
    public boolean isPartyLeader() {
        return isPartyLeader;
    }

    public void setCurrentParty(Party<ClientPartyMember> currentParty) {
        this.currentParty = currentParty;
    }

    public int getPartyIndex() {
        return this.currentParty.partyMembers().indexOf(this);
    }

    @Override
    public Party<? extends PartyMember> currentParty() {
        return currentParty;
    }

    public GameProfile gameProfile() {
        return new GameProfile(id, displayName);
    }
}
