package dev.mariany.keepinventorynerf.gamerule;

import dev.mariany.keepinventorynerf.KeepInventoryNerf;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.minecraft.world.rule.GameRule;
import net.minecraft.world.rule.GameRuleCategory;

public final class KINGamerules {
    private KINGamerules() {
    }

    public static final GameRule<Integer> KEEP_INVENTORY_MIN_ITEMS_TO_DROP = GameRuleBuilder.forInteger(1)
            .minValue(0)
            .category(GameRuleCategory.DROPS)
            .buildAndRegister(KeepInventoryNerf.id("keep_inventory_min_items_to_drop"));

    public static final GameRule<Integer> KEEP_INVENTORY_MAX_ITEMS_TO_DROP = GameRuleBuilder.forInteger(3)
            .minValue(1)
            .category(GameRuleCategory.DROPS)
            .buildAndRegister(KeepInventoryNerf.id("keep_inventory_max_items_to_drop"));

    public static final GameRule<Integer> EXPERIENCE_LOSS_PERCENTAGE = GameRuleBuilder.forInteger(33)
            .range(0, 100)
            .category(GameRuleCategory.DROPS)
            .buildAndRegister(KeepInventoryNerf.id("experience_loss_percentage"));

    public static final GameRule<Boolean> DEATH_DROPS_DESPAWN = GameRuleBuilder.forBoolean(false)
            .category(GameRuleCategory.DROPS)
            .buildAndRegister(KeepInventoryNerf.id("death_drops_despawn"));

    public static void bootstrap() {
        KeepInventoryNerf.bootstrapLog("Game Rules");
    }
}
