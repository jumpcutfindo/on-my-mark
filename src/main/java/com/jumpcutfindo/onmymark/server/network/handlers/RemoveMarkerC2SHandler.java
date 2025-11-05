package com.jumpcutfindo.onmymark.server.network.handlers;

import com.jumpcutfindo.onmymark.network.packets.serverbound.RemoveMarkerC2SPacket;
import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.server.network.ServerNetworkSender;
import com.jumpcutfindo.onmymark.server.network.ServerPacketContext;
import com.jumpcutfindo.onmymark.server.network.ServerPacketHandler;
import com.jumpcutfindo.onmymark.server.party.ServerPartyManager;
import com.jumpcutfindo.onmymark.server.party.ServerPartyMember;
import com.jumpcutfindo.onmymark.server.party.exceptions.PartyNotFoundException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

/**
 * Handler for when a party member attempts to remove a marker
 */
public class RemoveMarkerC2SHandler implements ServerPacketHandler<RemoveMarkerC2SPacket> {
    @Override
    public void handle(RemoveMarkerC2SPacket payload, ServerPacketContext context) {
        ServerPartyManager serverPartyManager = context.partyManager();

        try {
            Party<ServerPartyMember> party = serverPartyManager.getPartyOfPlayer(context.player());

            ServerWorld world = context.player().getEntityWorld();
            PlayerEntity markerPlayer = world.getPlayerByUuid(payload.markerPlayerId());

            for (ServerPartyMember partyMember : party.partyMembers()) {
                ServerNetworkSender.removeMarker(partyMember.player(), (ServerPlayerEntity) markerPlayer);
            }
        } catch (PartyNotFoundException e) {
            ServerNetworkSender.sendMessageToPlayer(context.player(), Text.translatable("text.action.onmymark.exception.invalidParty"));
        }
    }
}
