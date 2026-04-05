package eu.pb4.lovelysnailspatch.impl.res;

import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import eu.pb4.sgui.api.elements.GuiElementBuilderCreator;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import static eu.pb4.lovelysnailspatch.impl.res.UiResourceCreator.*;

public class GuiTextures {
    public static final Function<Component, Component> SNAIL = background("snail");
    public static final Supplier<GuiElementBuilder>[] CHEST = new Supplier[] { icon32("chest_1"), icon32("chest_2"), icon32("chest_3") };
    public static final Supplier<GuiElementBuilder> ENDER_CHEST = icon32("ender_chest");
    public static final Supplier<GuiElementBuilder> FILLER = icon32("filler");

    public static void register() {
    }
}
