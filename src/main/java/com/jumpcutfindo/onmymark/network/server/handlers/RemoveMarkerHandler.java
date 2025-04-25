package com.jumpcutfindo.onmymark.network.server.handlers;

import com.jumpcutfindo.onmymark.network.packets.RemoveMarkerPacket;
import com.jumpcutfindo.onmymark.network.server.ServerNetworkSender;
import com.jumpcutfindo.onmymark.network.server.ServerPacketContext;
import com.jumpcutfindo.onmymark.network.server.ServerPacketHandler;
import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.party.PartyMemberFilters;
import com.jumpcutfindo.onmymark.party.ServerPartyManager;
import com.jumpcutfindo.onmymark.party.ServerPartyMember;
import com.jumpcutfindo.onmymark.party.exceptions.PartyNotFoundException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

public class RemoveMarkerHandler implements ServerPacketHandler<RemoveMarkerPacket> {
    @Override
    public void handle(RemoveMarkerPacket payload, ServerPacketContext context) {
        ServerPartyManager serverPartyManager = context.partyManager();

        try {
            Party<ServerPartyMember> party = serverPartyManager.getPartyOfPlayer(context.player());
            ServerPartyMember playerPartyMember = serverPartyManager.getOrCreatePlayer(context.player());

            ServerWorld world = context.player().getServerWorld();
            PlayerEntity markerPlayer = world.getPlayerByUuid(payload.markerPlayerId());

            PartyMemberFilters.SameDimensionFilter sameDimensionFilter = new PartyMemberFilters.SameDimensionFilter(playerPartyMember);
            for (ServerPartyMember partyMember : party.partyMembers(sameDimensionFilter)) {
                ServerNetworkSender.removeMarker(partyMember.player(), (ServerPlayerEntity) markerPlayer);
            }
        } catch (PartyNotFoundException e) {
            ServerNetworkSender.sendMessageToPlayer(context.player(), Text.translatable("onmymark.action.exception.invalidParty"));
        }
    }
}
