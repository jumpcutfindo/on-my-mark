package com.jumpcutfindo.onmymark.party;

import com.mojang.authlib.GameProfile;

import java.util.UUID;

public class ClientPartyMember extends PartyMember {
    private final boolean isPartyLeader;

    public ClientPartyMember(UUID playerId, String displayName, boolean isPartyLeader) {
        super(playerId, displayName, State.AVAILABLE);

        this.isPartyLeader = isPartyLeader;
    }

    @Override
    public boolean isPartyLeader() {
        return isPartyLeader;
    }

    public GameProfile gameProfile() {
        return new GameProfile(id, displayName);
    }
}
