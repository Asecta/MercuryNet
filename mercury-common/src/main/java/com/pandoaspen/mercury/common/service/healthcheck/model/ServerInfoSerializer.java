//package com.pandoaspen.mercury.bukkit.service.healthcheck.model;
//
//import com.hazelcast.nio.ObjectDataInput;
//import com.hazelcast.nio.ObjectDataOutput;
//import com.pandoaspen.mercury.bukkit.serializer.MercurySerializer;
//
//import java.io.IOException;
//
//
//public class ServerInfoSerializer extends MercurySerializer<ServerInfo> {
//    @Override
//    public void write(ObjectDataOutput out, ServerInfo object) throws IOException {
//        out.writeString(object.getServerId());
//        writeUUID(out, object.getDistributionId());
//        out.writeLong(object.getStartTime());
//        out.writeDouble(object.getTps());
//        out.writeBoolean(object.isWhitelisted());
//        out.writeLong(object.getSystemTime());
//        out.writeByte(object.getServerStatus().ordinal());
//
//        out.writeShort(object.getOnlinePlayers().size());
//
//        for (PlayerInfo onlinePlayer : object.getOnlinePlayers()) {
//            writeUUID(out, onlinePlayer.getUuid());
//            out.writeString(onlinePlayer.getName());
//        }
//
//    }
//
//    @Override
//    public ServerInfo read(ObjectDataInput in) throws IOException {
//        ServerInfo serverInfo = new ServerInfo(in.readString(), readUUID(in));
//        serverInfo.setStartTime(in.readLong());
//        serverInfo.setTps(in.readDouble());
//        serverInfo.setWhitelisted(in.readBoolean());
//        serverInfo.setStartTime(in.readLong());
//        serverInfo.setServerStatus(ServerStatus.values()[in.readByte()]);
//
//
//        for (int i = 0; i < in.readShort(); i++) {
//            serverInfo.getOnlinePlayers().add(new PlayerInfo(readUUID(in), in.readString()));
//        }
//
//
//        return serverInfo;
//    }
//
//    @Override
//    public Class<ServerInfo> getTargetClass() {
//        return ServerInfo.class;
//    }
//
//    @Override
//    public int getTypeId() {
//        return 2;
//    }
//}
