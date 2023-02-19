package ewewukek.tpc;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class TPCrosshairClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Config.path = FabricLoader.getInstance().getConfigDir().resolve("tpcrosshair.txt");
        Config.load();
    }
}
