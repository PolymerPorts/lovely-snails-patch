package eu.pb4.lovelysnailspatch.impl.entity;

import dev.lambdaurora.lovely_snails.LovelySnails;
import dev.lambdaurora.lovely_snails.entity.SnailEntity;
import eu.pb4.polymer.core.api.entity.PolymerEntity;
import eu.pb4.polymer.virtualentity.api.VirtualEntityUtils;
import eu.pb4.polymer.virtualentity.api.attachment.IdentifiedUniqueEntityAttachment;
import eu.pb4.polymer.virtualentity.api.attachment.UniqueIdentifiableAttachment;
import eu.pb4.polymer.virtualentity.api.tracker.DisplayTrackedData;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAttachS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;
import java.util.function.Consumer;

public record SnailPolymerEntity(SnailEntity entity) implements PolymerEntity {
    public static final Identifier MODEL = LovelySnails.id("model");
    public SnailPolymerEntity {
        IdentifiedUniqueEntityAttachment.ofTicking(MODEL, new SnailEntityModelHandler(entity), entity);
    }

    @Override
    public void onEntityPacketSent(Consumer<Packet<?>> consumer, Packet<?> packet) {
        if (packet instanceof EntityAnimationS2CPacket) {
            return;
        }
        if (packet instanceof EntityPassengersSetS2CPacket packet1) {
            var model = (SnailEntityModelHandler) UniqueIdentifiableAttachment.get(entity, MODEL).holder();
            consumer.accept(VirtualEntityUtils.createRidePacket(entity.getId(), IntList.of(model.interaction.getEntityId())));
            consumer.accept(VirtualEntityUtils.createRidePacket(model.rideAttachment.getEntityId(), packet1.getPassengerIds()));
            return;
        }

        if (packet instanceof EntityAttachS2CPacket packet1) {
            var model = (SnailEntityModelHandler) UniqueIdentifiableAttachment.get(entity, MODEL).holder();
            consumer.accept(VirtualEntityUtils.createEntityAttachPacket(model.leadAttachment.getEntityId(), packet1.getHoldingEntityId()));
            return;
        }

        if (packet instanceof EntityStatusS2CPacket packet1) {
            emulateHandleStatus(this.entity, packet1.getStatus());
        }
        PolymerEntity.super.onEntityPacketSent(consumer, packet);
    }

    @Override
    public EntityType<?> getPolymerEntityType(PacketContext packetContext) {
        return EntityType.ITEM_DISPLAY;
    }

    @Override
    public void modifyRawTrackedData(List<DataTracker.SerializedEntry<?>> data, ServerPlayerEntity player, boolean initial) {
        PolymerEntity.super.modifyRawTrackedData(data, player, initial);
        if (initial) {
            data.add(DataTracker.SerializedEntry.of(DisplayTrackedData.TELEPORTATION_DURATION, 3));
        }
    }


    private static void addParticleClient(World world, ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        ((ServerWorld) world).spawnParticles(parameters, x, y, z, 0, velocityX, velocityY, velocityZ, 1);
    }

    private static void playEquipmentBreakEffects(LivingEntity entity, ItemStack stack) {
        if (!stack.isEmpty()) {
            var registryEntry = stack.get(DataComponentTypes.BREAK_SOUND);
            if (registryEntry != null && !entity.isSilent()) {
                entity.getEntityWorld().playSound(entity, entity.getX(), entity.getY(), entity.getZ(), registryEntry.value(), entity.getSoundCategory(), 0.8F, 0.8F + entity.getEntityWorld().getRandom().nextFloat() * 0.4F);
            }

            spawnItemParticles(entity, stack, 5);
        }
    }

