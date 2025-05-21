package com.jumpcutfindo.onmymark;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OnMyMarkMod {
    public static final String MOD_ID = "onmymark";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static com.jumpcutfindo.onmymark.OnMyMarkConfig CONFIG = com.jumpcutfindo.onmymark.OnMyMarkConfig.createAndLoad();
}
