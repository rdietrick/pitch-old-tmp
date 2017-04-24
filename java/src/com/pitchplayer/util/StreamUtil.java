package com.pitchplayer.util;

import java.io.*;

public class StreamUtil {

	public static String getString(InputStream in) throws IOException {
		LineNumberReader reader = new LineNumberReader(
				new InputStreamReader(in));
		StringBuffer strBuffer = new StringBuffer();
		String line = null;
		while ((line = reader.readLine()) != null) {
			strBuffer.append(line + "\n");
		}
		reader.close();
		return strBuffer.toString();
	}

}

