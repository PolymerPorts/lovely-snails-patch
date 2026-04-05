package eu.pb4.lovelysnailspatch.impl.entity;

import dev.lambdaurora.lovely_snails.LovelySnails;
import dev.lambdaurora.lovely_snails.entity.SnailEntity;
import eu.pb4.polymer.core.api.entity.PolymerEntity;
import eu.pb4.polymer.virtualentity.api.VirtualEntityUtils;
import eu.pb4.polymer.virtualentity.api.attachment.IdentifiedUniqueEntityAttachment;
import eu.pb4.polymer.virtualentity.api.attachment.UniqueIdentifiableAttachment;
import eu.pb4.polymer.virtualentity.api.data.DisplayEntityData;
import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public record SnailPolymerEntity(SnailEntity entity) implements PolymerEntity {
    public static final Identifier MODEL = LovelySnails.id("model");
    public SnailPolymerEntity {
        IdentifiedUniqueEntityAttachment.ofTicking(MODEL, new SnailEntityModelHandler(entity), entity);
    }

    @Override
    public void onEntityPacketSent(Consumer<Packet<?>> consumer, Packet<?> packet) {
        if (packet instanceof ClientboundAnimatePacket) {
            return;
        }
        if (packet instanceof ClientboundSetPassengersPacket packet1) {
            var model = (SnailEntityModelHandler) UniqueIdentifiableAttachment.get(entity, MODEL).holder();
            consumer.accept(VirtualEntityUtils.createClientboundSetPassengersPacket(entity.getId(), IntList.of(model.interaction.getEntityId())));
            consumer.accept(VirtualEntityUtils.createClientboundSetPassengersPacket(model.rideAttachment.getEntityId(), packet1.getPassengers()));
            return;
        }

        if (packet instanceof ClientboundSetEntityLinkPacket packet1) {
            var model = (SnailEntityModelHandler) UniqueIdentifiableAttachment.get(entity, MODEL).holder();
            consumer.accept(VirtualEntityUtils.createClientboundSetEntityLinkPacket(model.leadAttachment.getEntityId(), packet1.getDestId()));
            return;
        }

        if (packet instanceof ClientboundEntityEventPacket packet1) {
            emulateHandleStatus(this.entity, packet1.getEventId());
        }
        PolymerEntity.super.onEntityPacketSent(consumer, packet);
    }

    @Override
    public EntityType<?> getPolymerEntityType(PacketContext packetContext) {
        return EntityType.ITEM_DISPLAY;
    }

    @Override
    public void modifyRawTrackedData(List<SynchedEntityData.DataValue<?>> data, ServerPlayer player, boolean initial) {
        PolymerEntity.super.modifyRawTrackedData(data, player, initial);
        if (initial) {
            data.add(SynchedEntityData.DataValue.create(DisplayEntityData.TELEPORTATION_DURATION, 3));
        }
    }


    private static void addParticleClient(Level world, ParticleOptions parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        ((ServerLevel) world).sendParticles(parameters, x, y, z, 0, velocityX, velocityY, velocityZ, 1);
    }

    private static void playEquipmentBreakEffects(LivingEntity entity, ItemStack stack) {
        if (!stack.isEmpty()) {
            var registryEntry = stack.get(DataComponents.BREAK_SOUND);
            if (registryEntry != null && !entity.isSilent()) {
                entity.level().playSound(entity, entity.getX(), entity.getY(), entity.getZ(), registryEntry.value(), entity.getSoundSource(), 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
            }

            spawnItemParticles(entity, stack, 5);
        }
    }

    public static boolean emulateHandleStatus(Entity entity, byte status) {
        //
        {
            var world = entity.level();

            int i;
            double xOffset;
            double yOffset;
            double zOffset;
            if (status == 8) {
                for(i = 0; i < 7; ++i) {
                    xOffset = entity.getRandom().nextGaussian() * 0.02;
                    yOffset = entity.getRandom().nextGaussian() * 0.02;
                    zOffset = entity.getRandom().nextGaussian() * 0.02;
                    addParticleClient(world, entity.getRandom().nextBoolean() ? ParticleTypes.HAPPY_VILLAGER : ParticleTypes.HEART, entity.getRandomX(1.0), entity.getRandomY() + 0.5, entity.getRandomZ(1.0), xOffset, yOffset, zOffset);
                }
                return true;
            } else if (status == 9) {
                for(i = 0; i < 7; ++i) {
                    xOffset = entity.getRandom().nextGaussian() * 0.02;
                    yOffset = entity.getRandom().nextGaussian() * 0.02;
                    zOffset = entity.getRandom().nextGaussian() * 0.02;
                    addParticleClient(world, ParticleTypes.ANGRY_VILLAGER, entity.getRandomX(1.0), entity.getRandomY() + 0.5, entity.getRandomZ(1.0), xOffset, yOffset, zOffset);
                }
                return true;

            } else if (status == 10) {
                for(i = 0; i < 7; ++i) {
                    xOffset = entity.getRandom().nextGaussian() * 0.02;
                    yOffset = entity.getRandom().nextGaussian() * 0.02;
                    zOffset = entity.getRandom().nextGaussian() * 0.02;
                    addParticleClient(world, entity.getRandom().nextBoolean() ? ParticleTypes.ANGRY_VILLAGER : ParticleTypes.SMOKE, entity.getRandomX(1.0), entity.getRandomY() + 0.5, entity.getRandomZ(1.0), xOffset, yOffset, zOffset);
                }
                return true;
            }
        }

        // Vanilla
        var world = entity.level();
        if (entity instanceof Animal && status == 18) {
            for (int i = 0; i < 7; ++i) {
                var x = entity.getRandom().nextGaussian() * 0.02;
                var y = entity.getRandom().nextGaussian() * 0.02;
                var z = entity.getRandom().nextGaussian() * 0.02;
                addParticleClient(world, ParticleTypes.HEART, entity.getRandomX(1.0), entity.getRandomY() + 0.5, entity.getRandomZ(1.0), x, y, z);
            }
            return true;
        }

        if (entity instanceof Mob && status == 20) {
            for (int i = 0; i < 20; ++i) {
                var x = entity.getRandom().nextGaussian() * 0.02;
                var y = entity.getRandom().nextGaussian() * 0.02;
                var z = entity.getRandom().nextGaussian() * 0.02;
                addParticleClient(world, ParticleTypes.POOF, entity.getRandomX(1.0) - x * 10.0, entity.getRandomY() - y * 10.0, entity.getRandomZ(1.0) - z * 10.0, x, y, z);
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
                        var x = Mth.lerp(val, livingEntity.xo, livingEntity.getX()) + (livingEntity.getRandom().nextDouble() - 0.5) * (double) livingEntity.getBbWidth() * 2.0;
                        var y = Mth.lerp(val, livingEntity.yo, livingEntity.getY()) + livingEntity.getRandom().nextDouble() * (double) livingEntity.getBbHeight();
                        var z = Mth.lerp(val, livingEntity.zo, livingEntity.getZ()) + (livingEntity.getRandom().nextDouble() - 0.5) * (double) livingEntity.getBbWidth() * 2.0;
                        addParticleClient(world, ParticleTypes.PORTAL, x, y, z, dx, dy, dz);
                    }
                    return true;
                case 47:
                    playEquipmentBreakEffects(livingEntity, livingEntity.getItemBySlot(EquipmentSlot.MAINHAND));
                    return true;
                case 48:
                    playEquipmentBreakEffects(livingEntity, livingEntity.getItemBySlot(EquipmentSlot.OFFHAND));
                    return true;
                case 49:
                    playEquipmentBreakEffects(livingEntity, livingEntity.getItemBySlot(EquipmentSlot.HEAD));
                    return true;
                case 50:
                    playEquipmentBreakEffects(livingEntity, livingEntity.getItemBySlot(EquipmentSlot.CHEST));
                    return true;
                case 51:
                    playEquipmentBreakEffects(livingEntity, livingEntity.getItemBySlot(EquipmentSlot.LEGS));
                    return true;
                case 52:
                    playEquipmentBreakEffects(livingEntity, livingEntity.getItemBySlot(EquipmentSlot.FEET));
                    return true;
                case 54:
                    var blockState = Blocks.HONEY_BLOCK.defaultBlockState();

                    for (int i = 0; i < 10; ++i) {
                        addParticleClient(world, new BlockParticleOption(ParticleTypes.BLOCK, blockState), entity.getX(), entity.getY(), entity.getZ(), 0.0, 0.0, 0.0);
                    }
                    return true;
                case 60:
                    for (int i = 0; i < 20; ++i) {
                        var x = entity.getRandom().nextGaussian() * 0.02;
                        var y = entity.getRandom().nextGaussian() * 0.02;
                        var z = entity.getRandom().nextGaussian() * 0.02;
                        addParticleClient(world, ParticleTypes.POOF, entity.getRandomX(1.0) - x * 10.0, entity.getRandomY() - y * 10.0, entity.getRandomZ(1.0) - z * 10.0, x, y, z);
                    }
                    return true;
                case 65:
                    playEquipmentBreakEffects(livingEntity, livingEntity.getItemBySlot(EquipmentSlot.BODY));
                    return true;
                case 67:
                    var velocity = entity.getDeltaMovement();

                    for (int i = 0; i < 8; ++i) {
                        var x = entity.getRandom().triangle(0.0, 1.0);
                        var y = entity.getRandom().triangle(0.0, 1.0);
                        var z = entity.getRandom().triangle(0.0, 1.0);
                        addParticleClient(world, ParticleTypes.BUBBLE, entity.getX() + x, entity.getY() + y, entity.getZ() + z, velocity.x, velocity.y, velocity.z);
                    }
                    return true;
                case 68:
                    playEquipmentBreakEffects(livingEntity, livingEntity.getItemBySlot(EquipmentSlot.SADDLE));
                    return true;
            }
        }

        if (status == 53) {
            var blockState = Blocks.HONEY_BLOCK.defaultBlockState();

            for (int i = 0; i < 5; ++i) {
                addParticleClient(world, new BlockParticleOption(ParticleTypes.BLOCK, blockState), entity.getX(), entity.getY(), entity.getZ(), 0.0, 0.0, 0.0);
            }
            return true;
        }
        return false;
    }

    private static void spawnItemParticles(LivingEntity entity, ItemStack stack, int count) {
        var t = ItemStackTemplate.fromNonEmptyStack(stack);
        for (int i = 0; i < count; ++i) {
            Vec3 vec3d = new Vec3(((double) entity.getRandom().nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
            vec3d = vec3d.xRot(-entity.getXRot() * 0.017453292F);
            vec3d = vec3d.yRot(-entity.getYRot() * 0.017453292F);
            double d = (double) (-entity.getRandom().nextFloat()) * 0.6 - 0.3;
            Vec3 vec3d2 = new Vec3(((double) entity.getRandom().nextFloat() - 0.5) * 0.3, d, 0.6);
            vec3d2 = vec3d2.xRot(-entity.getXRot() * 0.017453292F);
            vec3d2 = vec3d2.yRot(-entity.getYRot() * 0.017453292F);
            vec3d2 = vec3d2.add(entity.getX(), entity.getEyeY(), entity.getZ());
            addParticleClient(entity.level(), new ItemParticleOption(ParticleTypes.ITEM, t), vec3d2.x, vec3d2.y, vec3d2.z, vec3d.x, vec3d.y + 0.05, vec3d.z);
        }

    }
}
