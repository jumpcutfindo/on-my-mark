package com.jumpcutfindo.onmymark.client.party;

import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.party.PartyMember;
import com.mojang.authlib.GameProfile;

import java.util.UUID;

public class ClientPartyMember extends PartyMember {
    private Party<ClientPartyMember> currentParty;
    private final boolean isPartyLeader;

    private final GameProfile gameProfile;

    public ClientPartyMember(UUID playerId, String displayName, boolean isPartyLeader, int color, PartyMember.State state, GameProfile gameProfile) {
        super(playerId, displayName, color, state);

        this.isPartyLeader = isPartyLeader;

        this.gameProfile = gameProfile;
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
        return this.gameProfile;
    }
}
