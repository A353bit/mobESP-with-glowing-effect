package com.a353bit.mobesp.client;

import com.a353bit.mobesp.gui.MobEspScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class MobEspClient implements ClientModInitializer {

    private static final KeyBinding.Category CATEGORY =
            KeyBinding.Category.create(Identifier.of("mobesp", "mobesp"));

    private static KeyBinding openMenuKey;

    public static KeyBinding getOpenMenuKey() {
        return openMenuKey;
    }

    @Override
    public void onInitializeClient() {
        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mobesp.open_menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new MobEspScreen());
                }
            }
        });
    }
}
