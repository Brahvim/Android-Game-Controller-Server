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
	/* NOSONAR */ public static final ArrayList<Thread> listThreadsUdp = new ArrayList<>(1);
	/* NOSONAR */ public static final ArrayList<DatagramSocket> listSocksUdp = new ArrayList<>(1);
	/* NOSONAR */ public static final IdentityHashMap<Integer, String> mapUiEntry = new IdentityHashMap<>(1);
	/* NOSONAR */ public static final IdentityHashMap<Integer, Socket> mapSocksSsl = new IdentityHashMap<>(1);

	private static final ArrayDeque<Integer> queueFreeIndices = new ArrayDeque<>(1);

	private static int count = 1;
	// private static final AtomicBoolean inCreateOrDestroy = new AtomicBoolean();
	// endregion

	private final Integer id;

	static {
		// `0` shall be `null`. ...The `null`-object pattern. <Sigh>.
		Client.queueFreeIndices.add(1);

		Client.mapUiEntry.put(0, null);
		Client.mapSocksSsl.put(0, null);

		Client.listThreadsUdp.add(null);
		Client.listSocksUdp.add(null);
	}

	public Client() {
		this.id = Client.createClient();
	}

	public synchronized void destroy() {
		// synchronized (Client.waitForOtherCreateOrDestroy()) {
		// `Map`s:
		Client.mapUiEntry.remove(this.id);
		Client.mapSocksSsl.remove(this.id);
		// Client.idToSoaIndexMap.remove(index);

		// `List`s:
		Client.listSocksUdp.set(this.id, null);
		Client.listThreadsUdp.set(this.id, null);

		Client.queueFreeIndices.add(this.id);
		// Client.endCurrentCreateOrDestroy();
		// }
	}

	// region Getters.
	public static int getCount() {
		return Client.count;
	}

	public String getUiEntry() {
		return Client.mapUiEntry.get(this.id);
	}

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
	public String setUiEntry(final String p_entry) {
		return Client.mapUiEntry.put(this.id, p_entry);
	}

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
		Integer id = Client.queueFreeIndices.poll();

		if (id == null)
			id = ++Client.count;

		// Client.idToSoaIndexMap.put(myId, soaIndex);
		App.ensureArrayListSize(Client.listSocksUdp, id);
		App.ensureArrayListSize(Client.listThreadsUdp, id);

		return id;
	}

}
