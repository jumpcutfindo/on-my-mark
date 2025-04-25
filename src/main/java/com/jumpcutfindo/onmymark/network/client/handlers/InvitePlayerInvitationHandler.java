package com.jumpcutfindo.onmymark.network.client.handlers;

import com.jumpcutfindo.onmymark.client.ClientPartyManager;
import com.jumpcutfindo.onmymark.graphics.screen.party.PartyInviteWindow;
import com.jumpcutfindo.onmymark.graphics.screen.party.PartyScreen;
import com.jumpcutfindo.onmymark.graphics.screen.toast.OnMyMarkToast;
import com.jumpcutfindo.onmymark.network.client.ClientPacketContext;
import com.jumpcutfindo.onmymark.network.client.ClientPacketHandler;
import com.jumpcutfindo.onmymark.network.packets.InvitePlayerInvitationPacket;
import com.jumpcutfindo.onmymark.party.ClientPartyMember;
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
