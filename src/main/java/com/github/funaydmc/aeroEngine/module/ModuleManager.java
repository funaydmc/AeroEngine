package com.github.funaydmc.aeroEngine.module;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    private final Plugin plugin;
    public List<Module> modules = new ArrayList<>();

    public ModuleManager(Plugin plugin){
        this.plugin = plugin;
    }

    public void register(Module module){
        final NamespacedKey moduleKey = module.getKey();
        if (modules.stream().anyMatch(m -> m.getKey() == moduleKey)) {
            throw new DuplicateModuleRegistration(module);
        } else {
            modules.add(module);
            plugin.getLogger().info("Registered module: " + moduleKey);
        }
    }

    public void register(Class<? extends Module> moduleClass) {
        try {
            Module module = moduleClass.getConstructor(Plugin.class).newInstance(this.plugin);
            register(module);
        } catch (Exception e) {
            throw new RuntimeException("Failed to register module: " + moduleClass.getName(), e);
        }
    }

    public void enableAllModules() {
        for (Module module : modules) {
            try {
                module.enable();
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to enable module: " + module.getKey() + ". Error: " + e.getMessage());
            }
        }
    }

    public void disableAllModules() {
        for (Module module : modules) {
            try {
                module.disable();
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to disable module: " + module.getKey() + ". Error: " + e.getMessage());
            }
        }
    }

    public List<Module> getModules() {
        return modules;
    }

}
