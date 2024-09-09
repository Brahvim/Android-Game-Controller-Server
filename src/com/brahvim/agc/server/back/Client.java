package com.brahvim.agc.server.back;

import java.net.DatagramSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.IdentityHashMap;

public class Client {

	private static ArrayList<Thread> udpSockThreads = new ArrayList<>();

	private static ArrayList<DatagramSocket> socksUdp = new ArrayList<>();
	private static IdentityHashMap<Integer, Socket> socksSsl = new IdentityHashMap<>();

	private static IdentityHashMap<Integer, Integer> idToIndexMap = new IdentityHashMap<>();

	private Client() {
		throw new IllegalAccessError();
	}

	private static int mapIdToIndex(final int p_clientId) {
		return Client.idToIndexMap.get(p_clientId);
	}

	public static Socket getSslSocket(final int p_clientId) {
		return Client.socksSsl.get(p_clientId);
	}

	public static Thread getUdpSocketThread(final int p_clientId) {
		return Client.udpSockThreads.get(Client.mapIdToIndex(p_clientId));
	}

	public static DatagramSocket getUdpSocket(final int p_clientId) {
		return Client.socksUdp.get(Client.mapIdToIndex(p_clientId));
	}

}
