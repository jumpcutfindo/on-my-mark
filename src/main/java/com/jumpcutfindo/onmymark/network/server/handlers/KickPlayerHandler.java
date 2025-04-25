package com.jumpcutfindo.onmymark.network.server.handlers;

import com.jumpcutfindo.onmymark.network.server.ServerNetworkSender;
import com.jumpcutfindo.onmymark.network.packets.KickPlayerPacket;
import com.jumpcutfindo.onmymark.network.server.ServerPacketContext;
import com.jumpcutfindo.onmymark.network.server.ServerPacketHandler;
import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.party.ServerPartyManager;
import com.jumpcutfindo.onmymark.party.ServerPartyMember;
import com.jumpcutfindo.onmymark.party.exceptions.InvalidPartyPermissionsException;
import com.jumpcutfindo.onmymark.party.exceptions.PartyNotFoundException;
import com.jumpcutfindo.onmymark.party.exceptions.PlayerNotInPartyException;
import com.jumpcutfindo.onmymark.party.exceptions.RemovePartyLeaderException;
import com.jumpcutfindo.onmymark.utils.EntityUtils;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class KickPlayerHandler implements ServerPacketHandler<KickPlayerPacket> {
    @Override
    public void handle(KickPlayerPacket payload, ServerPacketContext context) {
        ServerPartyManager partyManager = context.partyManager();
        ServerPlayerEntity player = context.player();
        ServerPlayerEntity otherPlayer = EntityUtils.getPlayerById(context, payload.playerId());

        if (otherPlayer == null) {
            ServerNetworkSender.sendMessageToPlayer(context.player(), Text.translatable("onmymark.action.exception.playerNotFound"));
            return;
        }

        try {
            Party<ServerPartyMember> party = partyManager.getPartyOfPlayer(player);
            partyManager.removePlayerFromParty(party.partyId(), player, otherPlayer);

            ServerNetworkSender.sendMessageToPlayer(otherPlayer, Text.translatable("onmymark.action.onKickFromParty.self"));
            ServerNetworkSender.removePartyInfo(otherPlayer);

            ServerNetworkSender.sendMessageToParty(party, Text.translatable("onmymark.action.onKickFromParty.other", otherPlayer.getName()));
            ServerNetworkSender.sendPartyInfo(party);
        } catch (PartyNotFoundException e) {
            ServerNetworkSender.sendMessageToPlayer(context.player(), Text.translatable("onmymark.action.exception.invalidParty"));
        } catch (RemovePartyLeaderException e) {
            ServerNetworkSender.sendMessageToPlayer(context.player(), Text.translatable("onmymark.action.exception.removePartyLeader"));
        } catch (PlayerNotInPartyException e) {
            ServerNetworkSender.sendMessageToPlayer(context.player(), Text.translatable("onmymark.action.exception.playerNotFound"));
        } catch (InvalidPartyPermissionsException e) {
            ServerNetworkSender.sendMessageToPlayer(context.player(), Text.translatable("onmymark.action.exception.invalidPermissions"));
        }
    }
}
