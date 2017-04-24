package com.pitchplayer;

/**
 * Encapsulates the directive (command) and argument of <BR>
 * a command passed across network.
 */
public class Command implements java.io.Serializable {

	private String com;

	private String[] args;

	/**
	 * Creates a command with no args
	 */
	public Command(String com) {
		this.com = com;
		this.args = null;
	}

	/**
	 * Creates a command with one arg
	 */
	public Command(String com, String arg) {
		this(com);
		this.args = new String[1];
		this.args[0] = arg;
	}

	/**
	 * Creates a command with an array of args
	 */
	public Command(String com, String[] args) {
		this(com);
		this.args = new String[args.length];
		this.args = args;
	}

	/**
	 * Returns the directive (command)
	 */
	public String getCommand() {
		return this.com;
	}

	/**
	 * Returns the arguments array
	 */
	public String[] getArgs() {
		return this.args;
	}

	/**
	 * Prints out all information about the object. <BR>
	 * useful for debugging
	 */
	public void print() {
		System.out.println("Object: " + this.getClass().getName());
		System.out.println("\t" + "Command: " + this.getCommand());
		for (int i = 0; i < this.args.length; i++)
			System.out.println("\t" + "Arg: " + this.args[i]);
	}

}