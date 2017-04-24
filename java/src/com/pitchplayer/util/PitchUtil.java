package com.pitchplayer.util;

public class PitchUtil {

	public static int getRandomInt(int min, int max) {
		return (int) (Math.random() * max) + min;
	}

}