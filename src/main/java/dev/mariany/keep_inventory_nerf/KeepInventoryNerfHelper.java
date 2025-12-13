package dev.mariany.keep_inventory_nerf;

import dev.mariany.keep_inventory_nerf.tag.KeepInventoryNerfTags;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;

public class KeepInventoryNerfHelper {
    public static List<ItemStack> dropRandomItems(PlayerEntity player, int n) {
        Random random = player.getRandom();
        PlayerInventory inventory = player.getInventory();
        List<ItemStack> droppableItems = new ArrayList<>();
        List<ItemStack> itemsToDrop = new ArrayList<>();
        List<ItemStack> itemsToDropCopy = new ArrayList<>();

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack itemStack = inventory.getStack(i);
            if (!itemStack.isEmpty() && !itemStack.isIn(KeepInventoryNerfTags.Items.IGNORE_DROP)) {
                droppableItems.add(itemStack);
            }
        }

        for (int i = 0; i < n; i++) {
            if (!droppableItems.isEmpty()) {
                ItemStack removed = droppableItems.remove(random.nextInt(droppableItems.size()));
                itemsToDrop.add(removed);
                itemsToDropCopy.add(removed.copy());
            }
        }

        for (ItemStack itemStack : itemsToDrop) {
            player.dropItem(itemStack.copy(), true, false);
            inventory.remove(
                    stack -> itemStack == stack, -1,
                    player.playerScreenHandler.getCraftingInput()
            );
        }

        return itemsToDropCopy;
    }

    public static void vanishCursedItems(PlayerEntity player) {
        PlayerInventory inventory = player.getInventory();

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack itemStack = inventory.getStack(i);
            if (!itemStack.isEmpty() && EnchantmentHelper.hasAnyEnchantmentsWith(
                    itemStack,
                    EnchantmentEffectComponentTypes.PREVENT_EQUIPMENT_DROP
            )) {
                inventory.removeStack(i);
            }
        }
    }

    public static int convertLevelsToExperience(int level) {
        if (level <= 16) {
            return (int) (Math.pow(level, 2) + level * 6);
        }

        if (level <= 31) {
            return (int) (Math.pow(level, 2) * 2.5 - 40.5 * level + 360);
        }

        return (int) (Math.pow(level, 2) * 4.5 - 162.5 * level + 2220);
    }

    public static Text formatItemsText(List<ItemStack> itemStacks) {
        List<MutableText> itemsTextList = itemStacks.stream()
                .map(stack -> stack.getName().copy().append(" (" + stack.getCount() + ")").withColor(Colors.GRAY))
                .toList();

        MutableText itemsText = Text.empty();

        for (int i = 0; i < itemsTextList.size(); i++) {
            itemsText.append(itemsTextList.get(i));

            if (i < itemsTextList.size() - 1) {
                itemsText.append(Text.literal(", "));
            }
        }

        return itemsText;
    }

    public static String formatCoords(PlayerEntity player) {
        int x = (int) Math.floor(player.getX());
        int y = (int) Math.floor(player.getY());
        int z = (int) Math.floor(player.getZ());

        return "X: " + x + " Y: " + y + " Z: " + z;
    }
}
