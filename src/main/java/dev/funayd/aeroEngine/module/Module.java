package dev.funayd.aeroEngine.module;

import dev.funayd.aeroEngine.config.ConfigManager;
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

    public NamespacedKey getKey() {
        return new NamespacedKey(plugin, this.getClass().getSimpleName().toLowerCase());
    }

    public abstract void onEnable();
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
