package com.jumpcutfindo.onmymark.server.network.handlers;

import com.jumpcutfindo.onmymark.network.packets.serverbound.UpdateColorC2SPacket;
import com.jumpcutfindo.onmymark.server.network.ServerNetworkSender;
import com.jumpcutfindo.onmymark.server.network.ServerPacketContext;
import com.jumpcutfindo.onmymark.server.network.ServerPacketHandler;
import com.jumpcutfindo.onmymark.server.party.ServerPartyManager;
import com.jumpcutfindo.onmymark.server.party.ServerPartyMember;
import com.jumpcutfindo.onmymark.server.party.exceptions.PartyNotFoundException;
import net.minecraft.text.Text;

public class UpdateColorC2SHandler implements ServerPacketHandler<UpdateColorC2SPacket> {
    @Override
    public void handle(UpdateColorC2SPacket payload, ServerPacketContext context) {
        ServerPartyManager serverPartyManager = context.partyManager();

        try {
            ServerPartyMember partyMember = serverPartyManager.getOrCreatePlayer(context.player());

            if (!partyMember.isInParty()) {
                throw new PartyNotFoundException();
            }

            partyMember.setColor(payload.color());
            ServerNetworkSender.sendPartyInfo(partyMember.currentParty());
        } catch (PartyNotFoundException e) {
            ServerNetworkSender.sendMessageToPlayer(context.player(), Text.translatable("onmymark.action.exception.invalidParty"));
        }
    }
}
