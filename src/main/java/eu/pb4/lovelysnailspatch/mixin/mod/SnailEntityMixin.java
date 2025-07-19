package eu.pb4.lovelysnailspatch.mixin.mod;

import dev.lambdaurora.lovely_snails.entity.SnailEntity;
import eu.pb4.lovelysnailspatch.impl.MovementHandler;
import eu.pb4.lovelysnailspatch.mixin.EntityAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SnailEntity.class)
public abstract class SnailEntityMixin extends TameableEntity {
    @Shadow public abstract boolean isScared();

    @Shadow public abstract boolean canUseSnail(Entity entity);

    protected SnailEntityMixin(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    }

    /**
     * @author Patbox
     * @reason Replace with server side friendly logic™
     */
    @Overwrite
    public void travel(Vec3d movementInput) {
        if (this.isAlive()) {
            var passenger  = this.getControllingPassenger();
            if (passenger instanceof LivingEntity rider && this.hasSaddleEquipped() && this.canUseSnail(passenger)) {
                if (this.isScared()) {
                    return;
                }

                var dirSpeed = passenger instanceof ServerPlayerEntity player
                        ? MovementHandler.getMovementWithAppliedFactors(player)
                        : new Vec2f(rider.sidewaysSpeed, rider.forwardSpeed);

                this.setYaw(rider.getYaw());
                this.lastYaw = this.getYaw();
                this.setPitch(rider.getPitch() * 0.5F);
                this.setRotation(this.getYaw(), this.getPitch());
                this.bodyYaw = this.getYaw();
                this.headYaw = this.bodyYaw;
                float sidewaysSpeed = dirSpeed.x * 0.25F;
                float forwardSpeed = dirSpeed.y * 0.4F;
                if (forwardSpeed <= 0.0F) {
                    forwardSpeed *= 0.25F;
                }

                if (super.isControlledByPlayer()) {
                    this.setMovementSpeed((float) this.getAttributeValue(EntityAttributes.MOVEMENT_SPEED));
                    super.travel(new Vec3d(sidewaysSpeed, movementInput.y, forwardSpeed));
                }

                this.updateLimbs(false);
            } else {
                super.travel(movementInput);
            }
        }

    }

    @Redirect(method = "handleStatus", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addParticleClient(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V"))
    private void serverSideParticle(World instance, ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        if (instance instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(parameters, x, y, z, 0, velocityX, velocityY, velocityZ, 1);
        }
    }

    @Override
    protected void showEmoteParticle(boolean positive) {
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            ParticleEffect particleEffect = ParticleTypes.HEART;
            if (!positive) {
                particleEffect = ParticleTypes.SMOKE;
            }

            for (int i = 0; i < 7; ++i) {
                double d = this.random.nextGaussian() * 0.02;
                double e = this.random.nextGaussian() * 0.02;
                double f = this.random.nextGaussian() * 0.02;
                serverWorld.spawnParticles(particleEffect, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), 0, d, e, f, 1);
            }
        }
    }

    @Override
    public boolean isControlledByPlayer() {
        return false;
    }
}
