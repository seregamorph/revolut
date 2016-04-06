package com.revolut.sinap.netty;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

class InnerUtils {
    private InnerUtils() {
    }

    static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    static void checkState(boolean state, String msg) {
        if (!state) {
            throw new IllegalStateException(msg);
        }
    }

    static String getHostAddress(SocketAddress socketAddress) {
        if (socketAddress instanceof InetSocketAddress) {
            InetSocketAddress isa = (InetSocketAddress) socketAddress;
            return isa.getAddress().getHostAddress();
        }
        return String.valueOf(socketAddress);
    }
}
