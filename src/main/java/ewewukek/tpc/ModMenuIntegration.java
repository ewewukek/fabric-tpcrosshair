package ewewukek.tpc;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("tpcrosshair.options.title"));
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            ConfigCategory category = builder.getOrCreateCategory(Text.literal("category"));

            category.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("tpcrosshair.options.enable_in_3rd_person"), Config.enableIn3rdPerson)
                .setSaveConsumer(value -> Config.enableIn3rdPerson = value)
                .setDefaultValue(Config.ENABLE_IN_3RD_PERSON)
                .build());

            category.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("tpcrosshair.options.enable_in_3rd_person_front"), Config.enableIn3rdPersonFront)
                .setSaveConsumer(value -> Config.enableIn3rdPersonFront = value)
                .setDefaultValue(Config.ENABLE_IN_3RD_PERSON_FRONT)
                .build());

            category.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("tpcrosshair.options.enable_bow_draw_indicator"), Config.enableBowDrawIndicator)
                .setSaveConsumer(value -> Config.enableBowDrawIndicator = value)
                .setDefaultValue(Config.ENABLE_BOW_DRAW_INDICATOR)
                .build());

            category.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("tpcrosshair.options.enable_trident_charge_indicator"), Config.enableTridentChargeIndicator)
                .setSaveConsumer(value -> Config.enableTridentChargeIndicator = value)
                .setDefaultValue(Config.ENABLE_TRIDENT_CHARGE_INDICATOR)
                .build());

            builder.setSavingRunnable(() -> {
                Config.save();
            });

            return builder.build();
        };
    }
}
