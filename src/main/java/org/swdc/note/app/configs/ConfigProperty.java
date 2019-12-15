package org.swdc.note.app.configs;

import lombok.Getter;
import org.controlsfx.property.BeanProperty;

import java.beans.PropertyDescriptor;

/**
 * Created by lenovo on 2019/6/8.
 */
public class ConfigProperty extends BeanProperty {

    @Getter
    private ConfigProp propData;

    public ConfigProperty(Object bean, PropertyDescriptor propertyDescriptor, ConfigProp prop) {
        super(bean, propertyDescriptor);
        propData = prop;
    }

    @Override
    public String getName() {
        return propData.name();
    }

    @Override
    public String getDescription() {
        return propData.tooltip();
    }
}
