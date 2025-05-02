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
                    "onmymark.keybind.party",
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_O,
                    "category.onmymark.keybinds"
            )
    );

    private static final KeyBinding MARK_BINDING = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                    "onmymark.keybind.mark",
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_R,
                    "category.onmymark.keybinds"
            )
    );

    private static final KeyBinding PLAYER_REPORT_BINDING = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                    "onmymark.keybind.playerReport",
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_U,
                    "category.onmymark.keybinds"
            )
    );

    private final ClientPartyManager clientPartyManager;
    private final ClientMarkerManager clientMarkerManager;
    private final OnMyMarkRenderer renderer;

    public InputListener(ClientPartyManager clientPartyManager, ClientMarkerManager clientMarkerManager, OnMyMarkRenderer renderer) {
        this.clientPartyManager = clientPartyManager;
        this.clientMarkerManager = clientMarkerManager;
        this.renderer = renderer;
    }

    public void onInput(MinecraftClient client) {
        if (client.player != null) {
            while (MARK_BINDING.wasPressed()) {
                // TODO: Add cooldown to marking
                InputHandler inputHandler = new OnPlayerMarkInputHandler(clientPartyManager, clientMarkerManager, renderer);
                inputHandler.execute(client);
            }

            while (PLAYER_REPORT_BINDING.wasPressed()) {
                InputHandler inputHandler = new PlayerReportInputHandler(clientPartyManager, clientMarkerManager, renderer);
                inputHandler.execute(client);
            }

            while (GUI_BINDING.wasPressed()) {
                client.setScreen(new PartyScreen(clientPartyManager.party()));
            }
        }
    }
}
