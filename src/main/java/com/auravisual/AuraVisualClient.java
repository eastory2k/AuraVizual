package com.auravisual;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import org.lwjgl.glfw.GLFW;

public class AuraVisualClient implements ClientModInitializer {
    public static KeyBinding openGuiKey;
    private static double wingTimer = 0;

    @Override
    public void onInitializeClient() {
        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.auravisual.opengui", 
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT, 
                "category.auravisual.general"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && openGuiKey.wasPressed()) {
                client.setScreen(new ClickGUI());
            }

            if (client.world != null && client.player != null && !client.options.hudHidden) {
                // ... (тут код крыльев, нимба и т.д. — он остается прежним)
            }
        });

        HudRenderCallback.EVENT.register((drawContext, renderTickCounter) -> {
            net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
            if (client.player == null || client.options.hudHidden) return;

            // ... (тут код отрисовки HUD — он остается прежним)

            // --- ЛОГИКА GLOW ESP (ЛУТ) ---
            if (client.world != null) {
                for (net.minecraft.entity.Entity entity : client.world.getEntities()) {
                    if (entity instanceof net.minecraft.entity.ItemEntity) {
                        entity.setGlowing(FeatureManager.glowESP);
                    }
                }
            }

            // --- ЛОГИКА SOULSIGHT (ИГРОКИ) ---
            if (client.world != null) {
                for (net.minecraft.entity.Entity entity : client.world.getEntities()) {
                    if (entity instanceof PlayerEntity && entity != client.player) {
                        // Включаем свечение только если модуль активирован
                        entity.setGlowing(FeatureManager.soulSight);
                    }
                }
            }
        });
    }
}
