package com.brahvim.agc.server.back;

import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayDeque;
import java.util.ArrayList;

import com.brahvim.agc.server.ExitCode;
import com.brahvim.agc.server.front.App;

// No, I'm not making this `AutoCloseable`! -v-
public final class Client {

	// region Fields.
	private static final ArrayList<DatagramSocket> LIST_SOCKETS_UDP = new ArrayList<>(1);
	private static final ArrayList<Socket> LIST_SOCKETS_SSL = new ArrayList<>(1);
	private static final ArrayList<Thread> LIST_THREADS_UDP = new ArrayList<>(1);
	private static final ArrayList<String> LIST_UI_ENTRIES = new ArrayList<>(1);
	private static final ArrayList<Integer> LIST_ACTIVE = new ArrayList<>(1);

	private static final ArrayDeque<Integer> QUEUE_FREE_SOCKETS_SSL = new ArrayDeque<>(2);
	private static final ArrayDeque<Integer> QUEUE_FREE_UI_ENTRIES = new ArrayDeque<>(2);
	private static final ArrayDeque<Integer> QUEUE_FREE_CLIENTS = new ArrayDeque<>(2);

	private static int countSocketsSsl = 1;
	private static int countUiEntries = 1;
	private static int countClient = 1;

	private int idSocketSsl = 0;
	private int idUiEntry = 0;
	private int idClient = 0;
	// endregion

	static {
		// `0` shall be `null`. ...The `null`-object pattern! Yay!...
		Client.QUEUE_FREE_CLIENTS.add(1);
		Client.QUEUE_FREE_UI_ENTRIES.add(1);
		Client.QUEUE_FREE_SOCKETS_SSL.add(1);

		Client.LIST_UI_ENTRIES.add("");
		Client.LIST_SOCKETS_SSL.add(new Socket());

		try {
			final DatagramSocket closedSocket = new DatagramSocket(0);
			closedSocket.close();
			Client.LIST_SOCKETS_UDP.add(closedSocket);
		} catch (final SocketException e) {
			App.exit(ExitCode.UNKNOWN);
		}

		Client.LIST_THREADS_UDP.add(Thread.currentThread());
	}

	public Client() {
		this.idClient = Client.createClient();
		Client.LIST_ACTIVE.add(this.idClient);
	}

	// region Getters.
	public synchronized String getUiEntry() {
		return Client.LIST_UI_ENTRIES.get(this.idClient);
	}

	public synchronized Thread getThreadUdp() {
		return Client.LIST_THREADS_UDP.get(this.idClient);
	}

	public synchronized Socket getSocketSsl() {
		return Client.LIST_SOCKETS_SSL.get(this.idClient);
	}

	public synchronized DatagramSocket getSocketUdp() {
		return Client.LIST_SOCKETS_UDP.get(this.idClient);
	}
	// endregion

	// region Setters.
	public String setUiEntry(final String p_entry) {
		if (this.idUiEntry == 0)
			this.createUiEntry();

		return Client.LIST_UI_ENTRIES.set(this.idUiEntry, p_entry);
	}

	public synchronized Thread setThreadUdp(final Thread p_thread) {
		return Client.LIST_THREADS_UDP.set(this.idClient, p_thread);
	}

	public synchronized Socket setSocketSsl(final Socket p_sslSocket) {
		if (this.idSocketSsl == 0)
			this.createSocketSsl();

		return Client.LIST_SOCKETS_SSL.set(this.idSocketSsl, p_sslSocket);
	}

	public synchronized DatagramSocket setSocketUdp(final DatagramSocket p_udpSocket) {
		return Client.LIST_SOCKETS_UDP.set(this.idClient, p_udpSocket);
	}
	// endregion

	// region Lifecycle.
	public synchronized void destroy() {
		// Perhaps these may get collected by the GC!...
		Client.LIST_SOCKETS_UDP.set(this.idClient, null);
		Client.LIST_THREADS_UDP.set(this.idClient, null);

		Client.LIST_ACTIVE.remove(this.idClient);

		Client.LIST_UI_ENTRIES.set(this.idUiEntry, null);
		Client.LIST_SOCKETS_SSL.set(this.idSocketSsl, null);

		Client.QUEUE_FREE_CLIENTS.add(this.idClient);
		Client.QUEUE_FREE_UI_ENTRIES.add(this.idUiEntry);
		Client.QUEUE_FREE_SOCKETS_SSL.add(this.idSocketSsl);

		// ...We're `null` now...:
		this.idClient = 0;
		this.idUiEntry = 0;
		this.idSocketSsl = 0;
	}

	private static synchronized int createClient() {
		Integer id = Client.QUEUE_FREE_CLIENTS.poll();

		if (id == null)
			id = ++Client.countClient;

		App.ensureArrayListSize(Client.LIST_SOCKETS_UDP, id);
		App.ensureArrayListSize(Client.LIST_THREADS_UDP, id);

		return id;
	}

	private void createSocketSsl() {
		Integer id = Client.QUEUE_FREE_SOCKETS_SSL.poll();

		if (id == null)
			id = ++Client.countSocketsSsl;

		App.ensureArrayListSize(Client.LIST_SOCKETS_SSL, id);
		this.idSocketSsl = id;
	}

	private void createUiEntry() {
		Integer id = Client.QUEUE_FREE_UI_ENTRIES.poll();

		if (id == null)
			id = ++Client.countUiEntries;

		App.ensureArrayListSize(Client.LIST_UI_ENTRIES, id);
		this.idUiEntry = id;
	}
	// endregion

}
