package com.jumpcutfindo.onmymark.server.party;

import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.party.PartyMember;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerPartyMember extends PartyMember {
    private ServerPlayerEntity player;
    private Party<ServerPartyMember> currentParty;

    public ServerPartyMember(ServerPlayerEntity player, int color) {
        super(player.getUuid(), player.getDisplayName().getString(), color, State.AVAILABLE);

        this.player = player;
    }

    public void setPlayer(ServerPlayerEntity serverPlayer) {
        this.player = serverPlayer;
    }

    public ServerPlayerEntity player() {
        return player;
    }

    public void setState(PartyMember.State state) {
        this.state = state;
    }

    @Override
    public Party<ServerPartyMember> currentParty() {
        return currentParty;
    }

    public void setCurrentParty(Party<ServerPartyMember> currentParty) {
        this.currentParty = currentParty;
    }

    public void removeCurrentParty() {
        this.currentParty = null;
        this.setState(State.AVAILABLE);
    }

    @Override
    public boolean isPartyLeader() {
        return this.currentParty.isPartyLeader(this);
    }
}
