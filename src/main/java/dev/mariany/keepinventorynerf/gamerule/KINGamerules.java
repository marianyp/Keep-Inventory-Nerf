package dev.mariany.keepinventorynerf.gamerule;

import dev.mariany.keepinventorynerf.KeepInventoryNerf;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public final class KINGamerules {
    private KINGamerules() {
    }

    public static final GameRules.Key<GameRules.IntRule> KEEP_INVENTORY_MIN_ITEMS_TO_DROP = GameRuleRegistry.register(
            "keepInventoryMinItemsToDrop",
            GameRules.Category.DROPS,
            GameRuleFactory.createIntRule(1, 0)
    );

    public static final GameRules.Key<GameRules.IntRule> KEEP_INVENTORY_MAX_ITEMS_TO_DROP = GameRuleRegistry.register(
            "keepInventoryMaxItemsToDrop",
            GameRules.Category.DROPS,
            GameRuleFactory.createIntRule(3, 1)
    );

    public static final GameRules.Key<GameRules.IntRule> EXPERIENCE_LOSS_PERCENTAGE = GameRuleRegistry.register(
            "experienceLossPercentage",
            GameRules.Category.DROPS,
            GameRuleFactory.createIntRule(33, 0, 100)
    );

    public static final GameRules.Key<GameRules.BooleanRule> DEATH_DROPS_DESPAWN = GameRuleRegistry.register(
            "deathDropsDespawn",
            GameRules.Category.DROPS,
            GameRuleFactory.createBooleanRule(false)
    );

    public static void bootstrap() {
        KeepInventoryNerf.bootstrapLog("Gamerules");
    }
}
