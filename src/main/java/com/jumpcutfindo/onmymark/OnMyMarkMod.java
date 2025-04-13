package com.jumpcutfindo.onmymark;

import com.jumpcutfindo.onmymark.network.ServerNetworkSender;
import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.party.PartyManager;
import com.jumpcutfindo.onmymark.party.exceptions.PlayerNotInPartyException;
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

		ServerPlayConnectionEvents.JOIN.register(this::onPlayerConnected);
		ServerPlayConnectionEvents.DISCONNECT.register(this::onPlayerDisconnected);
	}

	public void onServerStarted(MinecraftServer server) {
		// Initialize the party system
		PARTY_MANAGER = new PartyManager();

		LOGGER.info("Party manager initialized!");
	}

	public void onPlayerConnected(ServerPlayNetworkHandler networkHandler, PacketSender packetSender, MinecraftServer minecraftServer) {
		ServerPlayerEntity serverPlayer = networkHandler.getPlayer();

		try {
			Party party = PARTY_MANAGER.handlePlayerConnected(serverPlayer);
			ServerNetworkSender.sendPartyInfo(serverPlayer, party);
		} catch (PlayerNotInPartyException e) {
			// Player has no party info, remove it from the client
			ServerNetworkSender.removePartyInfo(serverPlayer);
		}
	}

	public void onPlayerDisconnected(ServerPlayNetworkHandler networkHandler, MinecraftServer minecraftServer) {
		ServerPlayerEntity serverPlayer = networkHandler.getPlayer();

		try {
			Party party = PARTY_MANAGER.handlePlayerDisconnected(serverPlayer);
			ServerNetworkSender.sendPartyInfo(serverPlayer, party);
		} catch (PlayerNotInPartyException e) {
			// Player has no party info, remove it from the client
			ServerNetworkSender.removePartyInfo(serverPlayer);
		}
	}
}