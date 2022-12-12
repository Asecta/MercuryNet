package com.pandoaspen.mercury.common.serializer;

import com.hazelcast.nio.serialization.StreamSerializer;

import java.util.HashMap;
import java.util.Map;

public class SerializerFactory {

    private final Map<Class<? extends MercurySerializer<?>>, MercurySerializer<?>> serializerClassCache;

    public SerializerFactory() {
        this.serializerClassCache = new HashMap<>();
    }

    public <T extends MercurySerializer<?>> T getSerializer(Class<? extends MercurySerializer<?>> serializerClass) {

        return null;
    }

    public <T> MercurySerializer<T> getSerializerFromTarget(T targetClass) {
        for (MercurySerializer<?> other : this.serializerClassCache.values()) {
            if (other.getTargetClass().equals(targetClass)){
                return (MercurySerializer<T>) other;
            }
        }
        return null;
    }


    private boolean checkSerializerId(StreamSerializer<?> serializer) {
        for (StreamSerializer<?> other : this.serializerClassCache.values()) {
            if (other.getTypeId() == serializer.getTypeId()) return true;
        }
        return false;
    }
}
