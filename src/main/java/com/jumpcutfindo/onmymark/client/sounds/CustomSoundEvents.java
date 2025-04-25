package com.jumpcutfindo.onmymark.client.sounds;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class CustomSoundEvents {
    private CustomSoundEvents() {}

    public static final SoundEvent PLACE_MARKER = registerSound("place_marker");
    public static final SoundEvent OTHER_MARKER = registerSound("other_marker");
    public static final SoundEvent PLAYER_REPORT = registerSound("player_report");

    public static void initialize() {
    }

    private static SoundEvent registerSound(String id) {
        Identifier identifier = Identifier.of(OnMyMarkMod.MOD_ID, id);
        return Registry.register(Registries.SOUND_EVENT, identifier, SoundEvent.of(identifier));
    }
}
