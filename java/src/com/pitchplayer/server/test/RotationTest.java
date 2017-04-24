package com.pitchplayer.server.test;

import junit.framework.TestCase;

import com.pitchplayer.server.game.Rotation;

/**
 * Test case for a deck of cards
 */
public class RotationTest extends TestCase {

	public RotationTest(String name) {
		super(name);
	}

	public void testTurns() {
		int numTurns = 4;
		Rotation rot = new Rotation(numTurns);
		int i = 0;
		while (rot.hasMoreTurns()) {
			assertEquals("Invalid turn", i, rot.turn());
			rot.increment();
			i++;
		}
		System.out.println("executed " + i + " turns");
		assertEquals("Incorrect number of turns", numTurns, i);
	}

}