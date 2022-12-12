package com.pandoaspen.mercury.common.serializer;

import com.google.gson.Gson;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import lombok.Getter;

import java.io.IOException;

public class HzGsonSerializer<T> extends MercurySerializer<T> {

    private static final Gson DEFAULT_GSON = new Gson();

    @Getter private final int typeId;
    @Getter private final Class<T> targetClass;

    private Gson gson;

    public HzGsonSerializer(int typeId, Class<T> clazz) {
        this.typeId = typeId;
        this.targetClass = clazz;
        this.gson = DEFAULT_GSON;
    }

    public HzGsonSerializer(int typeId, Class<T> clazz, Gson gson) {
        this.typeId = typeId;
        this.targetClass = clazz;
        this.gson = gson;
    }

    @Override
    public void write(ObjectDataOutput out, T object) throws IOException {
        out.writeString(gson.toJson(object));
    }

    @Override
    public T read(ObjectDataInput in) throws IOException {
        return gson.fromJson(in.readString(), targetClass);
    }
}
