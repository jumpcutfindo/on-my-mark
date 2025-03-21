package com.jumpcutfindo.onmymark.client;


import com.jumpcutfindo.onmymark.input.InputHandler;
import com.jumpcutfindo.onmymark.input.OnPlayerMarkInputHandler;
import com.jumpcutfindo.onmymark.network.ClientNetworkSender;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class InputListener implements ClientModInitializer {
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

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                while (DEBUG_BINDING.wasPressed()) {
                    ClientNetworkSender.createParty("New party name");
                    ClientNetworkSender.leaveParty();
                }

                while (MARK_BINDING.wasPressed()) {
                    // TODO: Add cooldown to marking
                    InputHandler inputHandler = new OnPlayerMarkInputHandler();
                    inputHandler.execute(client);
                }
            }
        });
    }
}
