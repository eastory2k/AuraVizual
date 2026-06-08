package com.auravisual;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class AuraVisual implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Подключаем игровой цикл строго на клиенте, чтобы не крашить лаунчер
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            AuraVisualClient.onClientTick(client);
        });
    }
}
