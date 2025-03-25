package com.jumpcutfindo.onmymark.client;


import com.jumpcutfindo.onmymark.graphics.screen.PartyScreen;
import com.jumpcutfindo.onmymark.input.InputHandler;
import com.jumpcutfindo.onmymark.input.OnPlayerMarkInputHandler;
import com.jumpcutfindo.onmymark.network.ClientNetworkSender;
import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.party.PartyMember;
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

    private final ClientMarkerManager clientMarkerManager;

    public InputListener(ClientMarkerManager clientMarkerManager) {
        this.clientMarkerManager = clientMarkerManager;
    }

    public void onInput(MinecraftClient client) {
        if (client.player != null) {
            while (DEBUG_BINDING.wasPressed()) {
                ClientNetworkSender.createParty("New party name");
                ClientNetworkSender.leaveParty();
            }

            while (MARK_BINDING.wasPressed()) {
                // TODO: Add cooldown to marking
                InputHandler inputHandler = new OnPlayerMarkInputHandler(clientMarkerManager);
                inputHandler.execute(client);
            }

            while (GUI_BINDING.wasPressed()) {
                // TODO: Remove this sample set of players
                PartyMember exampleMember = new PartyMember(MinecraftClient.getInstance().player);
                Party party = new Party("Some party name", exampleMember);
                for (int i = 0; i < 20; i++) {
                    PartyMember newMember = new PartyMember(MinecraftClient.getInstance().player);
                    party.addPartyMember(newMember);
                }

                client.setScreen(new PartyScreen(party));
            }
        }
    }
}
