package org.swdc.note.core.nitire;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import org.dizitart.no2.collection.NitriteId;

import java.io.IOException;

/**
 * @since 4.0
 * @author Anindya Chatterjee
 */
class NitriteIdDeserializer extends StdScalarDeserializer<NitriteId> {

    NitriteIdDeserializer() {
        super(NitriteId.class);
    }

    @Override
    public NitriteId deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return NitriteId.createId(p.getValueAsString());
    }
}
