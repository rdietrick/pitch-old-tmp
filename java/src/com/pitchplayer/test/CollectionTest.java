package com.pitchplayer.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import junit.framework.TestCase;

public class CollectionTest extends TestCase {

	private Object getObj() {
		return new Object() {
			public boolean equals() {
				return false;
			}
		};
	}
	
	public void testEquals() {
		List l = new ArrayList(1);
		Object o = getObj(); 
		l.add(o);
		this.assertTrue(l.contains(o));
	}
	
	
	
}
