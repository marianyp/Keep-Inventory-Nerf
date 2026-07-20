package dev.mariany.keepinventorynerf.mixin.accessor;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Player.class)
public interface PlayerEntityAccessor {
    @Invoker("destroyVanishingCursedItems")
    void keepinventorynerf$destroyVanishingCursedItems();
}
