package com.pandoaspen.mercury.bukkit.utils;

import io.netty.channel.ChannelFuture;
import org.bukkit.Server;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.util.List;

public class NettyUtils {


    private static List<ChannelFuture> findChannelList(Object object) throws IllegalAccessException {
        for (Field declaredField : object.getClass().getDeclaredFields()) {
            Type genType = declaredField.getGenericType();
            if (genType == null || !(genType instanceof ParameterizedType)) continue;
            ParameterizedType pType = (ParameterizedType) genType;
            if (pType.getActualTypeArguments() == null || pType.getActualTypeArguments().length != 1) continue;
            Type foundType = pType.getActualTypeArguments()[0];
            if (!foundType.toString().equals("interface io.netty.channel.ChannelFuture")) continue;
            declaredField.setAccessible(true);
            return (List<ChannelFuture>) declaredField.get(object);
        }
        return null;
    }


    public static InetSocketAddress findMinecraftPort(Server server) throws Exception {
        Method serverGetHandle = server.getClass().getDeclaredMethod("getServer");
        Object minecraftServer = serverGetHandle.invoke(server);
        // Get Server Connection
        Method serverConnectionMethod = null;
        for (Method method : minecraftServer.getClass().getSuperclass().getDeclaredMethods()) {
            if (!method.getReturnType().getSimpleName().equals("ServerConnection")) {
                continue;
            }
            serverConnectionMethod = method;
            break;
        }
        Object serverConnection = serverConnectionMethod.invoke(minecraftServer);


        List<ChannelFuture> channelFutureList = findChannelList(serverConnection);
        if (channelFutureList == null) {
            throw new Exception("Unable to determine server port!");
        }

        // Iterate ChannelFutures
        InetSocketAddress address = null;
        for (ChannelFuture channelFuture : channelFutureList) {
            InetSocketAddress newAddress = ((InetSocketAddress) channelFuture.channel().localAddress());
            if (newAddress != null) address = newAddress;
        }
        return address;
    }


}
