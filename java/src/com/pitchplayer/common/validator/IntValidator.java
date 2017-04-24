package com.pitchplayer.common.validator;

public class IntValidator implements Validator {

	private boolean allowEmpty = true;

	private int min = -1;

	private int max = -1;

	private boolean doMin = false;

	private boolean doMax = false;

	private String emptyErr = null;

	private String minErr = null;

	private String maxErr = null;

	private String nonNumericErr = null;

	public void setAllowEmpty(boolean allow) {
		allowEmpty = allow;
	}

	public void setMin(int n) {
		min = n;
		doMin = true;
	}

	public void setMax(int n) {
		max = n;
		doMax = true;
	}

	public void setEmptyErr(String s) {
		emptyErr = s;
	}

	public void setMinErr(String s) {
		minErr = s;
	}

	public void setMaxErr(String s) {
		maxErr = s;
	}

	public void setNonNumericErr(String s) {
		nonNumericErr = s;
	}

	/**
	 * Validate input.
	 */
	public String validate(String s) throws ValidationException {
		if (null == s) {
			s = "";
		} else {
			s = s.trim();
		}
		int n;
		try {
			n = Integer.parseInt(s);
		} catch (NumberFormatException nfe) {
			throw new ValidationException(nonNumericErr);
		}
		if (doMin && (n < min)) {
			throw new ValidationException(minErr);
		}
		if (doMax && (n > max)) {
			throw new ValidationException(maxErr);
		}
		return s;
	}

	/**
	 * Validate input.
	 */
	public int validateInt(String s) throws ValidationException {
		return Integer.parseInt(validate(s));
	}

}