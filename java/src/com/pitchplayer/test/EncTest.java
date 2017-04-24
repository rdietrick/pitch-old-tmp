package com.pitchplayer.test;

import net.myvietnam.mvncore.security.Encoder;
import junit.framework.TestCase;

public class EncTest extends TestCase {

	public void testEnc() {
		assertNotNull(Encoder.getMD5_Base64("passwd"));
	}
	
}
