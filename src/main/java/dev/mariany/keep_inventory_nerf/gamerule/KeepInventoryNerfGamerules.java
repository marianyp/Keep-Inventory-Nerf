package dev.mariany.keep_inventory_nerf.gamerule;

import dev.mariany.keep_inventory_nerf.KeepInventoryNerf;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public class KeepInventoryNerfGamerules {
    public static final GameRules.Key<GameRules.IntRule> KEEP_INVENTORY_MIN_ITEMS_TO_DROP = GameRuleRegistry.register(
            "keepInventoryMinItemsToDrop", GameRules.Category.PLAYER, GameRuleFactory.createIntRule(1, 0));

    public static final GameRules.Key<GameRules.IntRule> KEEP_INVENTORY_MAX_ITEMS_TO_DROP = GameRuleRegistry.register(
            "keepInventoryMaxItemsToDrop", GameRules.Category.PLAYER, GameRuleFactory.createIntRule(3, 1));

    public static void bootstrap() {
        KeepInventoryNerf.LOGGER.info("Registering gamerules for " + KeepInventoryNerf.MOD_ID);
    }
}
