package eu.pb4.lovelysnailspatch.impl.entity;

import com.mojang.math.Axis;
import dev.lambdaurora.lovely_snails.entity.SnailEntity;
import dev.lambdaurora.lovely_snails.registry.LovelySnailsRegistry;
import eu.pb4.factorytools.api.virtualentity.ItemDisplayElementUtil;
import eu.pb4.factorytools.api.virtualentity.emuvanilla.EntityModelTransforms;
import eu.pb4.factorytools.api.virtualentity.emuvanilla.poly.LeadAttachmentElement;
import eu.pb4.factorytools.api.virtualentity.emuvanilla.poly.ModelHandle;
import eu.pb4.factorytools.api.virtualentity.emuvanilla.poly.RideAttachmentElement;
import eu.pb4.lovelysnailspatch.impl.entity.model.EntityModels;
import eu.pb4.lovelysnailspatch.mixin.LivingEntityAccessor;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.elements.InteractionElement;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import eu.pb4.polymer.virtualentity.api.elements.VirtualElement;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4fStack;

public class SnailEntityModelHandler extends ElementHolder {
    private static final Matrix4fStack STACK = new Matrix4fStack(64);
    private static final Vec3 OFFSET = new Vec3(0, 0.2, 0);
    protected final InteractionElement interaction;
    protected final LeadAttachmentElement leadAttachment = new LeadAttachmentElement();
    protected final RideAttachmentElement rideAttachment = new RideAttachmentElement();
    private final SnailEntity entity;
    private final ModelHandle<SnailEntity> main = new ModelHandle<>(this, EntityModels.SNAIL, LovelySnailsRegistry.SNAIL_ENTITY_TYPE.getDimensions(), OFFSET);
    private final ModelHandle<SnailEntity> decor = new ModelHandle<>(this, OFFSET);
    private final ModelHandle<SnailEntity> saddle = new ModelHandle<>(this, EntityModels.SADDLE, LovelySnailsRegistry.SNAIL_ENTITY_TYPE.getDimensions(), OFFSET);

    private final ItemDisplayElement chest[] = new ItemDisplayElement[3];
    private boolean noTick = true;
    private boolean hasSaddle = false;
    private float height = -1;
    @Nullable
    private DyeColor decorColor = null;

    public SnailEntityModelHandler(SnailEntity entity) {

        this.saddle.setHidden(true, entity.getType().getDimensions());
        this.decor.setHidden(true, entity.getType().getDimensions());
        this.entity = entity;
        var interaction = VirtualElement.InteractionHandler.redirect(entity);
        this.interaction = new InteractionElement(interaction);
        this.interaction.setSendPositionUpdates(false);
        this.height = entity.getBbHeight();
        this.leadAttachment.setOffset(new Vec3(0, height / 2, 0));
        this.leadAttachment.setInteractionHandler(interaction);
        this.rideAttachment.setOffset(new Vec3(0, height, 0));
        this.interaction.setSize(entity.getBbWidth(), height);

        for (var i = 0; i < 3; i++) {
            this.chest[i] = ItemDisplayElementUtil.createSimple();
            this.chest[i].setOffset(OFFSET);
            this.chest[i].setInterpolationDuration(1);
            this.chest[i].setTeleportDuration(3);
        }

        this.rideAttachment.setInteractionHandler(interaction);
        this.addElement(leadAttachment);
        this.addElement(rideAttachment);
        this.addPassengerElement(this.interaction);
    }

    @Override
    public boolean startWatching(ServerGamePacketListenerImpl player) {
        if (noTick) {
            onTick();
        }
        return super.startWatching(player);
    }

