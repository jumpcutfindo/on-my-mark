package com.jumpcutfindo.onmymark.party;

import net.minecraft.server.network.ServerPlayerEntity;

public class ServerPartyMember extends PartyMember {
    private ServerPlayerEntity player;

    public ServerPartyMember(ServerPlayerEntity player) {
        super(player.getUuid(), player.getDisplayName().getString(), State.AVAILABLE);

        this.player = player;
    }

    public ServerPlayerEntity player() {
        return player;
    }

    @Override
    public boolean isPartyLeader() {
        return this.currentParty.isPartyLeader(this);
    }
}
