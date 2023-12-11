module noteEditor {

    requires swdc.application.fx;
    requires swdc.application.dependency;
    requires swdc.application.configs;
    requires swdc.application.data;
    requires jakarta.inject;
    requires jakarta.annotation;

    requires org.slf4j;

    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.swing;

    requires java.persistence;
    requires org.hibernate.orm.core;
    requires org.apache.tika.core;
    requires remark;
    requires nitrite;
    requires je;
    requires undofx;
    requires reactfx;
    requires java.sql;

    requires org.fxmisc.richtext;

    // require that access loggers
    requires flowless;
    requires flexmark.util.data;
    requires flexmark.profile.pegdown;
    requires flexmark.util.misc;
    requires flexmark;
    requires freemarker;
    requires jlatexmath;
    requires java.desktop;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;
    requires org.jsoup;
    requires jnativehook;
    requires java.logging;
    requires org.controlsfx.controls;
    requires epublib.core;
    requires lucene.core;
    requires ik.analyzer;

    opens org.swdc.note.core to
            swdc.application.dependency;

    opens org.swdc.note.core.aspect to
            swdc.application.dependency;

    opens org.swdc.note.core.files to
            swdc.application.dependency;

    opens org.swdc.note.core.files.factory to
            swdc.application.dependency;

    opens org.swdc.note.core.files.single to
            com.fasterxml.jackson.databind,
            swdc.application.dependency;

    opens org.swdc.note.core.files.storages to
            com.fasterxml.jackson.databind,
            swdc.application.dependency;

    opens org.swdc.note to
            swdc.application.dependency,
            swdc.application.fx,
            javafx.graphics;

    opens org.swdc.note.core.proto to
            swdc.application.dependency;

    opens org.swdc.note.core.entities to
            swdc.application.data,
            com.fasterxml.jackson.databind,
            nitrite,
            org.hibernate.orm.core;

    opens org.swdc.note.ui.controllers to
            swdc.application.dependency,
            swdc.application.fx,
            javafx.fxml;

    opens org.swdc.note.ui.controllers.dialogs to
            swdc.application.fx,
            swdc.application.dependency,
            javafx.fxml;

    opens org.swdc.note.ui.component to
            swdc.application.fx,
            swdc.application.dependency;

    opens org.swdc.note.core.render to
            swdc.application.dependency;

    opens org.swdc.note.core.service to
            swdc.application.dependency,
            swdc.application.fx;

    exports org.swdc.note.core.service;
    exports org.swdc.note.core.entities;

    opens org.swdc.note.config to
            org.controlsfx.controls,
            swdc.application.dependency,
            swdc.application.configs,
            swdc.application.fx;

    opens org.swdc.note.core.repo to
            swdc.application.data,
            swdc.application.dependency;

    opens org.swdc.note.ui.view to
            swdc.application.dependency,
            swdc.application.fx,
            javafx.controls,
            javafx.graphics;

    opens org.swdc.note.ui.view.cells to
            swdc.application.dependency,
            swdc.application.fx,
            com.fasterxml.jackson.databind,
            javafx.controls,
            javafx.graphics;

    opens org.swdc.note.ui.view.dialogs to
            swdc.application.dependency,
            swdc.application.fx,
            javafx.controls,
            javafx.graphics;

    opens views.main to
            swdc.application.dependency,
            swdc.application.fx,
            javafx.graphics;

    opens icons to
            swdc.application.dependency,
            swdc.application.fx;

}