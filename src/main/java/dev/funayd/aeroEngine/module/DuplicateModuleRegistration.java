package dev.funayd.aeroEngine.module;

public class DuplicateModuleRegistration extends RuntimeException {
    public DuplicateModuleRegistration(Module module) {
        super("Duplicate module registration: " + module.getKey().toString());
    }
}
