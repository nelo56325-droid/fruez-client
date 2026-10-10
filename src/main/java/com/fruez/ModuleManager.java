package com.fruez;

import com.fruez.modules.combat.KillAura;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {

    public static final List<Module> MODULES = new ArrayList<>();

    public static KillAura killAura;

    public static void init() {
        killAura = new KillAura();
        MODULES.add(killAura);
    }

    public static void onTick() {
        for (Module m : MODULES) {
            if (m.isEnabled()) {
                try {
                    m.onTick();
                } catch (Exception e) {
                    System.err.println("[Fruez] " + m.getName() + " error: " + e.getMessage());
                }
            }
        }
    }

    public static List<Module> getByCategory(Module.Category cat) {
        List<Module> out = new ArrayList<>();
        for (Module m : MODULES) {
            if (m.getCategory() == cat) out.add(m);
        }
        return out;
    }
}