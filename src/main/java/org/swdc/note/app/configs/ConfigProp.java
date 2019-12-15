package org.swdc.note.app.configs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by lenovo on 2019/6/8.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigProp {

    PropType type();

    String name();

    String value();

    String tooltip();

    String propName();

    Class<? extends Importer> importer() default Importer.class;

}
