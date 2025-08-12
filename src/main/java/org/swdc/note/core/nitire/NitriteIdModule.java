package org.swdc.note.core.nitire;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.dizitart.no2.collection.NitriteId;

/**
 * Class that registers capability of serializing {@link NitriteId} with the Jackson core.
 *
 * @author Anindya Chatterjee
 * @since 1.0.0
 */
public class NitriteIdModule extends SimpleModule {

    @Override
    public void setupModule(SetupContext context) {
        addSerializer(NitriteId.class, new NitriteIdSerializer());
        addDeserializer(NitriteId.class, new NitriteIdDeserializer());
        super.setupModule(context);
    }
}
