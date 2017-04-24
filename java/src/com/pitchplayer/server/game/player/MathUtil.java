package com.pitchplayer.server.game.player;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

public class MathUtil {

	public static BigInteger factorial(BigInteger n) {
		if (n.equals(BigInteger.ONE)) {
			return n;
		}
		return n.multiply(factorial(n.subtract(BigInteger.ONE)));
	}
	

	public static BigInteger choose(int n, int r) {
		BigInteger nFact = factorial(new BigInteger(String.valueOf(n)));
		BigInteger nMinusRFact = factorial(new BigInteger(String.valueOf(n-r)));
		BigInteger rFact = factorial(new BigInteger(String.valueOf(r)));
		return nFact.divide(nMinusRFact).multiply(rFact);
	}

}
