package dev.mariany.gamerule;

import dev.mariany.KeepInventoryNerf;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public class KeepInventoryNerfGamerules {
    public static final GameRules.Key<GameRules.IntRule> KEEP_INVENTORY_ITEMS_TO_DROP = GameRuleRegistry.register(
            "keepInventoryItemsToDrop", GameRules.Category.PLAYER, GameRuleFactory.createIntRule(1, 1));

    public static void bootstrap() {
        KeepInventoryNerf.LOGGER.info("Registering gamerules for " + KeepInventoryNerf.MOD_ID);
    }
}
