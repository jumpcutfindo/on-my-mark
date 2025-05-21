package com.jumpcutfindo.onmymark.client.input;


import com.jumpcutfindo.onmymark.client.graphics.OnMyMarkRenderer;
import com.jumpcutfindo.onmymark.client.graphics.screen.party.PartyScreen;
import com.jumpcutfindo.onmymark.client.marker.ClientMarkerManager;
import com.jumpcutfindo.onmymark.client.party.ClientPartyManager;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class InputListener {
    public static final KeyBinding GUI_BINDING = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                    "key.onmymark.party",
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_O,
                    "key.categories.onmymark.onmymark"
            )
    );

    private static final KeyBinding MARK_BINDING = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                    "key.onmymark.mark",
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_R,
                    "key.categories.onmymark.onmymark"
            )
    );

    private static final KeyBinding PLAYER_REPORT_BINDING = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                    "key.onmymark.playerReport",
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_U,
                    "key.categories.onmymark.onmymark"
            )
    );

    private final ClientPartyManager clientPartyManager;
    private final ClientMarkerManager clientMarkerManager;
    private final OnMyMarkRenderer renderer;

    private final InputHandler placeMarkerHandler, removeMarkerHandler, playerReportHandler;

    public InputListener(ClientPartyManager clientPartyManager, ClientMarkerManager clientMarkerManager, OnMyMarkRenderer renderer) {
        this.clientPartyManager = clientPartyManager;
        this.clientMarkerManager = clientMarkerManager;
        this.renderer = renderer;

        this.placeMarkerHandler = new PlaceMarkerInputHandler(MinecraftClient.getInstance(), clientPartyManager);
        this.removeMarkerHandler = new RemoveMarkerInputHandler(MinecraftClient.getInstance(), clientMarkerManager, clientPartyManager, renderer);
        this.playerReportHandler = new PlayerReportInputHandler(MinecraftClient.getInstance(), clientPartyManager);
    }

    public void onInput(MinecraftClient client) {
        if (client.player != null) {
            while (MARK_BINDING.wasPressed()) {
                if (removeMarkerHandler.canExecute() && removeMarkerHandler.execute()) {
                    return;
                }

                if (placeMarkerHandler.canExecute()) {
                    placeMarkerHandler.execute();
                }
            }

            while (PLAYER_REPORT_BINDING.wasPressed()) {
                if (playerReportHandler.canExecute()) {
                    playerReportHandler.execute();
                }
            }

            while (GUI_BINDING.wasPressed()) {
                client.setScreen(new PartyScreen(clientPartyManager.party()));
            }
        }
    }
}
