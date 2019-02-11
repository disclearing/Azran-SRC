package me.invakid.azran.server;

public class Address {
    final String ip;
    final int port;

    Address(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public String toString() {
        return ip + ":" + port;
    }
}