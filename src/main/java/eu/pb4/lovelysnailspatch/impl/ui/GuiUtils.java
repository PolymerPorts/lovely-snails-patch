package eu.pb4.lovelysnailspatch.impl.ui;

import eu.pb4.sgui.api.elements.GuiElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class GuiUtils {
    public static final GuiElement EMPTY = GuiElement.EMPTY;

    public static void playClickSound(ServerPlayerEntity player) {
        player.playSoundToPlayer(SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.UI, 0.5f, 1);
    }
}

