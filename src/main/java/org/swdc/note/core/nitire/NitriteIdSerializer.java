package org.swdc.note.core.nitire;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import org.dizitart.no2.collection.NitriteId;

import java.io.IOException;

/**
 * @since 4.0
 * @author Anindya Chatterjee
 */
class NitriteIdSerializer extends StdScalarSerializer<NitriteId> {

    protected NitriteIdSerializer() {
        super(NitriteId.class);
    }

    @Override
    public void serialize(NitriteId value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (value.getIdValue() != null) {
            gen.writeString(value.getIdValue());
        }
    }
}
