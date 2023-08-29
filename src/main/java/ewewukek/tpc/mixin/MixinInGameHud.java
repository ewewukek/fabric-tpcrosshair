package ewewukek.tpc.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import ewewukek.tpc.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.TridentItem;
import net.minecraft.util.Identifier;

@Mixin(InGameHud.class)
public class MixinInGameHud {
    @Redirect(
        method = "renderCrosshair(Lnet/minecraft/client/gui/DrawContext;)V",
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
        method = "renderCrosshair(Lnet/minecraft/client/gui/DrawContext;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V")
    )
    private void drawTexture(DrawContext context, Identifier textureId, int dst_x, int dst_y, int src_x, int src_y, int w, int h) {
        InGameHud hud = (InGameHud)(Object)this;

        if (src_y == 94) {
            dst_x -= 1; // fix indicator position
        }
        context.drawTexture(textureId, dst_x, dst_y, src_x, src_y, w, h);

        if (src_x == 0 && src_y == 0) { // main crosshair
            boolean weaponReady = false;

            MinecraftClient client = hud.client;
            ClientPlayerEntity player = client.player;
            ItemStack itemStack = player.getActiveItem();
            if (player.isUsingItem()) {
                if (Config.enableBowDrawIndicator && itemStack.getItem() == Items.BOW) {
                    int ticksInUse = Items.BOW.getMaxUseTime(itemStack) - player.getItemUseTimeLeft();
                    if (BowItem.getPullProgress(ticksInUse) == 1.0F ) {
                        weaponReady = true;
                    }
                }
                if (Config.enableTridentChargeIndicator && itemStack.getItem() == Items.TRIDENT) {
                    int ticksInUse = Items.TRIDENT.getMaxUseTime(itemStack) - player.getItemUseTimeLeft();
                    if (ticksInUse >= TridentItem.field_30926) {
                        weaponReady = true;
                    }
                }
            }

            if (weaponReady) { // small tick under main crosshair
                int k = hud.scaledWidth / 2 - 2;
                int j = hud.scaledHeight / 2 + 6;
                context.drawTexture(textureId, k, j, 75, 98, 3, 3);
            }
        }
    }
}
