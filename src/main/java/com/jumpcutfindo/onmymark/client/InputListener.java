package com.jumpcutfindo.onmymark.client;


import com.jumpcutfindo.onmymark.graphics.OnMyMarkRenderer;
import com.jumpcutfindo.onmymark.graphics.screen.party.PartyScreen;
import com.jumpcutfindo.onmymark.input.InputHandler;
import com.jumpcutfindo.onmymark.input.OnPlayerMarkInputHandler;
import com.jumpcutfindo.onmymark.network.client.ClientNetworkSender;
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
                    "category.onmymark.keybinds"
            )
    );

    private static final KeyBinding MARK_BINDING = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                    "key.onmymark.mark",
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_R,
                    "category.onmymark.keybinds"
            )
    );

    private static final KeyBinding DEBUG_BINDING = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                    "key.onmymark.debug",
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_O,
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
            while (DEBUG_BINDING.wasPressed()) {
                ClientNetworkSender.createParty("New party name");
                ClientNetworkSender.leaveParty();
            }

            while (MARK_BINDING.wasPressed()) {
                // TODO: Add cooldown to marking
                InputHandler inputHandler = new OnPlayerMarkInputHandler(clientPartyManager, clientMarkerManager, renderer);
                inputHandler.execute(client);
            }

            while (GUI_BINDING.wasPressed()) {
                client.setScreen(new PartyScreen(clientPartyManager.party()));
            }
        }
    }
}
