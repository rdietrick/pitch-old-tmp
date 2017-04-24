package com.pitchplayer.util;

import java.util.Hashtable;

public class RandomNumberGenerator {

	public static int getInt(int min, int max) {
		int rand = new Double(Math.floor(Math.random() * ((max) - min)))
				.intValue()
				+ min;
		return rand;
	}

	public static void main(String argv[]) {
		int min, max, reps;
		min = max = reps = 0;
		Hashtable stats = new Hashtable();
		if (argv.length == 3) {
			try {
				min = Integer.parseInt(argv[0]);
				max = Integer.parseInt(argv[1]);
				reps = Integer.parseInt(argv[2]);
			} catch (NumberFormatException nfe) {
				System.out.println("Invalid parameter.");
				System.exit(0);
			}

			for (int i = 0; i < reps; i++) {
				int r = getInt(min, max);
				updateStat(stats, r);
			}
		} else
			System.out.println("Invalid usage");

		for (int i = min; i < max; i++) {
			System.out.print(i + ":\t");
			Integer y = new Integer(0);
			if (stats.containsKey(i + "")) {
				y = (Integer) stats.get(i + "");
			}
			for (int j = 0; j < y.intValue(); j++) {
				System.out.print("#");
			}
			System.out.println();
		}

	}

	private static void updateStat(Hashtable hash, int n) {
		String key = n + "";
		if (hash.containsKey(key))
			hash
					.put(key, new Integer(
							((Integer) hash.get(key)).intValue() + 1));
		else
			hash.put(key, new Integer(1));
	}

}