    @Override
    protected void onTick() {
        noTick = false;

        this.rideAttachment.setMaxHealth(this.entity.getMaxHealth());
        this.rideAttachment.getSyncedData().set(LivingEntityAccessor.getDATA_HEALTH_ID(), this.entity.getHealth());
        this.interaction.setCustomName(this.entity.getCustomName());
        this.interaction.setCustomNameVisible(this.entity.isCustomNameVisible());
        this.rideAttachment.setYaw(entity.getYRot());
        if (entity.getBbHeight() != this.height) {
            this.height = entity.getBbHeight();
            this.interaction.setSize(entity.getBbWidth(), this.height);
            this.leadAttachment.setOffset(new Vec3(0, this.height / 2, 0));
            this.rideAttachment.setOffset(new Vec3(0, this.height, 0));
        }

        var saddle = this.entity.isSaddled();
        if (hasSaddle != saddle) {
            hasSaddle = saddle;
            this.saddle.setHidden(!saddle, this.entity.getType().getDimensions());
        }
        var decor = this.entity.getCarpetColor();

        if (decorColor != decor) {
            decorColor = decor;
            if (decor == null) {
                this.decor.setHidden(true, this.entity.getType().getDimensions());
            } else {
                this.decor.setModelAndShow(EntityModels.DECOR.get(decorColor), this.entity.getType().getDimensions());
            }
        }

        STACK.pushMatrix();

        STACK.translate(0.0F, -0.2f, 0.0F);
        EntityModelTransforms.livingEntityTransform(this.entity, STACK);

        this.main.update(this.entity, STACK);
        if (hasSaddle) {
            this.saddle.update(this.entity, STACK);
        }
        if (decorColor != null) {
            this.decor.update(this.entity, STACK);
        }

        float shellRotation = EntityModels.SNAIL.model().getCurrentModel(this.entity).getShell().xRot;
        var rightChest = entity.getChest(0);
        if (!rightChest.isEmpty()) {
            STACK.pushMatrix();
            STACK.rotate(Axis.XP.rotationDegrees(180.0F));
            STACK.rotate(Axis.XP.rotation(shellRotation));
            STACK.rotate(Axis.YP.rotationDegrees(90.0F));
            STACK.translate(0.65f, 0.2f, -0.505f);
            STACK.scale(1.25F, 1.25F, 1.25F);
            STACK.rotateY(Mth.PI);
            this.chest[0].setTransformation(STACK);
            this.chest[0].startInterpolationIfDirty();
            this.chest[0].setItem(rightChest);
            STACK.popMatrix();
            this.addElement(this.chest[0]);
        } else {
            this.removeElement(this.chest[0]);
        }

        var backChest = entity.getChest(1);
        if (!backChest.isEmpty()) {
            STACK.pushMatrix();
            STACK.rotate(Axis.XP.rotationDegrees(180.0F));
            STACK.rotate(Axis.XP.rotation(shellRotation));
            STACK.translate(0.0f, 0.2f, -0.94f);
            STACK.scale(1.25F, 1.25F, 1.25F);
            STACK.rotateY(Mth.PI);
            this.chest[1].setTransformation(STACK);
            this.chest[1].startInterpolationIfDirty();
            this.chest[1].setItem(backChest);
            STACK.popMatrix();
            this.addElement(this.chest[1]);
        } else {
            this.removeElement(this.chest[1]);
        }

        var leftChest = entity.getChest(2);
        if (!leftChest.isEmpty()) {
            STACK.pushMatrix();
            STACK.rotate(Axis.XP.rotationDegrees(180.0F));
            STACK.rotate(Axis.XP.rotation(shellRotation));
            STACK.rotate(Axis.YN.rotationDegrees(90.0F));
            STACK.translate(-0.65f, 0.2f, -0.505f);
            STACK.scale(1.25F, 1.25F, 1.25F);
            STACK.rotateY(Mth.PI);
            this.chest[2].setTransformation(STACK);
            this.chest[2].startInterpolationIfDirty();
            this.chest[2].setItem(leftChest);
            STACK.popMatrix();
            this.addElement(this.chest[2]);
        } else {
            this.removeElement(this.chest[2]);
        }

        STACK.popMatrix();
        super.onTick();
    }
}
