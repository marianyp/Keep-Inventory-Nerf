package dev.mariany.keepinventorynerf.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.keepinventorynerf.client.KeepInventoryNerfClient;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DeathScreen.class)
public class DeathScreenMixin {
    @Inject(method = "extractRenderState", at = @At(value = "TAIL"))
    public void injectRender(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        KeepInventoryNerfClient.DROPPED_ITEMS_RENDERER.extractRenderState(graphics, mouseX, mouseY);
    }

    @WrapOperation(
            method = "visitText",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/ActiveTextCollector;accept(Lnet/minecraft/client/gui/TextAlignment;IILnet/minecraft/network/chat/Component;)V"
            )
    )
    public void wrapVisitText(
            ActiveTextCollector textCollector,
            TextAlignment textAlignment,
            int anchorX,
            int y,
            Component text,
            Operation<Void> original
    ) {
        if (KeepInventoryNerfClient.DROPPED_ITEMS_RENDERER.disableScoreRender(y)) {
            return;
        }

        original.call(textCollector, textAlignment, anchorX, y, text);
    }
}
