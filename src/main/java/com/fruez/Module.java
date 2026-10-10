package com.fruez;

public abstract class Module {
    private final String name;
    private final Category category;
    private boolean enabled;

    public Module(String name, Category category) {
        this.name = name;
        this.category = category;
        this.enabled = false;
    }

    public void toggle() {
        enabled = !enabled;
        if (enabled) onEnable();
        else onDisable();
    }

    protected void onEnable() {}
    protected void onDisable() {}
    public void onTick() {}

    public String getName() { return name; }
    public Category getCategory() { return category; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean e) {
        if (this.enabled == e) return;
        this.enabled = e;
        if (e) onEnable();
        else onDisable();
    }

    public enum Category {
        COMBAT("Combat"),
        MOVEMENT("Movement"),
        RENDER("Render"),
        PLAYER("Player"),
        MISC("Misc");

        public final String displayName;
        Category(String d) { this.displayName = d; }
    }
}