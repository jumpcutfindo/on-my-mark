package com.jumpcutfindo.onmymark.client.network.handlers;

import com.jumpcutfindo.onmymark.client.party.ClientPartyManager;
import com.jumpcutfindo.onmymark.client.graphics.screen.party.PartyInviteWindow;
import com.jumpcutfindo.onmymark.client.graphics.screen.party.PartyScreen;
import com.jumpcutfindo.onmymark.client.graphics.screen.toast.PartyInvitationToast;
import com.jumpcutfindo.onmymark.client.network.ClientPacketContext;
import com.jumpcutfindo.onmymark.client.network.ClientPacketHandler;
import com.jumpcutfindo.onmymark.network.packets.clientbound.InvitePlayerInvitationS2CPacket;
import com.jumpcutfindo.onmymark.client.party.ClientPartyMember;
import com.jumpcutfindo.onmymark.party.PartyInvite;

public class InvitePlayerInvitationS2CHandler implements ClientPacketHandler<InvitePlayerInvitationS2CPacket> {
    @Override
    public void handle(InvitePlayerInvitationS2CPacket payload, ClientPacketContext context) {
        ClientPartyManager partyManager = context.partyManager();
        PartyInvite<ClientPartyMember> partyInvite = payload.toPartyInvite(context.player().getEntityWorld(), context.player());

        partyManager.setPartyInvite(partyInvite);

        PartyInvitationToast.add(context.client().getToastManager(), partyInvite);

        // Update screen if the player is looking
        if (context.client().currentScreen instanceof PartyScreen partyScreen) {
            partyScreen.setActiveWindow(new PartyInviteWindow(partyScreen, partyInvite));
        }
    }
}

