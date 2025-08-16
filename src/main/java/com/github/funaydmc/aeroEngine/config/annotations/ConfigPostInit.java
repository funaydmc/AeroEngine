package com.github.funaydmc.aeroEngine.config.annotations;

import java.lang.annotation.*;

/**
 * Indicates that a method should be run when an object is deserialized from config
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ConfigPostInit {
}
