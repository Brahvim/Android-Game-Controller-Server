package com.brahvim.agc.server.back;

import java.net.DatagramSocket;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.IdentityHashMap;

import com.brahvim.agc.server.front.App;

// No, I'm not making this `AutoCloseable`! -v-
public final class Client {

	// region Fields.
	private static final ArrayList<Thread> LIST_THREADS_UDP = new ArrayList<>(1);
	private static final ArrayList<DatagramSocket> LIST_SOCKS_UDP = new ArrayList<>(1);
	private static final IdentityHashMap<Integer, String> MAP_UI_ENTRY = new IdentityHashMap<>(1);
	private static final IdentityHashMap<Integer, Socket> MAP_SOCKS_SSL = new IdentityHashMap<>(1);
	private static final ArrayDeque<Integer> QUEUE_FREE_INDICES = new ArrayDeque<>(1);

	private static int count = 1;
	// private static final AtomicBoolean inCreateOrDestroy = new AtomicBoolean();
	// endregion

	private final Integer id;

	static {
		// `0` shall be `null`. ...The `null`-object pattern. <Sigh>.
		Client.QUEUE_FREE_INDICES.add(1);

		Client.MAP_UI_ENTRY.put(0, null);
		Client.MAP_SOCKS_SSL.put(0, null);

		Client.LIST_THREADS_UDP.add(null);
		Client.LIST_SOCKS_UDP.add(null);
	}

	public Client() {
		this.id = Client.create();
	}

	public synchronized void destroy() {
		// synchronized (Client.waitForOtherCreateOrDestroy()) {
		// `Map`s:
		Client.MAP_UI_ENTRY.remove(this.id);
		Client.MAP_SOCKS_SSL.remove(this.id);
		// Client.idToSoaIndexMap.remove(index);

		// `List`s:
		Client.LIST_SOCKS_UDP.set(this.id, null);
		Client.LIST_THREADS_UDP.set(this.id, null);

		Client.QUEUE_FREE_INDICES.add(this.id);
		// Client.endCurrentCreateOrDestroy();
		// }
	}

	// region Getters.
	public static int getCount() {
		return Client.count;
	}

	public String getUiEntry() {
		return Client.MAP_UI_ENTRY.get(this.id);
	}

	public Socket getSslSocket() {
		return Client.MAP_SOCKS_SSL.get(this.id);
	}

	public Thread getUdpSocketThread() {
		return Client.LIST_THREADS_UDP.get(this.id);
	}

	public DatagramSocket getUdpSocket() {
		return Client.LIST_SOCKS_UDP.get(this.id);
	}
	// endregion

	// region Setters.
	public String setUiEntry(final String p_entry) {
		return Client.MAP_UI_ENTRY.put(this.id, p_entry);
	}

	public synchronized Socket setSslSocket(final Socket p_sslSocket) {
		return Client.MAP_SOCKS_SSL.put(this.id, p_sslSocket);
	}

	public synchronized Thread setUdpSocketThread(final Thread p_thread) {
		return Client.LIST_THREADS_UDP.set(this.id, p_thread);
	}

	public synchronized DatagramSocket setUdpSocket(final DatagramSocket p_udpSocket) {
		return Client.LIST_SOCKS_UDP.set(this.id, p_udpSocket);
	}
	// endregion

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

	private static synchronized Integer create() {
		// synchronized (Client.waitForOtherCreateOrDestroy()) {
		Integer id = Client.QUEUE_FREE_INDICES.poll();

		if (id == null)
			id = ++Client.count;

		// Client.idToSoaIndexMap.put(myId, soaIndex);
		App.ensureArrayListSize(Client.LIST_SOCKS_UDP, id);
		App.ensureArrayListSize(Client.LIST_THREADS_UDP, id);

		return id;
	}

}
