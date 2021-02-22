package ewewukek.tpc.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;

@Mixin(InGameHud.class)
public interface InGameHudAccessor {
    @Accessor("client") MinecraftClient getClient();
    @Accessor("scaledWidth") int getScaledWidth();
    @Accessor("scaledHeight") int getScaledHeight();
}
