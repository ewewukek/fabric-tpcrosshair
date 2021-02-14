package ewewukek.tpc.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.options.Perspective;

@Mixin(InGameHud.class)
public class MixinInGameHud {
    @Redirect(
        method = "renderCrosshair(Lnet/minecraft/client/util/math/MatrixStack;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/options/Perspective;isFirstPerson()Z")
    )
    private boolean doRenderCrosshair(Perspective perspective) {
        return !perspective.isFrontView();
    }
}
