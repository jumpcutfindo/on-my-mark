package com.jumpcutfindo.onmymark.network.server.handlers;

import com.jumpcutfindo.onmymark.network.packets.MarkEntityPacket;
import com.jumpcutfindo.onmymark.network.server.ServerNetworkSender;
import com.jumpcutfindo.onmymark.network.server.ServerPacketContext;
import com.jumpcutfindo.onmymark.network.server.ServerPacketHandler;
import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.party.PartyMemberFilters;
import com.jumpcutfindo.onmymark.party.ServerPartyManager;
import com.jumpcutfindo.onmymark.party.ServerPartyMember;
import com.jumpcutfindo.onmymark.party.exceptions.PartyNotFoundException;
import com.jumpcutfindo.onmymark.utils.EntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

/**
 * Handler for when a party member is attempting to mark an entity
 */
public class MarkEntityHandler implements ServerPacketHandler<MarkEntityPacket> {
    @Override
    public void handle(MarkEntityPacket payload, ServerPacketContext context) {
        ServerPartyManager serverPartyManager = context.partyManager();

        try {
            Party<ServerPartyMember> party = serverPartyManager.getPartyOfPlayer(context.player());
            ServerPartyMember playerPartyMember = serverPartyManager.getOrCreatePlayer(context.player());
            ServerWorld world = context.player().getServerWorld();

            Entity entity = EntityUtils.getEntityByUuid(world, context.player().getPos(), payload.entityId());

            if (entity == null) {
                return;
            }

            // Send markers only to players in the same dimension
            PartyMemberFilters.SameDimensionFilter sameDimensionFilter = new PartyMemberFilters.SameDimensionFilter(playerPartyMember);
            for (ServerPartyMember partyMember : party.partyMembers(sameDimensionFilter)) {
                ServerNetworkSender.sendEntityMarker(partyMember.player(), context.player(), entity);
            }
        } catch (PartyNotFoundException e) {
            ServerNetworkSender.sendMessageToPlayer(context.player(), Text.translatable("onmymark.action.exception.invalidParty"));
        }
    }
}
