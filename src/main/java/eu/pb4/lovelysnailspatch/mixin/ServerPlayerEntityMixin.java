package eu.pb4.lovelysnailspatch.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.lambdaurora.lovely_snails.screen.SnailScreenHandler;
import eu.pb4.lovelysnailspatch.impl.entity.SnailPolymerEntity;
import eu.pb4.lovelysnailspatch.impl.ui.SnailUi;
import eu.pb4.polymer.core.api.entity.PolymerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BundleS2CPacket;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.OptionalInt;

@Mixin(value = ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Shadow public ServerPlayNetworkHandler networkHandler;

    @Inject(method = "openHandledScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V", shift = At.Shift.BEFORE), cancellable = true)
    private void openCustomScreen(NamedScreenHandlerFactory factory, CallbackInfoReturnable<OptionalInt> cir, @Local ScreenHandler handler) {
        if (handler instanceof SnailScreenHandler snailScreenHandler) {
            new SnailUi( (ServerPlayerEntity) (Object) this, snailScreenHandler);
            cir.setReturnValue(OptionalInt.empty());
        }
    }

    @ModifyArg(method = "startRiding", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"))
    private Packet<?> replacePacket(Packet<?> par1, @Local(argsOnly = true) Entity entity) {
        if (PolymerEntity.get(entity) instanceof SnailPolymerEntity snailPolymerEntity) {
            snailPolymerEntity.onEntityPacketSent(this.networkHandler::sendPacket, par1);
            return new BundleS2CPacket(List.of());
        }

        return par1;
    }

    @ModifyArg(method = "dismountVehicle", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"))
    private Packet<?> replacePacket2(Packet<?> par1, @Local Entity entity) {
        if (PolymerEntity.get(entity) instanceof SnailPolymerEntity snailPolymerEntity) {
            snailPolymerEntity.onEntityPacketSent(this.networkHandler::sendPacket, par1);
            return new BundleS2CPacket(List.of());
        }

        return par1;
    }
}