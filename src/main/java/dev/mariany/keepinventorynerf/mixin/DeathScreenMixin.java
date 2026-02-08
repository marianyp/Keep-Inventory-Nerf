package dev.mariany.keepinventorynerf.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.keepinventorynerf.client.KeepInventoryNerfClient;
import net.minecraft.client.font.TextRenderer;
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
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawCenteredTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)V"
            )
    )
    public void wrapRender(
            DrawContext context,
            TextRenderer textRenderer,
            Text text,
            int centerX,
            int y,
            int color,
            Operation<Void> original
    ) {
        if (!KeepInventoryNerfClient.DROPPED_ITEMS_RENDERER.disableScoreRender(y)) {
            original.call(context, textRenderer, text, centerX, y, color);
        }
    }
}
