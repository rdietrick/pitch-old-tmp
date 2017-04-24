package com.pitchplayer.util;

import java.io.*;

/**
 * Class to run a command-line process and get the output from it.
 */
public class ThreadRunner extends Thread {

	public final static long BLOCK = -1;

	public final static long NO_WAIT = 0;

	// the command being executed
	String command;

	// arguments to the command
	String[] args;

	// output to stdout
	String stdOut;

	// output to stderr
	String stdErr;

	int exitStatus;

	// amount of time to allow process to run
	long timeOut;

	private Process proc = null;

	public ThreadRunner(String command, long timeOut) {
		this.timeOut = timeOut;
		if (command == null) {
			System.out.println("No command given.  Exiting");
			System.exit(1);
		}

		this.command = command;
		this.args = null;
	}

	public ThreadRunner(String command) {
		this(command, BLOCK);
	}

	public ThreadRunner(String[] cmd_args, long timeOut) {
		this.timeOut = timeOut;
		if ((cmd_args.length < 1) || (cmd_args[0] == null)) {
			System.out.println("No command given.  Exiting");
			System.exit(1);
		}
		this.command = cmd_args[0];
		if (cmd_args.length > 1) {
			this.args = new String[cmd_args.length - 1];
			for (int i = 1; i < cmd_args.length; i++) {
				if (cmd_args[i] != null)
					this.args[i - 1] = cmd_args[i];
			}
		}
	}

	public ThreadRunner(String[] cmd_args) {
		this(cmd_args, BLOCK);
	}

	/**
	 * Set the amount of time (in millis) to allow the process to run.
	 */
	public void setTimeout(long n) {
		timeOut = n;
	}

	/**
	 * Run this thread, subsequently executing the sub-process.
	 */
	public void run() {

		// get the Runtime in which this process will be run
		Runtime runtime = Runtime.getRuntime();

		// construct the command line from cmd+args
		StringBuffer commandLine = new StringBuffer();
		commandLine.append(this.command);
		if (args != null) {
			for (int i = 0; i < args.length; i++) {
				commandLine.append(" " + args[i]);
			}
		}

		// run the process
		try {
			proc = runtime.exec(commandLine.toString());

			// if timeOut == BLOCK,
			// block until process finishes
			if (timeOut == BLOCK) {
				proc.waitFor();
			}
			// if timeOut > NO_WAIT (a.k.a. 0),
			// make this thread sleep
			else {
				if (timeOut > NO_WAIT) {
					Thread.sleep(timeOut);
				}
			}

			// now should I call this?:
			// proc.destroy();
			proc.destroy();

		} catch (Exception ioe) {
			System.err.println("command failed: " + commandLine.toString());
			ioe.printStackTrace();
			return;
		}

		// get the exit status of the process
		this.exitStatus = proc.exitValue();

		// get stdout and stderr as Strings
		InputStream in = proc.getInputStream();
		InputStream err = proc.getErrorStream();
		try {
			stdOut = StreamUtil.getString(in);
			stdErr = StreamUtil.getString(err);
		} catch (Throwable t) {
			t.printStackTrace();
		}

		// make sure the process is dead:
		proc.destroy();
		proc = null;

	}

	public String getOutput() {
		return this.stdOut;
	}

	public String getError() {
		return this.stdErr;
	}

	public int getExitStatus() {
		return this.exitStatus;
	}

	public static void main(String argv[]) {
		try {
			ThreadRunner t = new ThreadRunner(argv[0]);
			if (argv.length > 1)
				t.setTimeout(Long.parseLong(argv[1]));
			t.run();
			System.out.println("stdout: \n" + t.getOutput());
			System.out.println("stderr: \n" + t.getError());
			System.out.println("Exit status: " + t.getExitStatus());
			t = null;
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}

	public void destroy() {
		if (proc != null) {
			try {
				proc.destroy();
				System.err.println("process destroyed");
			} catch (Throwable t) {
				t.printStackTrace();
			}
			proc = null;
		}
	}

}