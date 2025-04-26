package com.jumpcutfindo.onmymark.server.network.handlers;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import com.jumpcutfindo.onmymark.marker.BlockMarker;
import com.jumpcutfindo.onmymark.network.packets.serverbound.MarkBlockC2SPacket;
import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.server.party.exceptions.PartyNotFoundException;
import com.jumpcutfindo.onmymark.server.marker.ServerMarkerManager;
import com.jumpcutfindo.onmymark.server.marker.exceptions.UnhandledMarkerException;
import com.jumpcutfindo.onmymark.server.network.ServerNetworkSender;
import com.jumpcutfindo.onmymark.server.network.ServerPacketContext;
import com.jumpcutfindo.onmymark.server.network.ServerPacketHandler;
import com.jumpcutfindo.onmymark.server.party.ServerPartyManager;
import com.jumpcutfindo.onmymark.server.party.ServerPartyMember;
import net.minecraft.block.BlockState;
import net.minecraft.text.Text;
import net.minecraft.world.World;

/**
 * Handler for when a party member is attempting to mark a block
 */
public class MarkBlockC2SHandler implements ServerPacketHandler<MarkBlockC2SPacket> {
    @Override
    public void handle(MarkBlockC2SPacket payload, ServerPacketContext context) {
        ServerPartyManager serverPartyManager = context.partyManager();
        ServerMarkerManager serverMarkerManager = context.markerManager();

        try {
            Party<ServerPartyMember> party = serverPartyManager.getPartyOfPlayer(context.player());
            ServerPartyMember markerPartyMember = serverPartyManager.getOrCreatePlayer(context.player());

            World markerWorld = context.player().getWorld();
            BlockState blockState = markerWorld.getBlockState(payload.blockPos());

            BlockMarker blockMarker = new BlockMarker(markerPartyMember, context.player().getWorld().getRegistryKey(), payload.blockPos(), blockState.getBlock());

            serverMarkerManager.addMarker(party, blockMarker);

            for (ServerPartyMember partyMember : party.partyMembers()) {
                ServerNetworkSender.sendBlockMarker(partyMember.player(), markerPartyMember, blockMarker);
            }
        } catch (PartyNotFoundException e) {
            ServerNetworkSender.sendMessageToPlayer(context.player(), Text.translatable("onmymark.action.exception.invalidParty"));
        } catch (UnhandledMarkerException e) {
            OnMyMarkMod.LOGGER.error("Unhandled marker type");
        }
    }
}
