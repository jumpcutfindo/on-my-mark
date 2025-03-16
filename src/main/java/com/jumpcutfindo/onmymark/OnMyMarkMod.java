package com.jumpcutfindo.onmymark;

import com.jumpcutfindo.onmymark.party.PartyManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OnMyMarkMod implements ModInitializer {
	public static final String MOD_ID = "on-my-mark";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static PartyManager PARTY_MANAGER = null;

	@Override
	public void onInitialize() {
		ServerLifecycleEvents
				.SERVER_STARTED
				.register(this::onServerStarted);

		// Debugging stuff, remove after development is completed
	}

	public void onServerStarted(MinecraftServer server) {
		// Initialize the party system
		PARTY_MANAGER = new PartyManager();
		LOGGER.info("Party manager initialized!");
	}
}