    public static boolean emulateHandleStatus(Entity entity, byte status) {
        //
        {
            var world = entity.getEntityWorld();

            int i;
            double xOffset;
            double yOffset;
            double zOffset;
            if (status == 8) {
                for(i = 0; i < 7; ++i) {
                    xOffset = entity.getRandom().nextGaussian() * 0.02;
                    yOffset = entity.getRandom().nextGaussian() * 0.02;
                    zOffset = entity.getRandom().nextGaussian() * 0.02;
                    addParticleClient(world, entity.getRandom().nextBoolean() ? ParticleTypes.HAPPY_VILLAGER : ParticleTypes.HEART, entity.getParticleX(1.0), entity.getRandomBodyY() + 0.5, entity.getParticleZ(1.0), xOffset, yOffset, zOffset);
                }
                return true;
            } else if (status == 9) {
                for(i = 0; i < 7; ++i) {
                    xOffset = entity.getRandom().nextGaussian() * 0.02;
                    yOffset = entity.getRandom().nextGaussian() * 0.02;
                    zOffset = entity.getRandom().nextGaussian() * 0.02;
                    addParticleClient(world, ParticleTypes.ANGRY_VILLAGER, entity.getParticleX(1.0), entity.getRandomBodyY() + 0.5, entity.getParticleZ(1.0), xOffset, yOffset, zOffset);
                }
                return true;

            } else if (status == 10) {
                for(i = 0; i < 7; ++i) {
                    xOffset = entity.getRandom().nextGaussian() * 0.02;
                    yOffset = entity.getRandom().nextGaussian() * 0.02;
                    zOffset = entity.getRandom().nextGaussian() * 0.02;
                    addParticleClient(world, entity.getRandom().nextBoolean() ? ParticleTypes.ANGRY_VILLAGER : ParticleTypes.SMOKE, entity.getParticleX(1.0), entity.getRandomBodyY() + 0.5, entity.getParticleZ(1.0), xOffset, yOffset, zOffset);
                }
                return true;
            }
        }

        // Vanilla
        var world = entity.getEntityWorld();
        if (entity instanceof AnimalEntity && status == 18) {
            for (int i = 0; i < 7; ++i) {
                var x = entity.getRandom().nextGaussian() * 0.02;
                var y = entity.getRandom().nextGaussian() * 0.02;
                var z = entity.getRandom().nextGaussian() * 0.02;
                addParticleClient(world, ParticleTypes.HEART, entity.getParticleX(1.0), entity.getRandomBodyY() + 0.5, entity.getParticleZ(1.0), x, y, z);
            }
            return true;
        }

        if (entity instanceof MobEntity && status == 20) {
            for (int i = 0; i < 20; ++i) {
                var x = entity.getRandom().nextGaussian() * 0.02;
                var y = entity.getRandom().nextGaussian() * 0.02;
                var z = entity.getRandom().nextGaussian() * 0.02;
                addParticleClient(world, ParticleTypes.POOF, entity.getParticleX(1.0) - x * 10.0, entity.getRandomBodyY() - y * 10.0, entity.getParticleZ(1.0) - z * 10.0, x, y, z);
            }
            return true;
        }

        if (entity instanceof LivingEntity livingEntity) {
            switch (status) {
                case 46:
                    for (int j = 0; j < 128; ++j) {
                        var val = (double) j / 127.0;
                        var dx = (livingEntity.getRandom().nextFloat() - 0.5F) * 0.2F;
                        var dy = (livingEntity.getRandom().nextFloat() - 0.5F) * 0.2F;
                        var dz = (livingEntity.getRandom().nextFloat() - 0.5F) * 0.2F;
                        var x = MathHelper.lerp(val, livingEntity.lastX, livingEntity.getX()) + (livingEntity.getRandom().nextDouble() - 0.5) * (double) livingEntity.getWidth() * 2.0;
                        var y = MathHelper.lerp(val, livingEntity.lastY, livingEntity.getY()) + livingEntity.getRandom().nextDouble() * (double) livingEntity.getHeight();
                        var z = MathHelper.lerp(val, livingEntity.lastZ, livingEntity.getZ()) + (livingEntity.getRandom().nextDouble() - 0.5) * (double) livingEntity.getWidth() * 2.0;
                        addParticleClient(world, ParticleTypes.PORTAL, x, y, z, dx, dy, dz);
                    }
                    return true;
                case 47:
                    playEquipmentBreakEffects(livingEntity, livingEntity.getEquippedStack(EquipmentSlot.MAINHAND));
                    return true;
                case 48:
                    playEquipmentBreakEffects(livingEntity, livingEntity.getEquippedStack(EquipmentSlot.OFFHAND));
                    return true;
                case 49:
                    playEquipmentBreakEffects(livingEntity, livingEntity.getEquippedStack(EquipmentSlot.HEAD));
                    return true;
                case 50:
                    playEquipmentBreakEffects(livingEntity, livingEntity.getEquippedStack(EquipmentSlot.CHEST));
                    return true;
                case 51:
                    playEquipmentBreakEffects(livingEntity, livingEntity.getEquippedStack(EquipmentSlot.LEGS));
                    return true;
                case 52:
                    playEquipmentBreakEffects(livingEntity, livingEntity.getEquippedStack(EquipmentSlot.FEET));
                    return true;
                case 54:
                    var blockState = Blocks.HONEY_BLOCK.getDefaultState();

                    for (int i = 0; i < 10; ++i) {
                        addParticleClient(world, new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), entity.getX(), entity.getY(), entity.getZ(), 0.0, 0.0, 0.0);
                    }
                    return true;
                case 60:
                    for (int i = 0; i < 20; ++i) {
                        var x = entity.getRandom().nextGaussian() * 0.02;
                        var y = entity.getRandom().nextGaussian() * 0.02;
                        var z = entity.getRandom().nextGaussian() * 0.02;
                        addParticleClient(world, ParticleTypes.POOF, entity.getParticleX(1.0) - x * 10.0, entity.getRandomBodyY() - y * 10.0, entity.getParticleZ(1.0) - z * 10.0, x, y, z);
                    }
                    return true;
                case 65:
                    playEquipmentBreakEffects(livingEntity, livingEntity.getEquippedStack(EquipmentSlot.BODY));
                    return true;
                case 67:
                    var velocity = entity.getVelocity();

                    for (int i = 0; i < 8; ++i) {
                        var x = entity.getRandom().nextTriangular(0.0, 1.0);
                        var y = entity.getRandom().nextTriangular(0.0, 1.0);
                        var z = entity.getRandom().nextTriangular(0.0, 1.0);
                        addParticleClient(world, ParticleTypes.BUBBLE, entity.getX() + x, entity.getY() + y, entity.getZ() + z, velocity.x, velocity.y, velocity.z);
                    }
                    return true;
                case 68:
                    playEquipmentBreakEffects(livingEntity, livingEntity.getEquippedStack(EquipmentSlot.SADDLE));
                    return true;
            }
        }

