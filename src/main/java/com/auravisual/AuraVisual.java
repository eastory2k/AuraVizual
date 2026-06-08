package com.auravisual;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class AuraVisual implements ClientModInitializer {
    public static KeyBinding openGuiKey;

    @Override
    public void onInitializeClient() {
        // Регистрируем кнопку Правый Shift для открытия меню ClickGUI
        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.auravisual.open", 
                InputUtil.Type.KEYSYM, 
                GLFW.GLFW_KEY_RIGHT_SHIFT, 
                "category.auravisual"
        ));

        // Оставляем общий клиентский тик
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && client.world != null) {
                // Проверяем нажатие кнопки
                if (openGuiKey.wasPressed() && client.currentScreen == null) {
                    client.setScreen(new ClickGUI());
                }
            }
        });
    }
}
