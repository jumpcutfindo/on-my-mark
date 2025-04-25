package com.jumpcutfindo.onmymark.client.network.handlers;

import com.jumpcutfindo.onmymark.client.party.ClientPartyManager;
import com.jumpcutfindo.onmymark.client.graphics.screen.party.PartyInviteWindow;
import com.jumpcutfindo.onmymark.client.graphics.screen.party.PartyScreen;
import com.jumpcutfindo.onmymark.client.graphics.screen.toast.OnMyMarkToast;
import com.jumpcutfindo.onmymark.client.network.ClientPacketContext;
import com.jumpcutfindo.onmymark.client.network.ClientPacketHandler;
import com.jumpcutfindo.onmymark.network.packets.InvitePlayerInvitationPacket;
import com.jumpcutfindo.onmymark.client.party.ClientPartyMember;
import com.jumpcutfindo.onmymark.party.PartyInvite;

public class InvitePlayerInvitationHandler implements ClientPacketHandler<InvitePlayerInvitationPacket> {
    @Override
    public void handle(InvitePlayerInvitationPacket payload, ClientPacketContext context) {
        ClientPartyManager partyManager = context.partyManager();
        PartyInvite<ClientPartyMember> partyInvite = payload.toPartyInvite(context.player().getWorld(), context.player());

        partyManager.setPartyInvite(partyInvite);

        OnMyMarkToast.addPartyInvitationToast(context.client(), partyInvite);

        // Update screen if the player is looking
        if (context.client().currentScreen instanceof PartyScreen partyScreen) {
            partyScreen.setActiveWindow(new PartyInviteWindow(partyScreen, partyInvite));
        }
    }
}
