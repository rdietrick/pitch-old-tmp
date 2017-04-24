package com.pitchplayer.server;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

public class MemoryBasedGameChallengeService implements GameChallengeService, Runnable {

	private boolean stopped = true;
	private long interval = 1500; // time to sleep between challenge distributions
	private List<Challenge> newChallenges = new Vector<Challenge>(10);
	private List<Challenge> sentChallenges = new Vector<Challenge>(10);
	private List<ChallengeListener> listeners = new Vector<ChallengeListener>(10);
	Logger log = Logger.getLogger(this.getClass().getName());
	
	public synchronized void sendChallenge(Challenge challenge) {
		newChallenges.add(challenge);
	}

	public void addChallengeListener(ChallengeListener listener) {
		synchronized (this) { 
			this.listeners.add(listener);
		}
		log.debug("registered new ChallengeListener of class " + listener.getClass().getName());
	}

	public void run() {
		stopped = false;
		while (!stopped) {
			try {
				Thread.currentThread().sleep(interval);
			} catch (InterruptedException e) {
				log.error("Challenge Service interrupted", e);
				stopped = true;
			}
			try {
			// send new challenges 
			if (!listeners.isEmpty() && !newChallenges.isEmpty()) {
				for (Iterator<Challenge> i = newChallenges.listIterator(); i.hasNext();) {
					Challenge c = i.next();
					if (!c.getExpired()) {
						synchronized (this) {
							log.debug("sending challenge from " + c.getChallenger().getUsername());
// 							listenerArr = listeners.toArray(new ChallengeListener[listeners.size()]);
							for (ChallengeListener listener : listeners) {
								listener.receiveChallenge(c);
							}
						}
						sentChallenges.add(c);
					}
					// challenge has been delivered or is expired, so remove it from the queue
					i.remove();
				}
			}
			} catch (Throwable t) {
				log.error("Error sending challenges", t);
			}
			// revoke expired challenges
			for (Iterator<Challenge> i = sentChallenges.listIterator();i.hasNext();) {
				Challenge c = i.next();
				if (c.getExpired()) {
					ChallengeListener[] listenerArr;
					synchronized (this) {
//						listenerArr = listeners.toArray(new ChallengeListener[listeners.size()]);
						for (ChallengeListener listener : listeners) {
							listener.revokeChallenge(c);
						}
					}
					i.remove();
				}
			}
		}
	}
	
	public void stop() {
		this.stopped = true;
	}
	
	public void destroy() {
		log.debug("destroying");
		stopped = true;
		newChallenges = null;
		listeners = null;
	}

	public long getInterval() {
		return interval;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}

	public void removeChallengeListener(ChallengeListener listener) {
		synchronized (this) {
			this.listeners.remove(listener);
		}
	}

}
