package dev.mariany.keep_inventory_nerf.mixin;

import dev.mariany.keep_inventory_nerf.KeepInventoryNerfHelper;
import dev.mariany.keep_inventory_nerf.gamerule.KeepInventoryNerfGamerules;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(method = "dropInventory", at = @At(value = "TAIL"))
    protected void dropInventory(ServerWorld world, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        GameRules gameRules = world.getGameRules();
        boolean keepInventory = gameRules.getBoolean(GameRules.KEEP_INVENTORY);
        int minItemsToDrop = gameRules.getInt(KeepInventoryNerfGamerules.KEEP_INVENTORY_MIN_ITEMS_TO_DROP);
        int maxItemsToDrop = gameRules.getInt(KeepInventoryNerfGamerules.KEEP_INVENTORY_MAX_ITEMS_TO_DROP);
        int itemsToDrop = world.getRandom().nextBetween(minItemsToDrop, maxItemsToDrop);

        if (keepInventory) {
            KeepInventoryNerfHelper.vanishCursedItems(player);
            List<ItemStack> droppedItems = KeepInventoryNerfHelper.dropRandomItems(player, itemsToDrop);

            if (!droppedItems.isEmpty()) {
                Text itemsText = KeepInventoryNerfHelper.formatItemsText(droppedItems);

                Text message = Text.translatable("death.keep_inventory_nerf.dropped_items",
                                Text.of(KeepInventoryNerfHelper.formatCoords(player)).copy().withColor(Colors.GRAY))
                        .withColor(Colors.LIGHT_RED)
                        .styled(style -> style.withHoverEvent(new HoverEvent.ShowText(itemsText)));

                player.sendMessage(message, false);
            }
        }
    }
}
