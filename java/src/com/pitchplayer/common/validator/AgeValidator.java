package com.pitchplayer.common.validator;

import java.util.Date;
import java.util.Calendar;

public class AgeValidator extends DateValidator {

	private int minAge = -1;

	private int maxAge = -1;

	private String minAgeErr;

	private String maxAgeErr;

	/**
	 * Set the minimum age required for validation.
	 */
	public void setMinAge(int n) {
		this.minAge = n;
	}

	/**
	 * Get the minimum age required for validation.
	 */
	public int getMinAge() {
		return minAge;
	}

	/**
	 * Set the maximum age required for validation.
	 */
	public void setMaxAge(int n) {
		this.maxAge = n;
	}

	/**
	 * Get the maximum age required for validation.
	 */
	public int getMaxAge() {
		return maxAge;
	}

	/**
	 * Set the minimum age violation error message
	 */
	public void setMinAgeErr(String s) {
		this.minAgeErr = s;
	}

	/**
	 * Get the minimum age violation error message
	 */
	public String getMinAgeErr() {
		return minAgeErr;
	}

	/**
	 * Set the maximum age violation error message
	 */
	public void setMaxAgeErr(String s) {
		this.maxAgeErr = s;
	}

	/**
	 * Get the maximum age violation error message
	 */
	public String getMaxAgeErr() {
		return maxAgeErr;
	}

	/**
	 * Ensure that a date meets mim/max age requirements.
	 */
	private Date validateDate(Date date) throws AgeValidationException {
		Calendar cal = Calendar.getInstance();
		Date now = cal.getTime();
		if (minAge > -1) {
			cal.add(Calendar.YEAR, -1 * minAge); // roll calendar back minAge
												 // years ago
			if (cal.getTime().before(date)) {
				throw new AgeValidationException(minAgeErr);
			}
		}
		if (maxAge > -1) {
			cal.setTime(date);
			cal.add(Calendar.YEAR, maxAge);
			if (cal.getTime().before(now)) {
				throw new AgeValidationException(maxAgeErr);
			}
		}
		return date;
	}

	/**
	 * Validate a date, checking for textual integrity and age restrictions.
	 */
	public Date validateDate(String dateStr) throws ValidationException {
		Date date = super.validateDate(dateStr);
		return validateDate(date);
	}

	/**
	 * Validate a date, checking for textual integrity and age restrictions.
	 */
	public Date validateDate(String monthStr, String dayStr, String yearStr)
			throws ValidationException {
		Date date = super.validateDate(monthStr, dayStr, yearStr);
		return validateDate(date);
	}

	/**
	 * Validate a date, checking for textual integrity and age restrictions.
	 */
	public Date validateDate(int month, int day, int year)
			throws ValidationException {
		Date date = super.validateDate(month, day, year);
		return validateDate(date);
	}

	public static void main(String args[]) {
		AgeValidator av = new AgeValidator();
		av.setInvalidErr("Work with me here, OK?");
		String dateStr = args[0];
		if (args.length > 1) {
			av.setMinAge(Integer.parseInt(args[1]));
			av.setMinAgeErr("Minimum age not met");
		}
		if (args.length > 2) {
			av.setMaxAge(Integer.parseInt(args[2]));
			av.setMaxAgeErr("Maximum age exceeded");
		}
		try {
			av.validateDate(dateStr);
			System.out.println("A-OK");
		} catch (ValidationException ve) {
			System.out.println(ve.getMessage());
		}
	}

}