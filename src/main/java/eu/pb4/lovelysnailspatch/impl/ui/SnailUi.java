package eu.pb4.lovelysnailspatch.impl.ui;

import dev.lambdaurora.lovely_snails.screen.SnailScreenHandler;
import eu.pb4.lovelysnailspatch.impl.res.GuiTextures;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.inventory.MenuType;

public class SnailUi extends SimpleGui {
    private final SnailScreenHandler wrapped;

    public SnailUi(ServerPlayer player, SnailScreenHandler wrapped) {
        super(MenuType.GENERIC_9x3, player, false);
        this.wrapped = wrapped;
        this.setTitle(GuiTextures.SNAIL.apply(this.wrapped.snail().getDisplayName()));
        int slot = 0;

        this.setSlot(1, wrapped.getSlot(slot++)); // Saddle
        this.setSlot(1 + 9, wrapped.getSlot(slot++)); // Decor
        this.setSlot(0, wrapped.getSlot(slot++)); // Chest 1
        this.setSlot(9, wrapped.getSlot(slot++)); // Chest 2
        this.setSlot(9 * 2, wrapped.getSlot(slot++)); // Chest 3

        this.updateCurrentStoragePage();

        this.open();
    }

    @Override
    public void onTick() {
        super.onTick();
        this.updateCurrentStoragePage();
    }

    @Override
    public void onRemoved() {
        super.onRemoved();
        this.wrapped.removed(this.player);
    }

    private void updateCurrentStoragePage() {
        for (int i = 0; i < 3; i++) {
            if (this.wrapped.hasChest(i)) {
                int finalI = i;
                this.setSlot(8 + 9 * i, GuiTextures.CHEST[i].get().hideTooltip().setCallback(() -> {
                    GuiUtils.playClickSound(player);
                    this.wrapped.setCurrentStoragePage(finalI);
                }));
            } else {
                this.clearSlot(8 + 9 * i);
            }
        }

        if (this.wrapped.hasEnderChest()) {
            this.setSlot(9 * 2 + 1, GuiTextures.ENDER_CHEST.get().hideTooltip().setCallback(() -> {
                GuiUtils.playClickSound(player);
                player.connection.send(new ClientboundSoundEntityPacket(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.ENDER_CHEST_OPEN), SoundSource.UI, player, 0.5f, this.wrapped.snail().getRandom().nextFloat() * 0.1F + 0.9F, this.wrapped.snail().getRandom().nextLong()));
                this.wrapped.snail().openEnderChestInventory(player);
            }));
        } else {
            this.clearSlot(9 * 2 + 1);
        }


        if (this.wrapped.hasChests()) {
            var slot = 5 + this.wrapped.getCurrentStoragePage() * 15;
            for (var y = 0; y < 3; y++) {
                for (int x = 0; x < 5; x++) {
                    this.setSlot(y * 9 + x + 3, this.wrapped.getSlot(slot++));
                }
            }
        } else {
            for (var y = 0; y < 3; y++) {
                for (int x = 0; x < 5; x++) {
                    this.setSlot(y * 9 + x + 3, GuiTextures.FILLER.get().hideTooltip());
                }
            }
        }
    }
}
