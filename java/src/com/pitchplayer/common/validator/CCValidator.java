package com.pitchplayer.common.validator;

/**
 * Class which verifies credit card numbers based solely on numerical validity.
 */
public class CCValidator implements Validator {

	private int cardType;

	private String invalidErr = null;

	// constants for credit card types
	public static final int CARDTYPE_UNKNOWN = 0;

	public static final int CARDTYPE_VISA = 1;

	public static final int CARDTYPE_AMEX = 2;

	public static final int CARDTYPE_DINERSCLUB = 3;

	public static final int CARDTYPE_JCB = 4;

	public static final int CARDTYPE_DISCOVER = 5;

	public static final int CARDTYPE_ENROUTE = 6;

	public static final int CARDTYPE_MASTERCARD = 7;

	/**
	 * Private contstructor. Must use factory method getCCValidator(int
	 * cardType) to get an instance.
	 */
	private CCValidator(int cardType) {
		this.cardType = cardType;
	}

	/**
	 * Factory method to get a validator for a particular card type.
	 */
	public static CCValidator getCCValidator(int cardType) {
		return new CCValidator(cardType);
	}

	/**
	 * Set the error message for invalida credit card numbers.
	 */
	public void setInvalidErr(String s) {
		this.invalidErr = s;
	}

	/**
	 * Determines whether a credit card number is valid based on multiplier
	 * algorithm
	 * 
	 * @param ccNum
	 *            a credit card number to be tested
	 * @return true if the card number is valid
	 */
	public static boolean isValidCCNum(String ccNum) {
		int i;
		int total = 0;
		String tempMultiplier = "";

		for (i = ccNum.length(); i >= 2; i -= 2) {
			total = total + cint(ccNum.charAt(i - 1));
			tempMultiplier = "" + (cint(ccNum.charAt(i - 2)) * 2);
			total = total + cint(left(tempMultiplier));

			if (tempMultiplier.length() > 1) {
				total = total + cint(right(tempMultiplier));
			}
		}

		if (ccNum.length() % 2 == 1) {
			total = total + cint(left(ccNum));
		}
		if (total % 10 == 0)
			return (true);
		else
			return (false);
	}

	/**
	 * @param s
	 *            a string
	 * @return the left-most character of the parameter s
	 */
	private static char left(String s) {
		return (s.charAt(0));
	}

	/**
	 * @param s
	 *            a string
	 * @return the right-most character of the parameter s
	 */
	private static char right(String s) {
		return (s.charAt(s.length() - 1));
	}

	/**
	 * parse an int from a char
	 */
	private static int cint(char ch) {
		if (ch == '0')
			return (0);
		if (ch == '1')
			return (1);
		if (ch == '2')
			return (2);
		if (ch == '3')
			return (3);
		if (ch == '4')
			return (4);
		if (ch == '5')
			return (5);
		if (ch == '6')
			return (6);
		if (ch == '7')
			return (7);
		if (ch == '8')
			return (8);
		if (ch == '9')
			return (9);
		// Should never get here, but oh well
		return (0);
	}

	/**
	 * Deduces the card type based on the credit card numbers.
	 * 
	 * @return an integer corresponding to one of the card type constants, based
	 *         on an algorithmic check of the card number
	 */
	public static int getCardType(String ccNum) {
		String header = "";

		switch (left(ccNum)) {
		case '5':
			header = ccNum.substring(0, 2);

			if (Integer.parseInt(header) >= 51
					&& Integer.parseInt(header) <= 55 && ccNum.length() == 16) {
				return (CARDTYPE_MASTERCARD);
			}
			break;
		case '4':

			if (ccNum.length() == 13 || ccNum.length() == 16) {
				return (CARDTYPE_VISA);
			}
			break;
		case '3':
			header = ccNum.substring(0, 3);

			if (Integer.parseInt(header) >= 340
					&& Integer.parseInt(header) <= 379 && ccNum.length() == 15) {
				return (CARDTYPE_AMEX);
			}

			if (Integer.parseInt(header) >= 300
					&& Integer.parseInt(header) <= 305 && ccNum.length() == 14) {
				return (CARDTYPE_DINERSCLUB);
			}

			if (Integer.parseInt(header) >= 360
					&& Integer.parseInt(header) <= 368 && ccNum.length() == 14) {
				return (CARDTYPE_DINERSCLUB);
			}

			if (Integer.parseInt(header) >= 380
					&& Integer.parseInt(header) <= 389 && ccNum.length() == 14) {
				return (CARDTYPE_DINERSCLUB);
			}

			if (Integer.parseInt(header) >= 300
					&& Integer.parseInt(header) <= 399 && ccNum.length() == 16) {
				return (CARDTYPE_JCB);
			}
			break;
		case '6':
			header = ccNum.substring(0, 4);

			if (Integer.parseInt(header) == 6011 && ccNum.length() == 16) {
				return (CARDTYPE_DISCOVER);
			}
			break;
		case '2':
			header = ccNum.substring(0, 4);

			if ((Integer.parseInt(header) == 2014 || Integer.parseInt(header) == 2149)
					&& ccNum.length() == 15) {
				return (CARDTYPE_ENROUTE);
			}

			if (Integer.parseInt(header) == 2131 && ccNum.length() == 15) {
				return (CARDTYPE_JCB);
			}
			break;
		case '1':
			header = ccNum.substring(0, 4);

			if (Integer.parseInt(header) == 1800 && ccNum.length() == 15) {
				return (CARDTYPE_JCB);
			}
			break;
		}
		return (CARDTYPE_UNKNOWN);
	}

	/**
	 * Find out if a credit card number is valid for that card type.
	 * 
	 * @param cardNum
	 *            a credit card number
	 * @return true if the card number is valid and matches the card type.
	 */
	public String validate(String cardNum) throws ValidationException {
		String cc = cardNum.trim();

		// check whether it's valid
		if (!isValidCCNum(cc)) {
			throw new ValidationException(invalidErr);
		}

		int validatedCardType = getCardType(cc);
		// only accept VISA and MASTERCARD presently
		if (((validatedCardType == CARDTYPE_MASTERCARD) && cardType == CARDTYPE_MASTERCARD)
				|| ((validatedCardType == CARDTYPE_VISA) && cardType == CARDTYPE_VISA)) {
			return cc;
		} else {
			throw new ValidationException(invalidErr);
		}
	}

}