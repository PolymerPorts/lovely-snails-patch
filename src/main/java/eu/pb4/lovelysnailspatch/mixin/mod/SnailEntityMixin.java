package eu.pb4.lovelysnailspatch.mixin.mod;

import dev.lambdaurora.lovely_snails.entity.SnailEntity;
import eu.pb4.lovelysnailspatch.impl.MovementHandler;
import eu.pb4.lovelysnailspatch.mixin.EntityAccessor;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SnailEntity.class)
public abstract class SnailEntityMixin extends TamableAnimal {
    @Shadow public abstract boolean isScared();

    @Shadow public abstract boolean canUseSnail(Entity entity);

    protected SnailEntityMixin(EntityType<? extends TamableAnimal> entityType, Level world) {
        super(entityType, world);
    }

    /**
     * @author Patbox
     * @reason Replace with server side friendly logic™
     */
    @Overwrite
    public void travel(Vec3 movementInput) {
        if (this.isAlive()) {
            var passenger  = this.getControllingPassenger();
            if (passenger instanceof LivingEntity rider && this.isSaddled() && this.canUseSnail(passenger)) {
                if (this.isScared()) {
                    return;
                }

                var dirSpeed = passenger instanceof ServerPlayer player
                        ? MovementHandler.getMovementWithAppliedFactors(player)
                        : new Vec2(rider.xxa, rider.zza);

                this.setYRot(rider.getYRot());
                this.yRotO = this.getYRot();
                this.setXRot(rider.getXRot() * 0.5F);
                this.setRot(this.getYRot(), this.getXRot());
                this.yBodyRot = this.getYRot();
                this.yHeadRot = this.yBodyRot;
                float sidewaysSpeed = dirSpeed.x * 0.25F;
                float forwardSpeed = dirSpeed.y * 0.4F;
                if (forwardSpeed <= 0.0F) {
                    forwardSpeed *= 0.25F;
                }

                if (super.isClientAuthoritative()) {
                    this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED));
                    super.travel(new Vec3(sidewaysSpeed, movementInput.y, forwardSpeed));
                }

                this.calculateEntityAnimation(false);
            } else {
                super.travel(movementInput);
            }
        }

    }

    @Redirect(method = "handleEntityEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"))
    private void serverSideParticle(Level instance, ParticleOptions parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        if (instance instanceof ServerLevel serverWorld) {
            serverWorld.sendParticles(parameters, x, y, z, 0, velocityX, velocityY, velocityZ, 1);
        }
    }

    @Override
    protected void spawnTamingParticles(boolean positive) {
        if (this.level() instanceof ServerLevel serverWorld) {
            ParticleOptions particleEffect = ParticleTypes.HEART;
            if (!positive) {
                particleEffect = ParticleTypes.SMOKE;
            }

            for (int i = 0; i < 7; ++i) {
                double d = this.random.nextGaussian() * 0.02;
                double e = this.random.nextGaussian() * 0.02;
                double f = this.random.nextGaussian() * 0.02;
                serverWorld.sendParticles(particleEffect, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), 0, d, e, f, 1);
            }
        }
    }

    @Override
    public boolean isClientAuthoritative() {
        return false;
    }
}
