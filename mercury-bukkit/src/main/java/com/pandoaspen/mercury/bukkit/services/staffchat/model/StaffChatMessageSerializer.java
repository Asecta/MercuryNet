package com.pandoaspen.mercury.bukkit.services.staffchat.model;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.pandoaspen.mercury.common.serializer.MercurySerializer;

import java.io.IOException;
import java.util.UUID;

public class StaffChatMessageSerializer extends MercurySerializer<StaffChatMessage> {

    @Override
    public Class<StaffChatMessage> getTargetClass() {
        return StaffChatMessage.class;
    }

    @Override
    public void write(ObjectDataOutput out, StaffChatMessage object) throws IOException {
        out.writeString(object.getServerId());
        writeUUID(out, object.getPlayerUUID());
        out.writeString(object.getPlayerName());
        out.writeString(object.getMessage());
    }

    @Override
    public StaffChatMessage read(ObjectDataInput in) throws IOException {
        String serverId = in.readString();
        UUID playerUUID = readUUID(in);
        String playerName = in.readString();
        String message = in.readString();
        return new StaffChatMessage(serverId, playerUUID, playerName, message);
    }

    @Override
    public int getTypeId() {
        return 1;
    }
}
