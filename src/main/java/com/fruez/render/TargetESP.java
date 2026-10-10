package com.fruez.render;

import com.fruez.ModuleManager;
import com.fruez.modules.combat.KillAura;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class TargetESP {

    public static float rotation = 0f;

    public static void onTick() {
        rotation += 10f;
        if (rotation >= 360f) rotation -= 360f;
    }

    public static void render(MatrixStack matrices, Camera camera) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;

        KillAura killAura = ModuleManager.killAura;
        if (killAura == null || !killAura.isEnabled()) return;

        Entity target = killAura.getCurrentTarget();
        if (target == null) return;

        Vec3d camPos = camera.getPos();
        double x = target.getX() - camPos.x;
        double y = target.getY() - camPos.y;
        double z = target.getZ() - camPos.z;

        double width = target.getWidth() * 1.2;
        double height = target.getHeight();

        matrices.push();
        matrices.translate(x, y, z);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.lineWidth(2.5f);

        Matrix4f matrix = matrices.peek().getPositionMatrix();

        float r = 0.9f, g = 0.3f, b = 1.0f, a = 0.9f;

        drawRotatedX(matrix, (float) height / 2f, width, rotation, r, g, b, a);

        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();

        matrices.pop();
    }

    private static void drawRotatedX(Matrix4f matrix, float yOffset, double width, float rot, float r, float g, float b, float a) {
        MatrixStack temp = new MatrixStack();
        temp.translate(0, yOffset, 0);
        temp.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rot));
        Matrix4f m = temp.peek().getPositionMatrix();

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        float w = (float) width;

        buffer.vertex(m, -w, 0, -w).color(r, g, b, a).next();
        buffer.vertex(m, w, 0, w).color(r, g, b, a).next();

        buffer.vertex(m, w, 0, -w).color(r, g, b, a).next();
        buffer.vertex(m, -w, 0, w).color(r, g, b, a).next();

        buffer.vertex(m, -w, 0, -w).color(r, g, b, a * 0.5f).next();
        buffer.vertex(m, -w, yOffset * 2f, -w).color(r, g, b, a * 0.5f).next();

        buffer.vertex(m, w, 0, w).color(r, g, b, a * 0.5f).next();
        buffer.vertex(m, w, yOffset * 2f, w).color(r, g, b, a * 0.5f).next();

        buffer.vertex(m, w, 0, -w).color(r, g, b, a * 0.5f).next();
        buffer.vertex(m, w, yOffset * 2f, -w).color(r, g, b, a * 0.5f).next();

        buffer.vertex(m, -w, 0, w).color(r, g, b, a * 0.5f).next();
        buffer.vertex(m, -w, yOffset * 2f, w).color(r, g, b, a * 0.5f).next();

        BufferRenderer.drawWithGlobalProgram(buffer.end());
    }
}