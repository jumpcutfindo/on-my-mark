package com.jumpcutfindo.onmymark.network;

import com.jumpcutfindo.onmymark.client.ClientMarkerManager;
import com.jumpcutfindo.onmymark.client.ClientPartyManager;
import com.jumpcutfindo.onmymark.client.OnMyMarkClientMod;
import com.jumpcutfindo.onmymark.graphics.screen.party.PartyInviteWindow;
import com.jumpcutfindo.onmymark.graphics.screen.party.PartyScreen;
import com.jumpcutfindo.onmymark.graphics.screen.toast.OnMyMarkToast;
import com.jumpcutfindo.onmymark.marker.BlockMarker;
import com.jumpcutfindo.onmymark.marker.EntityMarker;
import com.jumpcutfindo.onmymark.network.packets.*;
import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.party.PartyInvite;
import com.jumpcutfindo.onmymark.party.PartyMember;
import com.jumpcutfindo.onmymark.utils.EntityUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;

public class ClientNetworkReceiver implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        onPartyInfo();
        onRemovePartyInfo();
        onInviteToPartyResponse();

        onPartyInvitation();

        onMarkBlock();
        onMarkEntity();
        onRemoveMarker();
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
            ClientPartyManager partyManager = OnMyMarkClientMod.INSTANCE.clientPartyManager();
            PartyInvite partyInvite = packet.toPartyInvite(context.player().getWorld(), context.player());

            partyManager.setPartyInvite(partyInvite);

            OnMyMarkToast.addPartyInvitationToast(context.client(), partyInvite);

            // Update screen if the player is looking
            if (context.client().currentScreen instanceof PartyScreen partyScreen) {
                partyScreen.setActiveWindow(new PartyInviteWindow(partyScreen, partyInvite));
            }
        });
    }

    public static void onMarkBlock() {
        ClientPlayNetworking.registerGlobalReceiver(MarkBlockPacket.PACKET_ID, (packet, context) -> {
            ClientPartyManager partyManager = OnMyMarkClientMod.INSTANCE.clientPartyManager();
            Party party = partyManager.party();

            if (party.hasMemberWithId(packet.playerId())){
                ClientMarkerManager markerManager = OnMyMarkClientMod.INSTANCE.clientMarkerManager();
                PartyMember partyMember = party.getMemberWithId(packet.playerId());

                markerManager.setMarker(partyMember, new BlockMarker(partyMember, packet.blockPos(), context.client().world.getBlockState(packet.blockPos())));
            }
        });
    }

    public static void onMarkEntity() {
        ClientPlayNetworking.registerGlobalReceiver(MarkEntityPacket.PACKET_ID, (packet, context) -> {
            ClientWorld world = context.client().world;

            Entity entity = EntityUtils.getEntityByUuid(world, context.player().getPos(), packet.entityId());

            ClientPartyManager partyManager = OnMyMarkClientMod.INSTANCE.clientPartyManager();
            Party party = partyManager.party();

            if (party.hasMemberWithId(packet.playerId())){
                ClientMarkerManager markerManager = OnMyMarkClientMod.INSTANCE.clientMarkerManager();
                PartyMember partyMember = party.getMemberWithId(packet.playerId());

                markerManager.setMarker(partyMember, new EntityMarker(partyMember, entity));
            }
        });
    }

    public static void onRemoveMarker() {
        ClientPlayNetworking.registerGlobalReceiver(RemoveMarkerPacket.PACKET_ID, (packet, context) -> {
            ClientPartyManager partyManager = OnMyMarkClientMod.INSTANCE.clientPartyManager();
            Party party = partyManager.party();

            if (party.hasMemberWithId(packet.markerPlayerId())) {
                ClientMarkerManager markerManager = OnMyMarkClientMod.INSTANCE.clientMarkerManager();
                PartyMember partyMember = party.getMemberWithId(packet.markerPlayerId());

                markerManager.removeMarkerOf(partyMember);
            }
        });
    }
}
