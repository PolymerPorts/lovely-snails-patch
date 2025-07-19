package eu.pb4.lovelysnailspatch.impl.res;

import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import static eu.pb4.lovelysnailspatch.impl.res.UiResourceCreator.*;

public class GuiTextures {
    public static final Function<Text, Text> SNAIL = background("snail");
    public static final Supplier<GuiElementBuilder> EMPTY_BUILDER = icon16("empty");
    public static final Supplier<GuiElementBuilder>[] CHEST = new Supplier[] { icon32("chest_1"), icon32("chest_2"), icon32("chest_3") };
    public static final Supplier<GuiElementBuilder> ENDER_CHEST = icon32("ender_chest");
    public static final Supplier<GuiElementBuilder> FILLER = icon32("filler");
    public static final GuiElement EMPTY = EMPTY_BUILDER.get().hideTooltip().build();
    public static final char SPACE_1 = UiResourceCreator.space(1);
    public static final char POLYDEX_OFFSET = UiResourceCreator.space(168);
    public static final char POLYDEX_OFFSET_N = UiResourceCreator.space(-168);

    public static void register() {
    }

    public record Progress(GuiElement[] elements, ItemStack[] withTooltip) {
        public GuiElement get(float progress) {
            return elements[Math.min((int) (progress * elements.length), elements.length - 1)];
        }

        public GuiElement getCeil(float progress) {
            return elements[Math.min((int) Math.ceil(progress * elements.length), elements.length - 1)];
        }

        public ItemStack getNamed(float progress, Text text) {
            var base = withTooltip[Math.min((int) (progress * withTooltip.length), withTooltip.length - 1)].copy();
            base.set(DataComponentTypes.ITEM_NAME, text);
            return base;
        }

        private static Progress create(int size, IntFunction<GuiElementBuilder> function) {
            var elements = new GuiElement[size + 1];
            var withTooltip = new ItemStack[size + 1];

            elements[0] = EMPTY;
            withTooltip[0] = EMPTY.getItemStack().copy();

            for (var i = 1; i <= size; i++) {
                elements[i] = function.apply(i - 1).hideTooltip().build();
                withTooltip[i] = function.apply(i - 1).asStack();
            }
            return new Progress(elements, withTooltip);
        }

        public static Progress createVertical(String path, int start, int stop, boolean reverse) {
            var size = stop - start;
            var function = verticalProgress16(path, start, stop, reverse);

            return create(size, function);
        }

        public static Progress createHorizontal(String path, int start, int stop, boolean reverse) {
            var size = stop - start;
            var function = horizontalProgress16(path, start, stop, reverse, 0);

            return create(size, function);
        }

        public static Progress createHorizontal(String path, int start, int stop, boolean reverse, int offset) {
            var size = stop - start;
            var function = horizontalProgress16(path, start, stop, reverse, offset);

            return create(size, function);
        }

        public static Progress createHorizontal32(String path, int start, int stop, boolean reverse) {
            var size = stop - start;
            var function = horizontalProgress32(path, start, stop, reverse);

            return create(size, function);
        }

        public static Progress createHorizontal32Right(String path, int start, int stop, boolean reverse) {
            var size = stop - start;
            var function = horizontalProgress32Right(path, start, stop, reverse);

            return create(size, function);
        }
        public static Progress createVertical32Right(String path, int start, int stop, boolean reverse) {
            var size = stop - start;
            var function = verticalProgress32Right(path, start, stop, reverse);

            return create(size, function);
        }
    }

}
