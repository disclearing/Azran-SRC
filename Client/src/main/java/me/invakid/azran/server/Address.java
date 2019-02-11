package me.invakid.azran.server;

public class Address {
	public final String ip;
	public final int port;

	public Address(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	@Override
	public String toString() {
		return ip + ":" + port;
	}
}