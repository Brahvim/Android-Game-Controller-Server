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
	private static final IdentityHashMap<Integer, Integer> idToSoaIndexMap = new IdentityHashMap<>();

	private static final AtomicBoolean inCreateOrDestroy = new AtomicBoolean();
	// endregion

	static {
		Client.freeIndices.add(0);
	}

	private Client() {
		throw new IllegalAccessError();
	}

	public static synchronized Integer createClient() {
		// synchronized (Client.waitForOtherCreateOrDestroy()) {
		final Integer soaIndex = Client.idToSoaIndexMap.size();
		Integer id = Client.freeIndices.poll();

		if (id == null)
			id = soaIndex;

		Client.idToSoaIndexMap.put(id, soaIndex);
		Client.ensureArrayListSize(Client.socksUdp, soaIndex);
		Client.ensureArrayListSize(Client.udpSockThreads, soaIndex);

		// Client.endCurrentCreateOrDestroy();
		return id;
		// }

	}

	public static synchronized void destroyClient(final Integer p_clientId) {
		// synchronized (Client.waitForOtherCreateOrDestroy()) {
		final var index = Client.mapIdToIndex(p_clientId);

		// `Map`s:
		Client.socksSsl.remove(index);
		Client.idToSoaIndexMap.remove(index);

		// `List`s:
		Client.socksUdp.set(index, null);
		Client.udpSockThreads.set(index, null);

		Client.freeIndices.add(p_clientId);
		// Client.endCurrentCreateOrDestroy();
		// }
	}

	// region Getters.
	public static Socket getSslSocket(final Integer p_clientId) {
		return Client.socksSsl.get(p_clientId);
	}

	public static Thread getUdpSocketThread(final Integer p_clientId) {
		return Client.udpSockThreads.get(Client.mapIdToIndex(p_clientId));
	}

	public static DatagramSocket getUdpSocket(final Integer p_clientId) {
		return Client.socksUdp.get(Client.mapIdToIndex(p_clientId));
	}
	// endregion

	// region Setters.
	public static synchronized Socket setSslSocket(final Integer p_clientId, final Socket p_sslSocket) {
		return Client.socksSsl.put(p_clientId, p_sslSocket);
	}

	public static synchronized Thread setUdpSocketThread(final Integer p_clientId, final Thread p_thread) {
		return Client.udpSockThreads.set(Client.mapIdToIndex(p_clientId), p_thread);
	}

	public static synchronized DatagramSocket setUdpSocket(final Integer p_clientId, final DatagramSocket p_udpSocket) {
		return Client.socksUdp.set(p_clientId, p_udpSocket);
	}
	// endregion

	private static void ensureArrayListSize(final ArrayList<?> p_list, final Integer p_minSize) {
		// `Collection::addAll()`? Well, no!
		// Putting my own `Collection` subclass didn't exactly work out, and `List.of()`
		// won't create a `List` with `null`s! (See its use of `ImmutableCollections`!)
		p_list.ensureCapacity(p_minSize);

		while (p_list.size() <= p_minSize)
			p_list.add(null);
	}

	private static AtomicBoolean waitForOtherCreateOrDestroy() {
		synchronized (Client.inCreateOrDestroy) {
			while (Client.inCreateOrDestroy.get())
				try {
					Client.inCreateOrDestroy.wait();
				} catch (final InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			return Client.inCreateOrDestroy;
		}
	}

	private static AtomicBoolean endCurrentCreateOrDestroy() {
		synchronized (Client.inCreateOrDestroy) {
			Client.inCreateOrDestroy.set(false);
			Client.inCreateOrDestroy.notifyAll();
			return Client.inCreateOrDestroy;
		}
	}

	private static Integer mapIdToIndex(final Integer p_clientId) {
		return Client.idToSoaIndexMap.get(p_clientId);
	}

}
