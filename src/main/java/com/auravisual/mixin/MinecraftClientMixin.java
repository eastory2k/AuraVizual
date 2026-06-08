package com.auravisual.mixin;

import com.auravisual.AuraVisualClient;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(at = @At("END"), method = "tick")
    private void onTick(CallbackInfo info) {
        // Вызываем нашу логику чита каждый игровой тик
        AuraVisualClient.onClientTick(MinecraftClient.getInstance());
    }
}
