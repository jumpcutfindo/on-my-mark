package com.jumpcutfindo.onmymark.server.network.handlers;

import com.jumpcutfindo.onmymark.network.packets.serverbound.KickPlayerC2SPacket;
import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.server.network.ServerNetworkSender;
import com.jumpcutfindo.onmymark.server.network.ServerPacketContext;
import com.jumpcutfindo.onmymark.server.network.ServerPacketHandler;
import com.jumpcutfindo.onmymark.server.party.ServerPartyManager;
import com.jumpcutfindo.onmymark.server.party.ServerPartyMember;
import com.jumpcutfindo.onmymark.server.party.exceptions.InvalidPartyPermissionsException;
import com.jumpcutfindo.onmymark.server.party.exceptions.PartyNotFoundException;
import com.jumpcutfindo.onmymark.server.party.exceptions.PlayerNotInPartyException;
import com.jumpcutfindo.onmymark.server.party.exceptions.RemovePartyLeaderException;
import com.jumpcutfindo.onmymark.server.utils.ServerEntityUtils;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/**
 * Handler for when a party leader attempts to kick a player out of the party
 */
public class KickPlayerC2SHandler implements ServerPacketHandler<KickPlayerC2SPacket> {
    @Override
    public void handle(KickPlayerC2SPacket payload, ServerPacketContext context) {
        ServerPartyManager partyManager = context.partyManager();
        ServerPlayerEntity player = context.player();
        ServerPlayerEntity otherPlayer = ServerEntityUtils.getPlayerById(context, payload.playerId());

        if (otherPlayer == null) {
            ServerNetworkSender.sendMessageToPlayer(context.player(), Text.translatable("text.action.onmymark.exception.playerNotFound"));
            return;
        }

        try {
            Party<ServerPartyMember> party = partyManager.getPartyOfPlayer(player);
            partyManager.removePlayerFromParty(party.partyId(), player, otherPlayer);

            // Remove player's markers
            ServerNetworkSender.removeMarker(party, otherPlayer);

            ServerNetworkSender.sendMessageToPlayer(otherPlayer, Text.translatable("text.action.onmymark.onKickFromParty.self"));
            ServerNetworkSender.removePartyInfo(otherPlayer);

            ServerNetworkSender.sendMessageToParty(party, Text.translatable("text.action.onmymark.onKickFromParty.other", otherPlayer.getName()));
            ServerNetworkSender.sendPartyInfo(party);
        } catch (PartyNotFoundException e) {
            ServerNetworkSender.sendMessageToPlayer(context.player(), Text.translatable("text.action.onmymark.exception.invalidParty"));
        } catch (RemovePartyLeaderException e) {
            ServerNetworkSender.sendMessageToPlayer(context.player(), Text.translatable("text.action.onmymark.exception.removePartyLeader"));
        } catch (PlayerNotInPartyException e) {
            ServerNetworkSender.sendMessageToPlayer(context.player(), Text.translatable("text.action.onmymark.exception.playerNotFound"));
        } catch (InvalidPartyPermissionsException e) {
            ServerNetworkSender.sendMessageToPlayer(context.player(), Text.translatable("text.action.onmymark.exception.invalidPermissions"));
        }
    }
}
