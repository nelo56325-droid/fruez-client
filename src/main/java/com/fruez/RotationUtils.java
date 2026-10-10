package com.fruez.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class RotationUtils {

    public static void faceEntity(Entity target) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        Vec3d eye = mc.player.getEyePos();
        Vec3d tPos = target.getPos().add(0, target.getHeight() / 2.0, 0);

        double dx = tPos.x - eye.x;
        double dy = tPos.y - eye.y;
        double dz = tPos.z - eye.z;
        double distXZ = Math.sqrt(dx * dx + dz * dz);

        float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90);
        float pitch = (float) (-Math.toDegrees(Math.atan2(dy, distXZ)));

        mc.player.setYaw(yaw);
        mc.player.setPitch(pitch);
    }

    public static boolean isInFov(Entity target, double fov) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return false;

        Vec3d eye = mc.player.getEyePos();
        Vec3d tPos = target.getPos().add(0, target.getHeight() / 2.0, 0);
        Vec3d look = mc.player.getRotationVec(1.0f).normalize();
        Vec3d toTarget = tPos.subtract(eye).normalize();

        double dot = look.dotProduct(toTarget);
        double angle = Math.toDegrees(Math.acos(Math.max(-1, Math.min(1, dot))));
        return angle <= fov / 2.0;
    }

    public static double distanceTo(Entity target) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return 999;
        return mc.player.distanceTo(target);
    }
}