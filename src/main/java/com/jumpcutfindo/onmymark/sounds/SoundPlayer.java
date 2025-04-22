package com.jumpcutfindo.onmymark.sounds;

import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundEvents;

public class SoundPlayer {
    public static void playClickSound(SoundManager soundManager) {
        soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    public static void playPlaceMarkerSound(SoundManager soundManager) {
        soundManager.play(PositionedSoundInstance.master(CustomSoundEvents.PLACE_MARKER, 1.0F, 1.1F));
    }

    public static void playOtherMarkerSound(SoundManager soundManager) {
        soundManager.play(PositionedSoundInstance.master(CustomSoundEvents.OTHER_MARKER, 1.1F));
    }

    public static void playPlayerReportSound(SoundManager soundManager) {
        soundManager.play(PositionedSoundInstance.master(CustomSoundEvents.PLAYER_REPORT, 1.1F));
    }
}
