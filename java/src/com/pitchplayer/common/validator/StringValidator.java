package com.pitchplayer.common.validator;

import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

/**
 * Validator for String data. Allows configurable validation parameters and
 * related error messages.
 */
public class StringValidator implements Validator {

	private int minLen = -1;

	private int maxLen = -1;

	private boolean allowEmpty = false;

	private boolean doTrim = false;;

	private RE regExp = null;

	private String minLenErr = null;

	private String maxLenErr = null;

	private String emptyErr = null;

	private String patternErr = null;

	/**
	 * Whether or not data should be stripped of whitespace before validation.
	 */
	public void setDoTrim(boolean b) {
		this.doTrim = b;
	}

	/**
	 * Set the minimum required length of the data.
	 */
	public void setMinLen(int n) {
		this.minLen = n;
	}

	/**
	 * Set the error message for minimum length violations.
	 */
	public void setMinLenErr(String s) {
		this.minLenErr = s;
	}

	/**
	 * Set the maximim allowed length of the data.
	 */
	public void setMaxLen(int n) {
		this.maxLen = n;
	}

	/**
	 * Set the error message for max length violations.
	 */
	public void setMaxLenErr(String s) {
		this.maxLenErr = s;
	}

	/**
	 * Set the boolean flag for empty data.
	 */
	public void setAllowEmpty(boolean b) {
		this.allowEmpty = b;
		this.doTrim = true;
	}

	/**
	 * Set the error message for empty data violations.
	 */
	public void setEmptyErr(String s) {
		this.emptyErr = s;
	}

	/**
	 * Set a regular expression pattern for evaluation.
	 */
	public void setPattern(String regExpPattern) throws RESyntaxException {
		this.regExp = new RE(regExpPattern);
	}

	/**
	 * Set the error message for pattern matching violations.
	 */
	public void setPatternErr(String s) {
		this.patternErr = s;
	}

	/**
	 * Validate a piece of data against the rules which have been set.
	 * 
	 * @param s
	 *            the data to be validated
	 * @return the validated data
	 */
	public String validate(String s) throws ValidationException {
		if (s == null) {
			s = "";
		}
		if (doTrim) {
			s = s.trim();
		}
		if (!allowEmpty && s.equals("")) {
			throw new ValidationException(emptyErr);
		}
		if ((minLen >= 0) && (s.length() < minLen)) {
			throw new ValidationException(minLenErr);
		}
		if ((maxLen >= 0) && (s.length() > maxLen)) {
			throw new ValidationException(maxLenErr);
		}
		if ((regExp != null) && (!regExp.match(s))) {
			throw new ValidationException(patternErr);
		}
		return s;
	}

	public static void main(String args[]) {
		StringValidator val = new StringValidator();
		try {
			val.setPattern("\\d{3}-?\\d{4}");
		} catch (RESyntaxException re) {
			System.out.println("Bad regular expression pattern: "
					+ re.getMessage());
		}
		val.setPatternErr("Invalid phone number");

		try {
			val.validate(args[0]);
		} catch (ValidationException ve) {
			System.out.println("Failed validation: " + ve.getMessage());
		}
	}

}