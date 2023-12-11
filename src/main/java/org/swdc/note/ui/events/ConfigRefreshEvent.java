package org.swdc.note.ui.events;

import org.swdc.config.AbstractConfig;
import org.swdc.dependency.event.AbstractEvent;

public class ConfigRefreshEvent extends AbstractEvent {

    public ConfigRefreshEvent(AbstractConfig config) {
        super(config);
    }

}
