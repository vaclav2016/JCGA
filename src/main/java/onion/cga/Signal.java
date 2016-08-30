package onion.cga;

public class Signal {
	private boolean signalled = false;

	public synchronized void clear() {
		this.signalled = false;
	}

	public synchronized void setSignal() {
		this.signalled = true;
		this.notifyAll();
	}

	public synchronized void waitForSignal(long timeout) throws InterruptedException {
		if (!this.signalled) {
			this.wait(timeout);
		}
	}

	public synchronized void waitForSignal() throws InterruptedException {
		while (!this.signalled) {
			this.wait();
		}
	}

	public synchronized boolean isSignalled() {
		return this.signalled;
	}
}
