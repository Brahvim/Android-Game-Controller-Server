package com.brahvim.agc.server.back;

import java.net.DatagramSocket;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client {

	// region Fields.
	private static final ArrayList<Thread> udpSockThreads = new ArrayList<>();

	private static final ArrayList<DatagramSocket> socksUdp = new ArrayList<>();
	private static final IdentityHashMap<Integer, Socket> socksSsl = new IdentityHashMap<>();

	private static final ArrayDeque<Integer> freeIndices = new ArrayDeque<>();
	private static final IdentityHashMap<Integer, Integer> idToIndexMap = new IdentityHashMap<>();

	private static final AtomicBoolean inCreateOrDestroy = new AtomicBoolean();
	// endregion

	static {
		Client.freeIndices.add(0);
	}

	private Client() {
		throw new IllegalAccessError();
	}

	public static synchronized int createClient() {
		final var id = Client.freeIndices.poll();

		final int minSize = Client.idToIndexMap.size();
		Client.idToIndexMap.put(id, minSize);
		Client.udpSockThreads.ensureCapacity(minSize);
		Client.socksUdp.ensureCapacity(minSize);

		Client.inCreateOrDestroy.set(false);
		return id;
	}

	public static synchronized void destroyClient(final int p_clientId) {
		final var index = Client.mapIdToIndex(p_clientId);

		// `Map`s:
		Client.socksSsl.remove(index);
		Client.idToIndexMap.remove(index);

		// `List`s:
		Client.socksUdp.set(index, null);
		Client.udpSockThreads.set(index, null);

		Client.freeIndices.add(p_clientId);
	}

	// region Getters.
	public static Socket getSslSocket(final int p_clientId) {
		return Client.socksSsl.get(p_clientId);
	}

	public static Thread getUdpSocketThread(final int p_clientId) {
		return Client.udpSockThreads.get(Client.mapIdToIndex(p_clientId));
	}

	public static DatagramSocket getUdpSocket(final int p_clientId) {
		return Client.socksUdp.get(Client.mapIdToIndex(p_clientId));
	}
	// endregion

	// region Setters.
	public static synchronized void setSslSocket(final int p_clientId, final Socket p_sslSocket) {
		Client.socksSsl.put(p_clientId, p_sslSocket);
	}

	public static synchronized Thread setUdpSocketThread(final int p_clientId, final Thread p_thread) {
		return Client.udpSockThreads.set(Client.mapIdToIndex(p_clientId), p_thread);
	}

	public static synchronized void setUdpSocket(final int p_clientId, final DatagramSocket p_udpSocket) {
		Client.socksUdp.set(p_clientId, p_udpSocket);
	}
	// endregion

	private static int mapIdToIndex(final int p_clientId) {
		return Client.idToIndexMap.get(p_clientId);
	}

}
