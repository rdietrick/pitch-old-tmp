package com.pitchplayer.util;

import java.util.Hashtable;

/**
 * Abstract superclass of all command line programs. Provides methods for
 * parsing command-line arguments.
 */
public abstract class CommandLineProgram {

	/**
	 * optionHash will be a hashtable of valid command-line args and the number
	 * of values expected after each. Implement initOptions in subclasses and
	 * pass this into it.
	 */
	static Hashtable optionHash = new Hashtable();

	static void printError(String errMsg) {
		System.out.println(errMsg);
		System.out.println("Try:  -h for more information.");
		System.exit(1);
	}

	/**
	 * Should initialize the parameter oh by setting it's key-value pairs like
	 * so: oh.put("option_name", new Integer(number_of_values_expected)); ...
	 * Where "option_name" is a valid option (ie, "-p"), and
	 * number_of_values_expected is 0 or 1, and represents the number of values
	 * expected after that command-line argument.
	 */
	protected abstract void initOptions(Hashtable oh);

	/**
	 * Returns true if the parameter is a valid option. Valid options should
	 * have previously been defined by implementing initOptions
	 */
	static boolean isAnOption(String arg) {
		if (arg.indexOf("-") != 0)
			return false;
		else {
			if (optionHash.containsKey(arg)) {
				return true;
			} else
				return false;
		}
	}

	/**
	 * Returns opposite of isAnOption()
	 */
	static boolean isNotAnOption(String arg) {
		return !isAnOption(arg);
	}

	/**
	 * Takes main()'s argv parameter, and returns a Hashtable of command-line
	 * arguments and their corresponding values.
	 */
	static Hashtable parseArgs(String[] argv) {
		Hashtable hash = new Hashtable();
		for (int i = 0; i < argv.length; i++) {
			if (isAnOption(argv[i])) {
				if ((argv.length > (i + 1)) && (isNotAnOption(argv[i + 1]))) {
					hash.put(argv[i], argv[++i]);
				} else {
					hash.put(argv[i], "");
				}
			} else {
				printError("Bad option: " + argv[i]);
			}
		}
		return hash;
	}

}