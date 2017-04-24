package com.pitchplayer.server.game.test;

import com.pitchplayer.server.game.NewRotation;

import junit.framework.TestCase;

public class NewRotationTest extends TestCase {

	public void testSingleRotation() {
		System.out.println("starting at 0");
		NewRotation r = new NewRotation(4);
		while (r.hasMoreTurns()) {
			System.out.println(r.turn());
			r.increment();
		}
	}

	
	public void testSingleRotationFromNonZeroStart() {
		System.out.println("Starting at 1");
		NewRotation r = new NewRotation(4, 1);
		while (r.hasMoreTurns()) {
			System.out.println(r.turn());
			r.increment();
		}
	}

	public void testSingleRotationFromLastPos() {
		System.out.println("Starting at 3");
		NewRotation r = new NewRotation(4, 3);
		while (r.hasMoreTurns()) {
			System.out.println(r.turn());
			r.increment();
		}
	}

	
	public void testMultipleRotationsFromZero() {
		System.out.println("Starting at 0");
		NewRotation r = new NewRotation(4, 0);
		while (r.hasMoreTurns()) {
			System.out.println(r.turn());
			r.increment();
		}
		r.reinit(1);
		System.out.println("Starting at 1");
		while (r.hasMoreTurns()) {
			System.out.println(r.turn());
			r.increment();
		}
		r.reinit(2);
		System.out.println("Starting at 2");
		while (r.hasMoreTurns()) {
			System.out.println(r.turn());
			r.increment();
		}
		r.reinit(3);
		System.out.println("Starting at 3");
		while (r.hasMoreTurns()) {
			System.out.println(r.turn());
			r.increment();
		}
	}

	
}
