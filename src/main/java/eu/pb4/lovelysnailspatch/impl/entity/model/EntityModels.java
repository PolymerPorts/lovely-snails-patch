package eu.pb4.lovelysnailspatch.impl.entity.model;

import dev.lambdaurora.lovely_snails.LovelySnails;
import eu.pb4.factorytools.api.virtualentity.emuvanilla.PolyModelInstance;
import eu.pb4.factorytools.api.virtualentity.emuvanilla.model.CubeDeformation;
import eu.pb4.factorytools.api.virtualentity.emuvanilla.model.EntityModel;
import eu.pb4.factorytools.api.virtualentity.emuvanilla.model.LayerDefinition;
import eu.pb4.factorytools.api.virtualentity.emuvanilla.model.ModelPart;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.item.DyeColor;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface EntityModels {
    List<PolyModelInstance<?>> ALL = new ArrayList<>();
    PolyModelInstance<SnailModel> SNAIL = create(SnailModel::new, SnailModel.model(CubeDeformation.NONE), LovelySnails.id("entity/snail/snail"));

    Map<DyeColor, PolyModelInstance<SnailModel>> DECOR = Util.make(new EnumMap<>(DyeColor.class), m -> {
        PolyModelInstance<SnailModel> base = null;

        for (var dye : DyeColor.values()) {
            var id = LovelySnails.id("entity/snail/decor/" + dye.getSerializedName());
            m.put(dye, base == null ? base = create(SnailModel::new, SnailModel.model(new CubeDeformation(0.25f)), id) : withTexture(base, id));
        }
    });

    PolyModelInstance<SnailModel> SADDLE = create(SnailModel::new, SnailModel.model(new CubeDeformation(0.5f)), LovelySnails.id("entity/snail/saddle"));


    static <T extends EntityModel<?>> PolyModelInstance<T> create(Function<ModelPart, T> modelCreator, LayerDefinition data, Identifier texture) {
        var instance = PolyModelInstance.create(modelCreator, data, texture);
        ALL.add(instance);
        return instance;
    }

    static <T extends EntityModel<?>> PolyModelInstance<T> withTexture(PolyModelInstance<T> original, Identifier texture) {
        var instance = original.withTexture(texture);
        ALL.add(instance);
        return instance;
    }
}
