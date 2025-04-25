package com.jumpcutfindo.onmymark.server.network.handlers;

import com.jumpcutfindo.onmymark.network.packets.serverbound.MarkEntityC2SPacket;
import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.party.exceptions.PartyNotFoundException;
import com.jumpcutfindo.onmymark.server.network.ServerNetworkSender;
import com.jumpcutfindo.onmymark.server.network.ServerPacketContext;
import com.jumpcutfindo.onmymark.server.network.ServerPacketHandler;
import com.jumpcutfindo.onmymark.server.party.ServerPartyManager;
import com.jumpcutfindo.onmymark.server.party.ServerPartyMember;
import com.jumpcutfindo.onmymark.utils.EntityUtils;
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

        try {
            Party<ServerPartyMember> party = serverPartyManager.getPartyOfPlayer(context.player());
            ServerPartyMember playerPartyMember = serverPartyManager.getOrCreatePlayer(context.player());
            ServerWorld world = context.player().getServerWorld();

            Entity entity = EntityUtils.getEntityByUuid(world, context.player().getPos(), payload.entityId());

            if (entity == null) {
                return;
            }

            for (ServerPartyMember partyMember : party.partyMembers()) {
                ServerNetworkSender.sendEntityMarker(partyMember.player(), context.player(), entity);
            }
        } catch (PartyNotFoundException e) {
            ServerNetworkSender.sendMessageToPlayer(context.player(), Text.translatable("onmymark.action.exception.invalidParty"));
        }
    }
}
