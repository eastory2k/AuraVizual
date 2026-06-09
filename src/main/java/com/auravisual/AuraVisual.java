package com.auravisual;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class AuraVisual implements ClientModInitializer {
    public static KeyBinding openGuiKey;
    private static boolean isKeyPressed = false;

    @Override
    public void onInitializeClient() {
        // Стандартная регистрация для отображения в настройках управления управления игры
        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.auravisual.open", 
                InputUtil.Type.KEYSYM, 
                GLFW.GLFW_KEY_RIGHT_SHIFT, 
                "category.auravisual"
        ));

        // Жесткий перехват тика клиента
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;

            // Метод 1: Проверка через стандартный Fabric KeyBinding
            if (openGuiKey.wasPressed()) {
                openMenu(client);
                return;
            }

            // Метод 2 (Резервный): Прямой опрос LWJGL окна (если первый метод проигнорирован игрой)
            long windowHandle = client.getWindow().getHandle();
            boolean isDown = GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;

            if (isDown) {
                if (!isKeyPressed) { // Защита от дублирования кликов (выполняется 1 раз при нажатии)
                    isKeyPressed = true;
                    openMenu(client);
                }
            } else {
                isKeyPressed = false; // Сбрасываем, когда кнопку отпустили
            }
        });
    }

    private static void openMenu(MinecraftClient client) {
        if (client.currentScreen == null) {
            // Запуск GUI экрана
            client.setScreen(new ClickGUI());
        }
    }
}
