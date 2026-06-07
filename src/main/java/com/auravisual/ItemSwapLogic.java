package com.auravisual;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;

public class ItemSwapLogic {

    // Метод, который ищет сферу и меняет её с предметом в руке
    public static void trySwap() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.interactionManager == null) return;

        PlayerInventory inventory = client.player.getInventory();
        int currentHandSlot = inventory.selectedSlot; // Слот, который сейчас в руке (0-8)

        // 1. Ищем сферу или талисман в инвентаре (проверяем все 36 слотов)
        int targetSlot = -1;
        for (int i = 0; i < 36; i++) {
            // Пропускаем слот, который уже зажат в руке
            if (i == currentHandSlot) continue;

            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty()) {
                // Получаем название предмета
                String itemName = stack.getName().getString().toLowerCase();

                // ПРОВЕРКА: если в названии есть "сфера" или "талисман"
                if (itemName.contains("сфера") || itemName.contains("талисман") || itemName.contains("talisman") || itemName.contains("sphere")) {
                    targetSlot = i;
                    break
