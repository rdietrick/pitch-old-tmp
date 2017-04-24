package com.pitchplayer.common.validator;

import java.util.StringTokenizer;
import java.util.Date;
import java.util.Calendar;

/**
 * Custom validator for dates composed of month, day, and year fields. Expects
 * date string in format m/d/yyyy
 */
public class DateValidator extends StringValidator {

	private String delimiter = DFLT_DELIMITER;

	private String invalidErr = DFLT_INVALID_ERR;

	private String invalidMonthErr = DFLT_INVALID_MONTH_ERR;

	private String invalidDayErr = DFLT_INVALID_DAY_ERR;

	private String invalidYearErr = DFLT_INVALID_YEAR_ERR;

	private boolean allowPast = true;

	private boolean allowFuture = true;

	private String pastErr = DFLT_PAST_ERR;

	private String futureErr = DFLT_FUTURE_ERR;

	public static final String DFLT_INVALID_ERR = "Please enter a valid date";

	public static final String DFLT_INVALID_MONTH_ERR = "Invalid month";

	public static final String DFLT_INVALID_DAY_ERR = "Invalid day";

	public static final String DFLT_INVALID_YEAR_ERR = "Invalid year";

	public static final String DFLT_PAST_ERR = "Please enter a date in the future";

	public static final String DFLT_FUTURE_ERR = "Please enter a date in the past";

	public static final String DFLT_DELIMITER = "/";

	/**
	 * Set the delimiter used when parsing date strings '/' for example in the
	 * string 7/27/1973
	 */
	public void setDelimiter(String s) {
		this.delimiter = s;
	}

	/**
	 * Set the delimiter used when parsing date strings
	 */
	public String getDelimiter() {
		return this.delimiter;
	}

	/**
	 * Set the error message for invalid dates
	 */
	public void setInvalidErr(String s) {
		this.invalidErr = s;
	}

	/**
	 * Set the error message for invalid month
	 */
	public void setMonthErr(String s) {
		this.invalidMonthErr = s;
	}

	/**
	 * Set the error message for invalid day
	 */
	public void setDayErr(String s) {
		this.invalidDayErr = s;
	}

	/**
	 * Set the error message for invalid year
	 */
	public void setYearErr(String s) {
		this.invalidYearErr = s;
	}

	/**
	 * Get the error message for an invalid date
	 */
	public String getInvalidErr() {
		return this.invalidErr;
	}

	/**
	 * Get the error message for an invalid month
	 */
	public String getMonthErr() {
		return this.invalidMonthErr;
	}

	/**
	 * Get the error message for an invalid day
	 */
	public String getDayErr() {
		return this.invalidDayErr;
	}

	/**
	 * Get the error message for an invalid year
	 */
	public String getYearErr() {
		return this.invalidYearErr;
	}

	/**
	 * Set whether or not to allow dates in the past
	 */
	public void setAllowPast(boolean b) {
		this.allowPast = b;
	}

	/**
	 * Get whether or not to allow dates in the past
	 */
	public boolean getAllowPast() {
		return this.allowPast;
	}

	/**
	 * Set whether or not to allow dates in the future
	 */
	public void setAllowFuture(boolean b) {
		this.allowFuture = b;
	}

	/**
	 * Get whether or not to allow dates in the future
	 */
	public boolean getAllowFuture() {
		return this.allowFuture;
	}

	/**
	 * Set the error message for dates in the past if they are not allowed
	 */
	public void setPastErr(String s) {
		this.pastErr = s;
	}

	/**
	 * Get the error message for dates in the past if they are not allowed
	 */
	public String getPastErr() {
		return this.pastErr;
	}

	/**
	 * Set the error message for dates in the future if they are not allowed
	 */
	public void setFutureErr(String s) {
		this.futureErr = s;
	}

	/**
	 * Get the error message for dates in the future if they are not allowed
	 */
	public String getFutureErr() {
		return this.futureErr;
	}