        if (status == 53) {
            var blockState = Blocks.HONEY_BLOCK.getDefaultState();

            for (int i = 0; i < 5; ++i) {
                addParticleClient(world, new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), entity.getX(), entity.getY(), entity.getZ(), 0.0, 0.0, 0.0);
            }
            return true;
        }
        return false;
    }

    private static void spawnItemParticles(LivingEntity entity, ItemStack stack, int count) {
        for (int i = 0; i < count; ++i) {
            Vec3d vec3d = new Vec3d(((double) entity.getRandom().nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
            vec3d = vec3d.rotateX(-entity.getPitch() * 0.017453292F);
            vec3d = vec3d.rotateY(-entity.getYaw() * 0.017453292F);
            double d = (double) (-entity.getRandom().nextFloat()) * 0.6 - 0.3;
            Vec3d vec3d2 = new Vec3d(((double) entity.getRandom().nextFloat() - 0.5) * 0.3, d, 0.6);
            vec3d2 = vec3d2.rotateX(-entity.getPitch() * 0.017453292F);
            vec3d2 = vec3d2.rotateY(-entity.getYaw() * 0.017453292F);
            vec3d2 = vec3d2.add(entity.getX(), entity.getEyeY(), entity.getZ());
            addParticleClient(entity.getEntityWorld(), new ItemStackParticleEffect(ParticleTypes.ITEM, stack), vec3d2.x, vec3d2.y, vec3d2.z, vec3d.x, vec3d.y + 0.05, vec3d.z);
        }

    }
}
