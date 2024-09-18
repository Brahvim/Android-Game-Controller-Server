package com.brahvim.agc.server.back;

import java.util.ArrayDeque;

import com.brahvim.agc.server.front.App;

public class Profile {

	private static final ArrayDeque<Integer> queueFreeIndices = new ArrayDeque<>(1);

	private static int count = 1;

	private final Integer id;

	static {
		// `0` shall be `null`. ...The `null`-object pattern. <Sigh>.
		Profile.queueFreeIndices.add(1);

	}

	public Profile() {
		this.id = Profile.createProfile();
	}

	public synchronized void destroy() {
		Profile.queueFreeIndices.add(this.id);
	}

	private static synchronized Integer createProfile() {
		// synchronized (Client.waitForOtherCreateOrDestroy()) {
		Integer id = Profile.queueFreeIndices.poll();

		if (id == null)
			id = ++Profile.count;

		// Client.idToSoaIndexMap.put(myId, soaIndex);
		App.ensureArrayListSize(Client.listSocksUdp, id);
		App.ensureArrayListSize(Client.listThreadsUdp, id);

		return id;
	}

}
