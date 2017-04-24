package com.pitchplayer;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.DataOutputStream;

public class NetUtils {

	public static final int INT_LEN = 4;

	public static final int LONG_LEN = 8;

	/**
	 * Parse an integer from an array of bytes
	 * 
	 * @param in
	 *            the InputStream from which the ling will be read
	 */
	public int readInt(InputStream in) throws IOException {
		byte[] bytes = new byte[INT_LEN];
		in.read(bytes);
		int i = (((bytes[0] & 0xff) << 24) | ((bytes[1] & 0xff) << 16)
				| ((bytes[2] & 0xff) << 8) | (bytes[3] & 0xff));
		return i;
	}

	/**
	 * Parse an long from an array of bytes
	 * 
	 * @param in
	 *            the InputStream from which the long will be read
	 */
	public long readLong(InputStream in) throws IOException {
		byte[] bytes = new byte[LONG_LEN];
		in.read(bytes);
		long l = (((bytes[0] & 0xff) << 56) | ((bytes[1] & 0xff) << 48)
				| ((bytes[2] & 0xff) << 40) | ((bytes[3] & 0xff) << 32)
				| ((bytes[4] & 0xff) << 24) | ((bytes[5] & 0xff) << 16)
				| ((bytes[6] & 0xff) << 8) | (bytes[7] & 0xff));
		return l;
	}

	/**
	 *  
	 */
	public String readString(InputStream in, int len) throws IOException {
		byte[] bytes = new byte[len];
		in.read(bytes);
		return new String(bytes);
	}

	/**
	 * Write an integer out to the output stream
	 */
	public void writeInt(OutputStream out, int n) throws IOException {
		DataOutputStream dataOut = new DataOutputStream(out);
		dataOut.writeInt(n);
		dataOut = null;
	}

}