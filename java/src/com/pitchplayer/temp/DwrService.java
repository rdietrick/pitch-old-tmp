package com.pitchplayer.temp;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class DwrService implements Runnable {

	private static final long TIMEOUT = 7000;
	private List<DwrListener> listeners = new ArrayList<DwrListener>(10);
	private boolean running = false;
	// private List messages = new ArrayList(10);
	private Thread myThread;
	private Logger log = Logger.getLogger(this.getClass().getName());
	private String updatePage;
	
	public DwrService() {
		myThread  = new Thread(this);
		myThread.start();
		log.debug("thread started");
	}
	
	public void addDwrListener(DwrListener listener) {
		this.listeners.add(listener);
	}

	
	public void run() {
		running = true;
		int i = 0;
		try {
			while (running) {
				Thread.sleep(TIMEOUT);
				log.debug("sending updates, listeners.size = " + listeners.size());
				int l = 0;
				for (DwrListener listener : listeners) {
					if (l++ %2 == 0) {
						listener.update("hello " + i, updatePage);
					}
				}
				i++;
			}
			log.debug("running = false; thread stopped.");
		} catch (InterruptedException e) {
			log.error("thread interrupted", e);
			e.printStackTrace();
			running = false;
		}
	}
	
	public void finalize() {
		log.debug("finalizing");
		running = false;
		try {
			myThread.join(2000);
		} catch (InterruptedException e) {
		}
		myThread = null;
	}

	public String getUpdatePage() {
		return updatePage;
	}

	public void setUpdatePage(String updatePage) {
		log.debug("updatePage set to " + updatePage);
		this.updatePage = updatePage;
	}
	
}
