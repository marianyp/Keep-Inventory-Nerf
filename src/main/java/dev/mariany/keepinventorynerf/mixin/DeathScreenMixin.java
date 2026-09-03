package dev.mariany.keepinventorynerf.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.keepinventorynerf.client.KeepInventoryNerfClient;
import net.minecraft.client.font.Alignment;
import net.minecraft.client.font.DrawnTextConsumer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DeathScreen.class)
public class DeathScreenMixin {
    @Inject(method = "render", at = @At(value = "TAIL"))
    public void injectRender(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        KeepInventoryNerfClient.DROPPED_ITEMS_RENDERER.render(context, mouseX, mouseY);
    }

    @WrapOperation(
            method = "drawTitles",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/font/DrawnTextConsumer;text(Lnet/minecraft/client/font/Alignment;IILnet/minecraft/text/Text;)V"
            )
    )
    public void wrapDrawTitles(
            DrawnTextConsumer textConsumer,
            Alignment alignment,
            int anchorX,
            int y,
            Text text,
            Operation<Void> original
    ) {
        if (KeepInventoryNerfClient.DROPPED_ITEMS_RENDERER.disableScoreRender(y)) {
            return;
        }

        original.call(textConsumer, alignment, anchorX, y, text);
    }
}
