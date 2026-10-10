package com.fruez;

import com.fruez.render.TargetESP;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class FruezClient implements ClientModInitializer {

    public static KeyBinding killAuraKey;

    @Override
    public void onInitializeClient() {
        System.out.println("[Fruez Client] Loading...");

        ModuleManager.init();

        killAuraKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.fruez.killaura",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category.fruez"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (killAuraKey.wasPressed()) {
                ModuleManager.killAura.toggle();
                if (client.player != null) {
                    client.player.sendMessage(
                        Text.literal("§bKillAura: " +
                            (ModuleManager.killAura.isEnabled() ? "§aON" : "§cOFF")),
                        true
                    );
                }
            }

            ModuleManager.onTick();
            TargetESP.onTick();
        });

        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            TargetESP.render(context.matrixStack(), context.camera());
        });

        System.out.println("[Fruez Client] Ready!");
    }
}