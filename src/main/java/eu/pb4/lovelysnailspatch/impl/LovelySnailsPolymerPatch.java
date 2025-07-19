package eu.pb4.lovelysnailspatch.impl;

import dev.lambdaurora.lovely_snails.entity.SnailEntity;
import dev.lambdaurora.lovely_snails.registry.LovelySnailsRegistry;
import eu.pb4.lovelysnailspatch.impl.entity.SnailPolymerEntity;
import eu.pb4.lovelysnailspatch.impl.item.PolyBaseItem;
import eu.pb4.lovelysnailspatch.impl.res.ResourcePackGenerator;
import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.other.PolymerScreenHandlerUtils;
import eu.pb4.polymer.core.api.other.PolymerSoundEvent;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.polymer.resourcepack.extras.api.ResourcePackExtras;
import eu.pb4.polymer.resourcepack.extras.api.format.item.ItemAsset;
import eu.pb4.polymer.resourcepack.extras.api.format.item.model.BasicItemModel;
import eu.pb4.polymer.resourcepack.extras.api.format.item.tint.MapColorTintSource;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class LovelySnailsPolymerPatch implements ModInitializer {
    public static final String MOD_ID = "lovely-snails-polymer-patch";
    public static final Logger LOGGER = LoggerFactory.getLogger("lovely-snails-polymer-patch");

    @Override
    public void onInitialize() {
        PolymerResourcePackUtils.addModAssets("lovely_snails");
        PolymerResourcePackUtils.addModAssets("lovely-snails-polymer-patch");
        PolymerResourcePackUtils.addModAssets(MOD_ID);
        ResourcePackExtras.forDefault().addBridgedModelsFolder(Identifier.of("lovely_snails", "entity"), (id, b) -> {
            return new ItemAsset(new BasicItemModel(id, List.of(new MapColorTintSource(0xFFFFFF))), new ItemAsset.Properties(true, true));
        });
        ResourcePackExtras.forDefault().addBridgedModelsFolder(Identifier.of("lovely-snails-patch", "sgui"), (id, b) -> {
            return new ItemAsset(new BasicItemModel(id), new ItemAsset.Properties(true, true));
        });

        ResourcePackGenerator.setup();

        PolymerItem.registerOverlay(LovelySnailsRegistry.SNAIL_SPAWN_EGG_ITEM, new PolyBaseItem(LovelySnailsRegistry.SNAIL_SPAWN_EGG_ITEM));
        PolymerEntityUtils.registerOverlay(LovelySnailsRegistry.SNAIL_ENTITY_TYPE, entity -> new SnailPolymerEntity((SnailEntity) entity));
        PolymerScreenHandlerUtils.registerType(LovelySnailsRegistry.SNAIL_SCREEN_HANDLER_TYPE);
        PolymerSoundEvent.registerOverlay(LovelySnailsRegistry.SNAIL_DEATH_SOUND_EVENT);
        PolymerSoundEvent.registerOverlay(LovelySnailsRegistry.SNAIL_HURT_SOUND_EVENT);
    }

    public static Identifier id(String path) {
        return Identifier.of("lovely-snails-patch", path);
    }
}