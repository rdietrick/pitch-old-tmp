package com.pitchplayer.userprofiling.util;

import com.pitchplayer.userprofiling.om.CPUPlayerRecord;

public interface CPUPlayerRecordUpdateListener {

	/**
	 * Called when a player record is updated.
	 * @param player
	 */
	public void playerUpdated(CPUPlayerRecord player);
	
}
