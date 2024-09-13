package com.brahvim.agc.server.back;

import java.net.DatagramSocket;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.IdentityHashMap;

// No, I'm not making this `AutoCloseable`! -v-
public final class Client {

	// region Fields.
	private static final ArrayList<Thread> udpSockThreads = new ArrayList<>();
	private static final ArrayList<DatagramSocket> socksUdp = new ArrayList<>();
	private static final IdentityHashMap<Integer, Socket> socksSsl = new IdentityHashMap<>();

	private static final ArrayDeque<Integer> freeIndices = new ArrayDeque<>();
	// private static final AtomicBoolean inCreateOrDestroy = new AtomicBoolean();
	// endregion

	private final Integer id;

	static {
		// `0` shall be `null`. ...The `null`-object pattern. <Sigh>.
		Client.freeIndices.add(1);

		Client.socksUdp.add(null);
		Client.socksSsl.put(0, null);
		Client.udpSockThreads.add(null);
	}

	public Client() {
		this.id = Client.createClient();
	}

	public synchronized void destroy() {
		// synchronized (Client.waitForOtherCreateOrDestroy()) {
		// `Map`s:
		Client.socksSsl.remove(this.id);
		// Client.idToSoaIndexMap.remove(index);

		// `List`s:
		Client.socksUdp.set(this.id, null);
		Client.udpSockThreads.set(this.id, null);

		Client.freeIndices.add(this.id);
		// Client.endCurrentCreateOrDestroy();
		// }
	}

	// region Getters.
	public Socket getSslSocket() {
		return Client.socksSsl.get(this.id);
	}

	public Thread getUdpSocketThread() {
		return Client.udpSockThreads.get(this.id);
	}

	public DatagramSocket getUdpSocket() {
		return Client.socksUdp.get(this.id);
	}
	// endregion

	// region Setters.
	public synchronized Socket setSslSocket(final Socket p_sslSocket) {
		return Client.socksSsl.put(this.id, p_sslSocket);
	}

	public synchronized Thread setUdpSocketThread(final Thread p_thread) {
		return Client.udpSockThreads.set(this.id, p_thread);
	}

	public synchronized DatagramSocket setUdpSocket(final DatagramSocket p_udpSocket) {
		return Client.socksUdp.set(this.id, p_udpSocket);
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
		final Integer soaIndex = Client.socksUdp.size();
		Integer id = Client.freeIndices.poll();

		if (id == null)
			id = soaIndex;

		// Client.idToSoaIndexMap.put(myId, soaIndex);
		Client.ensureArrayListSize(Client.socksUdp, soaIndex);
		Client.ensureArrayListSize(Client.udpSockThreads, soaIndex);

		return id;
	}

}
