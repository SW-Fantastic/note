package org.swdc.note.core.nitire;

import com.fasterxml.jackson.databind.Module;
import org.dizitart.no2.common.module.NitriteModule;
import org.dizitart.no2.common.module.NitritePlugin;

import java.util.Set;

import static org.dizitart.no2.common.util.Iterables.setOf;

public class JacksonMapperModule implements NitriteModule {

    private final JacksonMapper jacksonMapper;

    /**
     * Instantiates a new {@link JacksonMapperModule}.
     */
    public JacksonMapperModule() {
        jacksonMapper = new JacksonMapper();
    }

    /**
     * Instantiates a new {@link JacksonMapperModule} with custom
     * jackson modules.
     *
     * @param jacksonModules the jackson modules
     */
    public JacksonMapperModule(com.fasterxml.jackson.databind.Module... jacksonModules) {
        jacksonMapper = new JacksonMapper();
        for (Module jacksonModule : jacksonModules) {
            jacksonMapper.registerJacksonModule(jacksonModule);
        }
    }

    @Override
    public Set<NitritePlugin> plugins() {
        return setOf(jacksonMapper);
    }

}