	/**
	 * Create a new date validator
	 */
	public DateValidator() {
		this.setAllowEmpty(false);
		this.setEmptyErr(DFLT_INVALID_ERR);
		this.setMinLen(8);
		this.setMinLenErr(DFLT_INVALID_ERR);
	}

	/**
	 * Validate the three parameters as month, day, and year fields of a date.
	 * Month is expected to be 1-based.
	 * 
	 * @param monthStr
	 *            the numeric month (1-12)
	 * @param dayStr
	 *            the day of the month (1-31)
	 * @param yearStr
	 *            the year
	 * @return a Date parsed from the three parameters
	 * @throws ValidationException
	 *             if the three parameters don't parse into a valid date or the
	 *             parsed date violates any validation rules previously set.
	 */
	public Date validateDate(String monthStr, String dayStr, String yearStr)
			throws ValidationException {
		int month = -1;
		int day = -1;
		int year = -1;
		try {
			month = Integer.parseInt(monthStr);
		} catch (NumberFormatException nfe) {
			throw new ValidationException(invalidMonthErr);
		}
		try {
			day = Integer.parseInt(dayStr);
		} catch (NumberFormatException nfe) {
			throw new ValidationException(invalidDayErr);
		}
		try {
			year = Integer.parseInt(yearStr);
		} catch (NumberFormatException nfe) {
			throw new ValidationException(invalidYearErr);
		}
		return validateDate(month, day, year);
	}

	/**
	 * Validate the three parameters as month, day, and year fields of a date.
	 * Month is expected to be 1-based.
	 * 
	 * @param month
	 *            the numeric month (1-12)
	 * @param day
	 *            the day of the month (1-31)
	 * @param year
	 *            the year
	 * @return a Date parsed from the three parameters
	 * @throws ValidationException
	 *             if the three parameters don't parse into a valid date or the
	 *             parsed date violates any validation rules previously set.
	 */
	public Date validateDate(int month, int day, int year)
			throws ValidationException {

		month = month - 1; // translate to 0-based month
		if ((month < 0) || (month > 11)) {
			throw new ValidationException(invalidMonthErr);
		}
		if ((day < 1) || (day > 31)) {
			throw new ValidationException(invalidDayErr);
		}

		Calendar cal = Calendar.getInstance();
		Date now = cal.getTime();
		Date date = null;
		cal.setLenient(false);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DATE, day);
		cal.set(Calendar.YEAR, year);
		try {
			date = cal.getTime();
		} catch (IllegalArgumentException iae) {
			throw new ValidationException(invalidErr);
		}

		if (!allowFuture) {
			if (now.before(date)) {
				throw new ValidationException(futureErr);
			}
		}
		if (!allowPast) {
			if (now.after(date)) {
				throw new ValidationException(pastErr);
			}
		}
		return date;
	}

	/**
	 * Takes a string in the format m[delimiter]d[delimiter]yyyy and checks to
	 * see if it forms a valid date. The delimiter is configurable via the
	 * setDelimiter() method.
	 * 
	 * @param dateStr
	 *            a string specifying a date as m[delimiter]d[delimiter]yyyy
	 * @return a Date parsed from the dateStr parameter
	 * @throws ValidationException
	 *             if the string parameter did not specify a valid date,
	 *             according to the specified validation rules.
	 */
	public Date validateDate(String dateStr) throws ValidationException {
		// string validation
		try {
			dateStr = super.validate(dateStr);
		} catch (ValidationException ve) {
			System.out.println("failed string validation");
			throw ve;
		}

		StringTokenizer st = new StringTokenizer(dateStr, delimiter);
		if (st.countTokens() != 3) {
			throw new ValidationException(invalidErr);
		}
		return validateDate(st.nextToken(), st.nextToken(), st.nextToken());
	}

	/**
	 * This is only a test.
	 */
	public static void main(String args[]) {
		DateValidator dv = new DateValidator();
		dv.setInvalidErr("Work with me here, biatch");
		String s = args[0];
		try {
			dv.validateDate(s);
			System.out.println(s + " is a valid date");
		} catch (ValidationException ve) {
			System.out.println("Error: " + ve.getMessage());
		}
	}

}