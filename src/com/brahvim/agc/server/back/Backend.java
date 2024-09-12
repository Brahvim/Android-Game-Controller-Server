package com.brahvim.agc.server.back;

import java.net.ServerSocket;
import java.util.concurrent.atomic.AtomicInteger;

import com.brahvim.agc.server.event.EventDispatchThread;

public class Backend {

	public static final EventDispatchThread EDT = new EventDispatchThread("BACKEND");
	public static final AtomicInteger INT_CLIENTS_LEFT = new AtomicInteger();

	static ServerSocket socketForWelcome;
	static Thread threadForWelcomeSocket;

	private Backend() {
		throw new IllegalAccessError();
	}

}
