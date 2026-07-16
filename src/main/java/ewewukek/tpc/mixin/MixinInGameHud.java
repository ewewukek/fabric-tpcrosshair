package ewewukek.tpc.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import ewewukek.tpc.Config;
import com.mojang.blaze3d.pipeline.RenderPipeline;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@Mixin(Gui.class)
public class MixinInGameHud {
    private static final Identifier CROSSHAIR_BOW_DRAWN = Identifier.fromNamespaceAndPath("tpcrosshair", "hud/crosshair_bow_drawn");
    private static final Identifier CROSSHAIR_SPRITE = Identifier.withDefaultNamespace("hud/crosshair");

    @Redirect(
        method = "extractCrosshair(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/CameraType;isFirstPerson()Z")
    )
    private boolean doRenderCrosshair(CameraType cameraType) {
        if (cameraType.isFirstPerson()) {
            return true;
        } else {
            if (cameraType == CameraType.THIRD_PERSON_FRONT) {
                return Config.enableIn3rdPersonFront;
            } else {
                return Config.enableIn3rdPerson;
            }
        }
    }

    @Redirect(
        method = "extractCrosshair(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V")
    )
    private void drawGuiTexture(GuiGraphicsExtractor context, RenderPipeline pipeline, Identifier texture, int x, int y, int w, int h) {
        Gui gui = (Gui)(Object)this;

        context.blitSprite(pipeline, texture, x, y, w, h);

        if (texture.equals(CROSSHAIR_SPRITE)) {
            boolean weaponReady = false;

            Minecraft client = gui.minecraft;
            LocalPlayer player = client.player;
            ItemStack itemStack = player.getActiveItem();
            if (player.isUsingItem()) {
                if (Config.enableBowDrawIndicator && itemStack.getItem() == Items.BOW) {
                    int ticksInUse = Items.BOW.getUseDuration(itemStack, player) - player.getUseItemRemainingTicks();
                    if (BowItem.getPowerForTime(ticksInUse) == 1.0F) {
                        weaponReady = true;
                    }
                }
                if (Config.enableTridentChargeIndicator && itemStack.getItem() == Items.TRIDENT) {
                    int ticksInUse = Items.TRIDENT.getUseDuration(itemStack, player) - player.getUseItemRemainingTicks();
                    if (ticksInUse >= 10) {
                        weaponReady = true;
                    }
                }
            }

            if (weaponReady) { // small tick under main crosshair
                int k = context.guiWidth() / 2 - 3;
                int j = context.guiHeight() / 2 + 5;
                context.blitSprite(RenderPipelines.CROSSHAIR, CROSSHAIR_BOW_DRAWN, k, j, 5, 5);
            }
        }
    }
}
