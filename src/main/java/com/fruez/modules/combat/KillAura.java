package com.fruez.modules.combat;

import com.fruez.Module;
import com.fruez.utils.RotationUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Hand;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class KillAura extends Module {

    public double range = 4.0;
    public double fov = 180.0;
    public int minCps = 8;
    public int maxCps = 14;
    public boolean targetPlayers = true;
    public boolean targetMobs = false;
    public boolean autoRotate = true;
    public boolean swingHand = true;
    public boolean onlyWeapon = true;

    private long lastAttack = 0;
    private long nextAttackDelay = 100;
    private Entity currentTarget = null;

    public KillAura() {
        super("KillAura", Category.COMBAT);
    }

    @Override
    protected void onDisable() {
        currentTarget = null;
    }

    @Override
    public void onTick() {
        if (!isEnabled()) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;
        if (mc.player.isDead()) return;
        if (onlyWeapon && !isHoldingWeapon(mc)) return;

        long now = System.currentTimeMillis();
        if (now - lastAttack < nextAttackDelay) return;

        List<Entity> targets = findTargets(mc);
        if (targets.isEmpty()) {
            currentTarget = null;
            return;
        }

        Entity target = targets.get(0);
        currentTarget = target;

        if (autoRotate) RotationUtils.faceEntity(target);

        mc.interactionManager.attackEntity(mc.player, target);
        if (swingHand) mc.player.swingHand(Hand.MAIN_HAND);

        int cps = minCps + (int) (Math.random() * (maxCps - minCps + 1));
        nextAttackDelay = 1000L / Math.max(1, cps);
        lastAttack = now;
    }

    private List<Entity> findTargets(MinecraftClient mc) {
        List<Entity> result = new ArrayList<>();

        for (Entity e : mc.world.getEntities()) {
            if (e == mc.player) continue;
            if (!(e instanceof LivingEntity)) continue;
            if (!e.isAlive()) continue;
            if (e.isInvisible()) continue;

            if (e instanceof PlayerEntity) {
                if (!targetPlayers) continue;
                if (((PlayerEntity) e).isCreative()) continue;
                if (((PlayerEntity) e).isSpectator()) continue;
            } else if (e instanceof HostileEntity) {
                if (!targetMobs) continue;
            } else {
                continue;
            }

            double dist = RotationUtils.distanceTo(e);
            if (dist > range) continue;
            if (!RotationUtils.isInFov(e, fov)) continue;

            result.add(e);
        }

        result.sort(Comparator.comparingDouble(RotationUtils::distanceTo));
        return result;
    }

    private boolean isHoldingWeapon(MinecraftClient mc) {
        if (mc.player == null) return false;
        var item = mc.player.getMainHandStack().getItem();
        return item instanceof SwordItem || item instanceof AxeItem;
    }

    public Entity getCurrentTarget() {
        return currentTarget;
    }
}