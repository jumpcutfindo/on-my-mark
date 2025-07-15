package com.jumpcutfindo.onmymark.server.network.handlers;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import com.jumpcutfindo.onmymark.marker.EntityMarker;
import com.jumpcutfindo.onmymark.network.packets.serverbound.MarkEntityC2SPacket;
import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.server.marker.ServerMarkerManager;
import com.jumpcutfindo.onmymark.server.marker.exceptions.UnhandledMarkerException;
import com.jumpcutfindo.onmymark.server.network.ServerNetworkSender;
import com.jumpcutfindo.onmymark.server.network.ServerPacketContext;
import com.jumpcutfindo.onmymark.server.network.ServerPacketHandler;
import com.jumpcutfindo.onmymark.server.party.ServerPartyManager;
import com.jumpcutfindo.onmymark.server.party.ServerPartyMember;
import com.jumpcutfindo.onmymark.server.party.exceptions.PartyNotFoundException;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

/**
 * Handler for when a party member is attempting to mark an entity
 */
public class MarkEntityC2SHandler implements ServerPacketHandler<MarkEntityC2SPacket> {
    @Override
    public void handle(MarkEntityC2SPacket payload, ServerPacketContext context) {
        ServerPartyManager serverPartyManager = context.partyManager();
        ServerMarkerManager serverMarkerManager = context.markerManager();

        try {
            Party<ServerPartyMember> party = serverPartyManager.getPartyOfPlayer(context.player());
            ServerPartyMember markerPartyMember = serverPartyManager.getOrCreatePlayer(context.player());
            ServerWorld world = context.player().getWorld();

            Entity entity = world.getEntity(payload.entityId());

            if (entity == null) {
                return;
            }

            EntityMarker entityMarker = new EntityMarker(markerPartyMember, context.player().getWorld().getRegistryKey(), entity.getUuid(), entity.getName().getString(), entity.getPos());
            serverMarkerManager.addMarker(party, entityMarker);

            for (ServerPartyMember partyMember : party.partyMembers()) {
                ServerNetworkSender.sendEntityMarker(partyMember.player(), markerPartyMember, entityMarker);
            }
        } catch (PartyNotFoundException e) {
            ServerNetworkSender.sendMessageToPlayer(context.player(), Text.translatable("text.action.onmymark.exception.invalidParty"));
        } catch (UnhandledMarkerException e) {
            OnMyMarkMod.LOGGER.error("Unhandled marker type");
        }
    }
}
