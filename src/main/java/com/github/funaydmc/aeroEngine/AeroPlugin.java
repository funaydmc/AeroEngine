package com.github.funaydmc.aeroEngine;

import com.github.funaydmc.aeroEngine.module.ModuleManager;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class AeroPlugin extends JavaPlugin {
    private final ModuleManager moduleManager = new ModuleManager(this);

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

}
