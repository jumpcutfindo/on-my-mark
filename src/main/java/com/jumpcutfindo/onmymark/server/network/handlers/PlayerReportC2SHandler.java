package com.jumpcutfindo.onmymark.server.network.handlers;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import com.jumpcutfindo.onmymark.marker.PlayerMarker;
import com.jumpcutfindo.onmymark.network.packets.serverbound.PlayerReportC2SPacket;
import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.server.marker.ServerMarkerManager;
import com.jumpcutfindo.onmymark.server.marker.exceptions.UnhandledMarkerException;
import com.jumpcutfindo.onmymark.server.network.ServerNetworkSender;
import com.jumpcutfindo.onmymark.server.network.ServerPacketContext;
import com.jumpcutfindo.onmymark.server.network.ServerPacketHandler;
import com.jumpcutfindo.onmymark.server.party.ServerPartyManager;
import com.jumpcutfindo.onmymark.server.party.ServerPartyMember;
import com.jumpcutfindo.onmymark.server.party.exceptions.PartyNotFoundException;
import com.jumpcutfindo.onmymark.utils.StringUtils;
import net.minecraft.text.Text;

public class PlayerReportC2SHandler implements ServerPacketHandler<PlayerReportC2SPacket> {
    @Override
    public void handle(PlayerReportC2SPacket payload, ServerPacketContext context) {
        ServerPartyManager serverPartyManager = context.partyManager();
        ServerMarkerManager serverMarkerManager = context.markerManager();

        try {
            Party<ServerPartyMember> party = serverPartyManager.getPartyOfPlayer(context.player());
            ServerPartyMember markerPartyMember = serverPartyManager.getOrCreatePlayer(context.player());

            PlayerMarker playerMarker = new PlayerMarker(markerPartyMember, context.player().getWorld().getRegistryKey(), context.player().getUuid(), context.player().getDisplayName().getString(), context.player().getPos());
            serverMarkerManager.addMarker(party, playerMarker);

            for (ServerPartyMember partyMember : party.partyMembers()) {
                ServerNetworkSender.sendPlayerMarker(partyMember.player(), markerPartyMember, playerMarker);
                ServerNetworkSender.sendMessageToPlayer(
                        partyMember.player(),
                        Text.translatable(
                                "onmymark.action.playerReport.playerLocation",
                                markerPartyMember.displayName(),
                                StringUtils.coordinatesAsFancyText(context.player().getX(), context.player().getY(), context.player().getZ(), markerPartyMember.color())
                        )
                );
            }
        } catch (PartyNotFoundException e) {
            ServerNetworkSender.sendMessageToPlayer(context.player(), Text.translatable("onmymark.action.exception.invalidParty"));
        } catch (UnhandledMarkerException e) {
            OnMyMarkMod.LOGGER.error("Unhandled marker type");
        }
    }
}
