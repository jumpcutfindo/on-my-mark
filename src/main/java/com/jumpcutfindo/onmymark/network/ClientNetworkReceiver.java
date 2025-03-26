package com.jumpcutfindo.onmymark.network;

import com.jumpcutfindo.onmymark.client.ClientPartyManager;
import com.jumpcutfindo.onmymark.client.OnMyMarkClientMod;
import com.jumpcutfindo.onmymark.graphics.screen.party.PartyScreen;
import com.jumpcutfindo.onmymark.network.packets.InviteToPartyResponsePacket;
import com.jumpcutfindo.onmymark.network.packets.PartyInfoPacket;
import com.jumpcutfindo.onmymark.network.packets.PartyInvitationRequestPacket;
import com.jumpcutfindo.onmymark.network.packets.RemovePartyInfoPacket;
import com.jumpcutfindo.onmymark.party.Party;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientNetworkReceiver implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        onPartyInfo();
        onRemovePartyInfo();
        onInviteToPartyResponse();

        onPartyInvitation();
    }

    public static void onPartyInfo() {
        ClientPlayNetworking.registerGlobalReceiver(PartyInfoPacket.PACKET_ID, ((partyInfoPacket, context) -> {
            ClientPartyManager partyManager = OnMyMarkClientMod.INSTANCE.clientPartyManager();
            Party party = partyInfoPacket.toParty(context.player().getWorld());
            partyManager.setParty(party);

            // Update screen if the player is looking
            if (context.client().currentScreen instanceof PartyScreen partyScreen) {
                partyScreen.setParty(party);
            }
        }));
    }

    public static void onRemovePartyInfo() {
        ClientPlayNetworking.registerGlobalReceiver(RemovePartyInfoPacket.PACKET_ID, ((removePartyInfoPacket, context) -> {
            ClientPartyManager partyManager = OnMyMarkClientMod.INSTANCE.clientPartyManager();
            partyManager.setParty(null);

            // Update screen if the player is looking
            if (context.client().currentScreen instanceof PartyScreen partyScreen) {
                partyScreen.setParty(null);
            }
        }));
    }

    public static void onInviteToPartyResponse() {
        ClientPlayNetworking.registerGlobalReceiver(InviteToPartyResponsePacket.PACKET_ID, ((inviteToPartyResponsePacket, context) -> {
            // TODO: Update window to close if invite was successful; if not, maybe show error?
        }));
    }

    public static void onPartyInvitation() {
        ClientPlayNetworking.registerGlobalReceiver(PartyInvitationRequestPacket.PACKET_ID, (packet, context) -> {
            // TODO: Implement storing of this invite somewhere
        });
    }
}
