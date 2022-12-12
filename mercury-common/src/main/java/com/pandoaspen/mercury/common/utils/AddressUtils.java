package com.pandoaspen.mercury.common.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

public class AddressUtils {
    public static InetAddress findLocalNetwork() {
        try {
            Set<InetAddress> addresses = listAddresses();

            InetAddress address = addresses.stream()
                    .filter(a -> a.getHostAddress().startsWith("10.0.255"))
                    .findAny()
                    .orElse(null);
            if (address == null) {
                address = InetAddress.getLocalHost();
            }

            System.out.println(address);

            return address;
        } catch (Exception e) {
            return null;
        }
    }

    public static Set<InetAddress> listAddresses() throws SocketException {
        Set<InetAddress> addresses = new HashSet<>();
        Enumeration<NetworkInterface> interfaceEnumeration = NetworkInterface.getNetworkInterfaces();
        while (interfaceEnumeration.hasMoreElements()) {
            NetworkInterface networkInterface = interfaceEnumeration.nextElement();
            Enumeration<InetAddress> addressEnumeration = networkInterface.getInetAddresses();

            while (addressEnumeration.hasMoreElements()) {
                InetAddress inetAddress = addressEnumeration.nextElement();
                if (inetAddress.getAddress().length != 4) continue;
                addresses.add(inetAddress);
            }
        }
        return addresses;
    }
}
