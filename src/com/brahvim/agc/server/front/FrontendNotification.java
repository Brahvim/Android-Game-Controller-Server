package com.brahvim.agc.server.front;

import javafx.application.Platform;

public enum FrontendNotification {

	BACKEND_STARTED();

	// region Class stuff.
	private volatile boolean ready;

	public boolean wasFired() {
		return this.ready;
	}

	public synchronized void fire() {
		this.ready = true;
		this.notifyAll();
	}

	synchronized void doWhenFired(final Runnable p_task) {
		new Thread() {

			@Override
			public void run() {
				FrontendNotification.this.waitForFireAndHandleInterrupts();
				p_task.run();
			}

		}.start();
	}

	synchronized void onUiThreadWhenFired(final Runnable p_task) {
		new Thread() {

			@Override
			public void run() {
				FrontendNotification.this.waitForFireAndHandleInterrupts();
				Platform.runLater(p_task);
			}

		}.start();
	}

	synchronized void waitForFireAndHandleInterrupts() {
		try {
			while (!this.ready)
				this.wait();
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
