package com.jumpcutfindo.onmymark;

import com.jumpcutfindo.onmymark.network.ServerNetworkSender;
import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.party.PartyManager;
import com.jumpcutfindo.onmymark.party.exceptions.PartyNotFoundException;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OnMyMarkMod implements ModInitializer {
	public static final String MOD_ID = "onmymark";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static PartyManager PARTY_MANAGER = null;

	@Override
	public void onInitialize() {
		ServerLifecycleEvents
				.SERVER_STARTED
				.register(this::onServerStarted);

		ServerEntityEvents.ENTITY_LOAD.register(this::onEntityLoaded);

	}

	public void onServerStarted(MinecraftServer server) {
		// Initialize the party system
		PARTY_MANAGER = new PartyManager();
		LOGGER.info("Party manager initialized!");
	}

	public void onEntityLoaded(Entity entity, ServerWorld serverWorld) {
		if (entity instanceof ServerPlayerEntity serverPlayer) {
			// Send party info the to the client
			try {
				Party party = PARTY_MANAGER.getPartyOfPlayer(serverPlayer);
				ServerNetworkSender.sendPartyInfo(serverPlayer, party);
			} catch (PartyNotFoundException e) {
				// Player has no party info, remove it from the client
				ServerNetworkSender.removePartyInfo(serverPlayer);
			}

		}
	}
}