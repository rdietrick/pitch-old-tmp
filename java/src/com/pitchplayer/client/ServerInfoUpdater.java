package com.pitchplayer.client;

/**
 * Component which continuously polls for information from the server,
 * such as the list of current games and the list of currently logged-in
 * players.
 * Should be run as a background thread.
 * There are methods provided for pausing and resuming this thread.
 * See:
 * {@link #pause()}
 * and
 * {@link #unpause()}
 * 
 * The interval at which the thread polls for server updates may be 
 * controlled via an argument to the constructor.
 * Typically, when the user interface components which display the 
 * information this component polls for are hidden, the ServerInfoUpdater
 * should be paused.
 * 
 * @author robd
 *
 */
public class ServerInfoUpdater extends Thread {

	public static final long DFLT_INTERVAL = 5000; // default sleep for 10 secs
	
	private long updateInterval = DFLT_INTERVAL;
	
	private PitchClient client;
	
	private boolean paused = false;

	private boolean running = true;
	
	public ServerInfoUpdater(PitchClient client) {
		this.client = client;
	}
	
	public ServerInfoUpdater(PitchClient client, long updateInterval) {
		this.client = client;
		this.updateInterval = updateInterval;
	}
	
	public void run() {
		while (running ) {
			try {
				if (!paused) {
					client.sendGetPlayerList();
					client.sendGetGameList();
				}
				sleep(updateInterval);
			} catch (InterruptedException ie) {
				// interruptions mean the thread resumes
			}
		}
	}
	
	public void pause() {
		if (!paused) {
			paused = true;
		}
	}
	
	public void unpause() {
		if (paused) {
			paused = false;
		}
	}

	public void kill() {
		running = false;
	}

}
