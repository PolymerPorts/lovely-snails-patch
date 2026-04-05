package eu.pb4.lovelysnailspatch.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.lambdaurora.lovely_snails.screen.SnailScreenHandler;
import eu.pb4.lovelysnailspatch.impl.entity.SnailPolymerEntity;
import eu.pb4.lovelysnailspatch.impl.ui.SnailUi;
import eu.pb4.polymer.core.api.entity.PolymerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.OptionalInt;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.AbstractContainerMenu;

@Mixin(value = ServerPlayer.class)
public class ServerPlayerEntityMixin {
    @Shadow public ServerGamePacketListenerImpl connection;

    @Inject(method = "openMenu", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V", shift = At.Shift.BEFORE), cancellable = true)
    private void openCustomScreen(MenuProvider factory, CallbackInfoReturnable<OptionalInt> cir, @Local AbstractContainerMenu handler) {
        if (handler instanceof SnailScreenHandler snailScreenHandler) {
            new SnailUi( (ServerPlayer) (Object) this, snailScreenHandler);
            cir.setReturnValue(OptionalInt.empty());
        }
    }

    @ModifyArg(method = "startRiding", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V"))
    private Packet<?> replacePacket(Packet<?> par1, @Local(argsOnly = true) Entity entity) {
        if (PolymerEntity.get(entity) instanceof SnailPolymerEntity snailPolymerEntity) {
            snailPolymerEntity.onEntityPacketSent(this.connection::send, par1);
            return new ClientboundBundlePacket(List.of());
        }

        return par1;
    }

    @ModifyArg(method = "removeVehicle", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V"))
    private Packet<?> replacePacket2(Packet<?> par1, @Local Entity entity) {
        if (PolymerEntity.get(entity) instanceof SnailPolymerEntity snailPolymerEntity) {
            snailPolymerEntity.onEntityPacketSent(this.connection::send, par1);
            return new ClientboundBundlePacket(List.of());
        }

        return par1;
    }
}