package com.brahvim.agc.server.back;

import com.brahvim.agc.server.front.FrontendNotification;

public class AgcBackend {

	public static void launch() {
		BackendNotification.START_BACKEND.waitForFireAndHandleInterrupts();

		try {
			Thread.sleep(2000);
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		System.out.println("Backend ready!");
		FrontendNotification.BACKEND_STARTED.fire();
	}

}
