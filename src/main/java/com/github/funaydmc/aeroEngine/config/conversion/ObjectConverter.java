package com.github.funaydmc.aeroEngine.config.conversion;

import com.github.funaydmc.aeroEngine.config.ConfigField;
import com.github.funaydmc.aeroEngine.config.ConfigType;
import com.github.funaydmc.aeroEngine.config.ConversionManager;
import com.github.funaydmc.aeroEngine.config.data.DataHolder;
import com.github.funaydmc.aeroEngine.config.instantiation.FieldSummary;
import com.github.funaydmc.aeroEngine.config.instantiation.Instantiator;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * A converter which builds objects from configuration sections
 *
 * @author Redempt
 */
public class ObjectConverter {

    /**
     * Creates an object converter
     *
     * @param manager The ConversionManager handling converters
     * @param type    The config type to convert
     * @param <T>     The type
     * @return An object converter for the given type
     */
    public static <T> TypeConverter<T> create(ConversionManager manager, ConfigType<?> type) {
        if (type.getType().isInterface() || Modifier.isAbstract(type.getType().getModifiers())) {
            throw new IllegalStateException("Cannot automatically convert abstract classe or interface " + type.getType());
        }
        FieldSummary summary = FieldSummary.getFieldSummary(manager, type.getType(), false);
        Instantiator instantiator = Instantiator.getInstantiator(type.getType());
        return new TypeConverter<T>() {
            @Override
            public T loadFrom(DataHolder section, String path, T currentValue) {
                DataHolder newSection = path == null ? section : section.getSubsection(path);
                List<Object> objs = new ArrayList<>(summary.getFields().size());
                for (ConfigField field : summary.getFields()) {
                    Object value = summary.getConverters().get(field).loadFrom(newSection, field.getName(), null);
                    objs.add(value);
                }
                return (T) instantiator.instantiate(manager, currentValue, type.getType(), objs, path, summary);
            }

            @Override
            public void saveTo(T t, DataHolder section, String path) {
                saveTo(t, section, path, true);
            }

            @Override
            public void saveTo(T t, DataHolder section, String path, boolean overwrite) {
                if (path != null && section.isSet(path) && !overwrite) {
                    return;
                }
                DataHolder newSection = path == null ? section : section.createSubsection(path);
                if (t == null) {
                    return;
                }
                for (ConfigField field : summary.getFields()) {
                    saveWith(summary.getConverters().get(field), field.get(t), newSection, field.getName(), overwrite);
                }
                summary.applyComments(newSection);
            }
        };
    }

    private static <T> void saveWith(TypeConverter<T> converter, Object obj, DataHolder section, String path, boolean overwrite) {
        converter.saveTo((T) obj, section, path, overwrite);
    }

}
