package ewewukek.tpc.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import ewewukek.tpc.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

@Mixin(InGameHud.class)
public class MixinInGameHud {
    @Redirect(
        method = "renderCrosshair(Lnet/minecraft/client/util/math/MatrixStack;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/Perspective;isFirstPerson()Z")
    )
    private boolean doRenderCrosshair(Perspective perspective) {
        if (perspective.isFirstPerson()) {
            return true;
        } else {
            if (perspective.isFrontView()) {
                return Config.enableIn3rdPersonFront;
            } else {
                return Config.enableIn3rdPerson;
            }
        }
    }

    @Redirect(
        method = "renderCrosshair(Lnet/minecraft/client/util/math/MatrixStack;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V")
    )
    private void drawTexture(InGameHud hud, MatrixStack matrices, int dst_x, int dst_y, int src_x, int src_y, int w, int h) {
        if (src_y == 94) {
            dst_x -= 1; // fix indicator position
        }
        hud.drawTexture(matrices, dst_x, dst_y, src_x, src_y, w, h);

        if (src_x == 0 && src_y == 0) { // main crosshair
            boolean bowReady = false;

            MinecraftClient client = hud.client;
            ClientPlayerEntity player = client.player;
            ItemStack itemStack = player.getActiveItem();
            if (player.isUsingItem() && itemStack.getItem() == Items.BOW) {
                int ticksInUse = Items.BOW.getMaxUseTime(itemStack) - player.getItemUseTimeLeft();
                if (BowItem.getPullProgress(ticksInUse) == 1.0F ) {
                    bowReady = true;
                }
            }

            if (Config.enableBowDrawIndicator && bowReady) { // small tick under main crosshair
                int k = hud.scaledWidth / 2 - 2;
                int j = hud.scaledHeight / 2 + 6;
                hud.drawTexture(matrices, k, j, 75, 98, 3, 3);
            }
        }
    }
}
