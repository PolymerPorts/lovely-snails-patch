package eu.pb4.lovelysnailspatch.impl;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec2;

public class MovementHandler {
    public static Vec2 getMovementWithAppliedFactors(ServerPlayer player) {
        return applyMovementSpeedFactors(getMovementInput(player.getLastClientInput()), player);
    }


    private static float getMovementMultiplier(boolean positive, boolean negative) {
        if (positive == negative) {
            return 0.0F;
        } else {
            return positive ? 1.0F : -1.0F;
        }
    }

    public static Vec2 getMovementInput(Input input) {
        float f = getMovementMultiplier(input.forward(), input.backward());
        float g = getMovementMultiplier(input.left(), input.right());
        return (new Vec2(g, f)).normalized();
    }

    private static Vec2 applyMovementSpeedFactors(Vec2 input, ServerPlayer player) {
        if (input.lengthSquared() == 0.0F) {
            return input;
        } else {
            Vec2 vec2f = input.scale(0.98F);
            if (player.isUsingItem() && !player.isPassenger()) {
                vec2f = vec2f.scale(0.2F);
            }

            if (player.isCrouching() || player.isVisuallyCrawling()) {
                float f = (float) player.getAttributeValue(Attributes.SNEAKING_SPEED);
                vec2f = vec2f.scale(f);
            }

            return applyDirectionalMovementSpeedFactors(vec2f);
        }
    }

    private static Vec2 applyDirectionalMovementSpeedFactors(Vec2 vec) {
        float f = vec.length();
        if (f <= 0.0F) {
            return vec;
        } else {
            Vec2 vec2f = vec.scale(1.0F / f);
            float g = getDirectionalMovementSpeedMultiplier(vec2f);
            float h = Math.min(f * g, 1.0F);
            return vec2f.scale(h);
        }
    }

    private static float getDirectionalMovementSpeedMultiplier(Vec2 vec) {
        float f = Math.abs(vec.x);
        float g = Math.abs(vec.y);
        float h = g > f ? f / g : g / f;
        return Mth.sqrt(1.0F + Mth.square(h));
    }
}
