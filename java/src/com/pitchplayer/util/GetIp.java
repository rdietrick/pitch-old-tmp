package com.pitchplayer.util;

public class GetIp {

	ThreadRunner runner;

	private String output;

	String ip;

	public GetIp() {
		runner = new ThreadRunner("/sbin/ifconfig eth0");
		runner.run();
		output = runner.getOutput();
		String prefix = "inet addr:";
		String end = "  B";
		ip = output.substring(output.indexOf(prefix) + prefix.length(), output
				.indexOf(end, output.indexOf(prefix) + 1));
	}

	public String getIp() {
		return ip;
	}

	public static void main(String argv[]) {
		GetIp getip = new GetIp();
		System.out.println("'" + getip.getIp() + "'");
	}

}