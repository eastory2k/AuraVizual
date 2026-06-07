package com.auravisual;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

public class ItemSwapLogic {

    // Метод, который вызывается при нажатии кнопки 'R'
    public static void executeSwap() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        // Если функция выключена в ClickGUI, ничего не делаем
        if (!FeatureManager.itemSwapVisual) return;

        PlayerInventory inventory = client.player.getInventory();
        
        // Пример простой логики: ищем Тотем бессмертия на панели быстрого доступа (слоты 0-8)
        for (int i = 0; i < 9; i++) {
            ItemStack stack = inventory.getStack(i);
            
            // Если нашли тотем и он НЕ в руке прямо сейчас
            if (stack.isOf(Items.TOTEM_OF_UNDYING) && inventory.selectedSlot != i) {
                inventory.selectedSlot = i; // Быстро переключаем руку на этот слот
                client.player.sendMessage(Text.literal("§d[AuraVisual] §fПредмет успешно сменен!"), true);
                break; // Точка с запятой на месте, выходим из цикла!
            }
        }
    }
}
