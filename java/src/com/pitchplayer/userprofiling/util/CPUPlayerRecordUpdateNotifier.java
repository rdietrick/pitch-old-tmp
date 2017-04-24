package com.pitchplayer.userprofiling.util;

import com.pitchplayer.userprofiling.om.CPUPlayerRecord;

public interface CPUPlayerRecordUpdateNotifier {

	public void addListener(CPUPlayerRecordUpdateListener listener);
	
	public void notifyPlayerUpdated(CPUPlayerRecord player);
	
}
