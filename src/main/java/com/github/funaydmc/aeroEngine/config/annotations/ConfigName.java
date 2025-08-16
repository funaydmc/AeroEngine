package com.github.funaydmc.aeroEngine.config.annotations;

import java.lang.annotation.*;

/**
 * Specifies the name that should be used to access and set a value in config
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ConfigName {

    String value();

}
