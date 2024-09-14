package com.brahvim.agc.server.back;

import java.net.DatagramSocket;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.IdentityHashMap;

// No, I'm not making this `AutoCloseable`! -v-
public final class Client {

	// region Fields.
	private static final ArrayList<Thread> listThreadsUdp = new ArrayList<>();
	private static final ArrayList<DatagramSocket> listSocksUdp = new ArrayList<>();
	private static final IdentityHashMap<Integer, Socket> mapSocksSsl = new IdentityHashMap<>();

	private static final ArrayDeque<Integer> listFreeIndices = new ArrayDeque<>();
	// private static final AtomicBoolean inCreateOrDestroy = new AtomicBoolean();
	// endregion

	private final Integer id;

	static {
		// `0` shall be `null`. ...The `null`-object pattern. <Sigh>.
		Client.listFreeIndices.add(1);

		Client.listSocksUdp.add(null);
		Client.mapSocksSsl.put(0, null);
		Client.listThreadsUdp.add(null);
	}

	public Client() {
		this.id = Client.createClient();
	}

	public synchronized void destroy() {
		// synchronized (Client.waitForOtherCreateOrDestroy()) {
		// `Map`s:
		Client.mapSocksSsl.remove(this.id);
		// Client.idToSoaIndexMap.remove(index);

		// `List`s:
		Client.listSocksUdp.set(this.id, null);
		Client.listThreadsUdp.set(this.id, null);

		Client.listFreeIndices.add(this.id);
		// Client.endCurrentCreateOrDestroy();
		// }
	}

	// region Getters.
	public Socket getSslSocket() {
		return Client.mapSocksSsl.get(this.id);
	}

	public Thread getUdpSocketThread() {
		return Client.listThreadsUdp.get(this.id);
	}

	public DatagramSocket getUdpSocket() {
		return Client.listSocksUdp.get(this.id);
	}
	// endregion

	// region Setters.
	public synchronized Socket setSslSocket(final Socket p_sslSocket) {
		return Client.mapSocksSsl.put(this.id, p_sslSocket);
	}

	public synchronized Thread setUdpSocketThread(final Thread p_thread) {
		return Client.listThreadsUdp.set(this.id, p_thread);
	}

	public synchronized DatagramSocket setUdpSocket(final DatagramSocket p_udpSocket) {
		return Client.listSocksUdp.set(this.id, p_udpSocket);
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

	// private static AtomicBoolean waitForOtherCreateOrDestroy() {
	// synchronized (Client.inCreateOrDestroy) {
	// while (Client.inCreateOrDestroy.get())
	// try {
	// Client.inCreateOrDestroy.wait();
	// } catch (final InterruptedException e) {
	// Thread.currentThread().interrupt();
	// }
	// return Client.inCreateOrDestroy;
	// }
	// }
	//
	// private static AtomicBoolean endCurrentCreateOrDestroy() {
	// synchronized (Client.inCreateOrDestroy) {
	// Client.inCreateOrDestroy.set(false);
	// Client.inCreateOrDestroy.notifyAll();
	// return Client.inCreateOrDestroy;
	// }
	// }

	private static synchronized Integer createClient() {
		// synchronized (Client.waitForOtherCreateOrDestroy()) {
		final Integer soaIndex = Client.listSocksUdp.size();
		Integer id = Client.listFreeIndices.poll();

		if (id == null)
			id = soaIndex;

		// Client.idToSoaIndexMap.put(myId, soaIndex);
		Client.ensureArrayListSize(Client.listSocksUdp, soaIndex);
		Client.ensureArrayListSize(Client.listThreadsUdp, soaIndex);

		return id;
	}

}
