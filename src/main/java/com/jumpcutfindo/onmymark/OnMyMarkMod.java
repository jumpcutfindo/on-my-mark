package com.jumpcutfindo.onmymark;

import com.jumpcutfindo.onmymark.network.ServerNetworkSender;
import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.party.PartyManager;
import com.jumpcutfindo.onmymark.party.exceptions.PartyNotFoundException;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OnMyMarkMod implements ModInitializer {
	public static final String MOD_ID = "onmymark";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static PartyManager PARTY_MANAGER = null;

	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);

		ServerPlayConnectionEvents.JOIN.register(this::onEntityLoaded);
		ServerPlayConnectionEvents.DISCONNECT.register(this::onPlayerDisconnect);
	}

	public void onServerStarted(MinecraftServer server) {
		// Initialize the party system
		PARTY_MANAGER = new PartyManager();

		LOGGER.info("Party manager initialized!");
	}

	public void onEntityLoaded(ServerPlayNetworkHandler networkHandler, PacketSender packetSender, MinecraftServer minecraftServer) {
		ServerPlayerEntity serverPlayer = networkHandler.getPlayer();

		try {
			// Send party info the to the client
			Party party = PARTY_MANAGER.getPartyOfPlayer(serverPlayer);
			ServerNetworkSender.sendPartyInfo(serverPlayer, party);
		} catch (PartyNotFoundException e) {
			// Player has no party info, remove it from the client
			ServerNetworkSender.removePartyInfo(serverPlayer);
		}
	}

	public void onPlayerDisconnect(ServerPlayNetworkHandler networkHandler, MinecraftServer minecraftServer) {
		ServerPlayerEntity serverPlayer = networkHandler.getPlayer();

		// TODO: Persist user in server as long as server is running
		// For now, we remove the player when they leave the server
		PARTY_MANAGER.removeInvite(serverPlayer);
		PARTY_MANAGER.leaveParty(serverPlayer);
	}
}