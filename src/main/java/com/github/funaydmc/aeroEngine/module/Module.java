package com.github.funaydmc.aeroEngine.module;

import com.github.funaydmc.aeroEngine.config.ConfigManager;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.function.Consumer;

public abstract class Module {

    protected static Module instance;
    protected final Plugin plugin;
    protected boolean enabled = false;
    protected List<ConfigManager> configManagers;
    protected List<EventListener<?>> listeners;
    protected List<Module> subModules;

    public Module(Plugin plugin) {
        instance = this;
        this.plugin = plugin;
    }

    /**
     * Enables this module, loading configuration and enabling submodules.
     * Calls {@link #onEnable()} for custom enable logic.
     * Handles exceptions and logs errors.
     */
    public void enable() {
        try {
            if (configManagers != null) {
                for (ConfigManager configManager : configManagers) {
                    configManager.saveDefaults().load();
                }
            }
            onEnable();
            if (subModules != null) {
                for (Module subModule : subModules) {
                    subModule.enable();
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to enable module: " + getKey() + ". Error: " + e.getMessage());
        } finally {
            enabled = true;
        }
    }

    /**
     * Disables this module and all submodules, unregisters listeners.
     * Calls {@link #onDisable()} for custom disable logic.
     * Handles exceptions and logs errors.
     */
    public void disable() {
        try {
            if (subModules != null) {
                for (Module subModule : subModules) {
                    subModule.disable();
                }
            }
            onDisable();
            listeners.forEach(EventListener::unregister);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to disable module: " + getKey() + ". Error: " + e.getMessage());
        } finally {
            enabled = false;
        }
    }

    /**
     * Returns a unique {@link NamespacedKey} for this module, using the plugin and class name.
     * @return the namespaced key for this module
     */
    public NamespacedKey getKey() {
        return new NamespacedKey(plugin, this.getClass().getSimpleName().toLowerCase());
    }

    /**
     * Called when the module is enabled. Implement custom enable logic here.
     */
    public abstract void onEnable();

    /**
     * Called when the module is disabled. Implement custom disable logic here.
     */
    public abstract void onDisable();

    public void addConfig(ConfigManager configManager) {
        if (configManagers == null) {
            configManagers = new java.util.ArrayList<>();
        }
        configManagers.add(configManager);
    }

    public <T extends Event> void addListener(EventListener<T> listener) {
        if (listeners == null) {
            listeners = new java.util.ArrayList<>();
        }
        listeners.add(listener);
    }

    public <T extends Event> void addListener(Class<T> eventClass, Consumer<T> handler) {
        if (listeners == null) {
            listeners = new java.util.ArrayList<>();
        }
        addListener(new EventListener<>(plugin, eventClass, handler));
    }

    public void addSubModule(Module module) {
        if (subModules == null) {
            subModules = new java.util.ArrayList<>();
        }
        if (subModules.stream().anyMatch(m -> m.getKey().equals(module.getKey()))) {
            throw new DuplicateModuleRegistration(module);
        } else {
            subModules.add(module);
            plugin.getLogger().info("Registered submodule: " + module.getKey());
        }
    }

    public void reloadConfigs() {
        if (configManagers != null) {
            for (ConfigManager configManager : configManagers) {
                configManager.reload();
            }
        }
    }

    public List<ConfigManager> getConfigManagers() {
        return configManagers;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public static Module getInstance() {
        return instance;
    }

}
