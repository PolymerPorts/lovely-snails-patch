package eu.pb4.lovelysnailspatch.impl.res;


import eu.pb4.lovelysnailspatch.impl.entity.model.EntityModels;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.polymer.resourcepack.api.ResourcePackBuilder;
import eu.pb4.polymer.resourcepack.extras.api.format.atlas.AtlasAsset;


public class ResourcePackGenerator {
    public static void setup() {
        PolymerResourcePackUtils.RESOURCE_PACK_AFTER_INITIAL_CREATION_EVENT.register(ResourcePackGenerator::build);
        UiResourceCreator.setup();
        GuiTextures.register();
    }

    private static void build(ResourcePackBuilder builder) {
        var atlas = AtlasAsset.builder();

        for (var model : EntityModels.ALL) {
            model.generateAssets(builder::addData, atlas);
        }

        builder.addData("assets/minecraft/atlases/blocks.json", atlas.build());
    }
}