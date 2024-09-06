package com.brahvim.agc.server.back;

public enum BackendNotification {

	START_BACKEND(),
	SHUTDOWN();

	// region Class stuff.
	private volatile boolean ready;

	public boolean wasFired() {
		return this.ready;
	}

	public synchronized void fire() {
		this.ready = true;
		this.notifyAll();
	}

	synchronized void waitForFireAndHandleInterrupts() {
		try {
			this.waitForFire();
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	synchronized void waitForFire() throws InterruptedException {
		while (!this.ready)
			this.wait();
	}
	// endregion

}
