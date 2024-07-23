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
import net.minecraft.util.Identifier;

@Mixin(InGameHud.class)
public class MixinInGameHud {
    private static final Identifier CROSSHAIR_BOW_DRAWN = Identifier.of("tpcrosshair", "hud/crosshair_bow_drawn");

    @Redirect(
        method = "renderCrosshair(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V",
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
        method = "renderCrosshair(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V")
    )
    private void drawGuiTexture(DrawContext context, Identifier texture, int x, int y, int w, int h) {
        InGameHud hud = (InGameHud)(Object)this;

        context.drawGuiTexture(texture, x, y, w, h);

        if (texture == InGameHud.CROSSHAIR_TEXTURE) {
            boolean weaponReady = false;

            MinecraftClient client = hud.client;
            ClientPlayerEntity player = client.player;
            ItemStack itemStack = player.getActiveItem();
            if (player.isUsingItem()) {
                if (Config.enableBowDrawIndicator && itemStack.getItem() == Items.BOW) {
                    int ticksInUse = Items.BOW.getMaxUseTime(itemStack, player) - player.getItemUseTimeLeft();
                    if (BowItem.getPullProgress(ticksInUse) == 1.0F) {
                        weaponReady = true;
                    }
                }
                if (Config.enableTridentChargeIndicator && itemStack.getItem() == Items.TRIDENT) {
                    int ticksInUse = Items.TRIDENT.getMaxUseTime(itemStack, player) - player.getItemUseTimeLeft();
                    if (ticksInUse >= 10) {
                        weaponReady = true;
                    }
                }
            }

            if (weaponReady) { // small tick under main crosshair
                int k = context.getScaledWindowWidth() / 2 - 3;
                int j = context.getScaledWindowHeight() / 2 + 5;
                context.drawGuiTexture(CROSSHAIR_BOW_DRAWN, k, j, 5, 5);
            }
        }
    }
}
