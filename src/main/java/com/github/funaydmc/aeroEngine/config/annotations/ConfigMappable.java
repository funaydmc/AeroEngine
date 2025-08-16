package com.github.funaydmc.aeroEngine.config.annotations;

import java.lang.annotation.*;

/**
 * Indicates that a class can be automatically serialized to config
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConfigMappable {
}
