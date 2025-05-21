package com.jumpcutfindo.onmymark;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OnMyMarkMod implements ModInitializer {
    public static final String MOD_ID = "onmymark";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static com.jumpcutfindo.onmymark.OnMyMarkConfig CONFIG = null;

    @Override
    public void onInitialize() {
        CONFIG = com.jumpcutfindo.onmymark.OnMyMarkConfig.createAndLoad();
    }
}
