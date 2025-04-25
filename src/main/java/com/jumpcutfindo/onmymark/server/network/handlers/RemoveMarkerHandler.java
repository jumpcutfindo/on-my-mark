package com.jumpcutfindo.onmymark.server.network.handlers;

import com.jumpcutfindo.onmymark.network.packets.RemoveMarkerPacket;
import com.jumpcutfindo.onmymark.server.network.ServerNetworkSender;
import com.jumpcutfindo.onmymark.server.network.ServerPacketContext;
import com.jumpcutfindo.onmymark.server.network.ServerPacketHandler;
import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.party.PartyMemberFilters;
import com.jumpcutfindo.onmymark.server.party.ServerPartyManager;
import com.jumpcutfindo.onmymark.server.party.ServerPartyMember;
import com.jumpcutfindo.onmymark.party.exceptions.PartyNotFoundException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

/**
 * Handler for when a party member attempts to remove a marker
 */
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
