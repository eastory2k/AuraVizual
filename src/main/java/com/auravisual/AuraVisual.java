package com.auravisual;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class AuraVisual implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Логика вынесена в Mixin для более стабильного FPS, но точку входа оставляем пустой для работы Fabric
    }
}
