package dev.mariany.keepinventorynerf.mixin;

import net.minecraft.world.rule.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(GameRules.class)
public class GameRulesMixin {
    @ModifyArgs(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/rule/GameRules;registerBooleanRule(Ljava/lang/String;Lnet/minecraft/world/rule/GameRuleCategory;Z)Lnet/minecraft/world/rule/GameRule;"
            )
    )
    private static void keepinventorynerf$defaultKeepInventory(Args args) {
        if ("keep_inventory".equals(args.get(0))) {
            args.set(2, true);
        }
    }
}
