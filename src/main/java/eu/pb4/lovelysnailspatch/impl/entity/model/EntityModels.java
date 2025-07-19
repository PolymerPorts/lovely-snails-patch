package eu.pb4.lovelysnailspatch.impl.entity.model;

import dev.lambdaurora.lovely_snails.LovelySnails;
import eu.pb4.lovelysnailspatch.impl.entity.model.emuvanilla.PolyModelInstance;
import eu.pb4.lovelysnailspatch.impl.entity.model.emuvanilla.model.Dilation;
import eu.pb4.lovelysnailspatch.impl.entity.model.emuvanilla.model.EntityModel;
import eu.pb4.lovelysnailspatch.impl.entity.model.emuvanilla.model.ModelPart;
import eu.pb4.lovelysnailspatch.impl.entity.model.emuvanilla.model.TexturedModelData;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface EntityModels {
    List<PolyModelInstance<?>> ALL = new ArrayList<>();
    PolyModelInstance<SnailModel> SNAIL = create(SnailModel::new, SnailModel.model(Dilation.NONE), LovelySnails.id("entity/snail/snail"));

    Map<DyeColor, PolyModelInstance<SnailModel>> DECOR = Util.make(new EnumMap<>(DyeColor.class), m -> {
        PolyModelInstance<SnailModel> base = null;

        for (var dye : DyeColor.values()) {
            var id = LovelySnails.id("entity/snail/decor/" + dye.asString());
            m.put(dye, base == null ? base = create(SnailModel::new, SnailModel.model(new Dilation(0.25f)), id) : withTexture(base, id));
        }
    });

    PolyModelInstance<SnailModel> SADDLE = create(SnailModel::new, SnailModel.model(new Dilation(0.5f)), LovelySnails.id("entity/snail/saddle"));


    static <T extends EntityModel<?>> PolyModelInstance<T> create(Function<ModelPart, T> modelCreator, TexturedModelData data, Identifier texture) {
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
