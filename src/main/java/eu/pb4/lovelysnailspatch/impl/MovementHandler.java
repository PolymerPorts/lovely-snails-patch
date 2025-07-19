package eu.pb4.lovelysnailspatch.impl;

import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;

public class MovementHandler {
    public static Vec2f getMovementWithAppliedFactors(ServerPlayerEntity player) {
        return applyMovementSpeedFactors(getMovementInput(player.getPlayerInput()), player);
    }


    private static float getMovementMultiplier(boolean positive, boolean negative) {
        if (positive == negative) {
            return 0.0F;
        } else {
            return positive ? 1.0F : -1.0F;
        }
    }

    public static Vec2f getMovementInput(PlayerInput input) {
        float f = getMovementMultiplier(input.forward(), input.backward());
        float g = getMovementMultiplier(input.left(), input.right());
        return (new Vec2f(g, f)).normalize();
    }

    private static Vec2f applyMovementSpeedFactors(Vec2f input, ServerPlayerEntity player) {
        if (input.lengthSquared() == 0.0F) {
            return input;
        } else {
            Vec2f vec2f = input.multiply(0.98F);
            if (player.isUsingItem() && !player.hasVehicle()) {
                vec2f = vec2f.multiply(0.2F);
            }

            if (player.isInSneakingPose() || player.isCrawling()) {
                float f = (float) player.getAttributeValue(EntityAttributes.SNEAKING_SPEED);
                vec2f = vec2f.multiply(f);
            }

            return applyDirectionalMovementSpeedFactors(vec2f);
        }
    }

    private static Vec2f applyDirectionalMovementSpeedFactors(Vec2f vec) {
        float f = vec.length();
        if (f <= 0.0F) {
            return vec;
        } else {
            Vec2f vec2f = vec.multiply(1.0F / f);
            float g = getDirectionalMovementSpeedMultiplier(vec2f);
            float h = Math.min(f * g, 1.0F);
            return vec2f.multiply(h);
        }
    }

    private static float getDirectionalMovementSpeedMultiplier(Vec2f vec) {
        float f = Math.abs(vec.x);
        float g = Math.abs(vec.y);
        float h = g > f ? f / g : g / f;
        return MathHelper.sqrt(1.0F + MathHelper.square(h));
    }
}
