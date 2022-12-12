package com.pandoaspen.mercury.common.serializer;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;

import java.io.IOException;
import java.util.UUID;

public abstract class MercurySerializer<T> implements StreamSerializer<T> {


    public abstract Class<T> getTargetClass();

    public static void writeUUID(final ObjectDataOutput out, final UUID uuid) throws IOException {
        out.writeLong(uuid.getMostSignificantBits());
        out.writeLong(uuid.getLeastSignificantBits());
    }

    public static UUID readUUID(final ObjectDataInput in) throws IOException {
        return new UUID(in.readLong(), in.readLong());
    }
}
