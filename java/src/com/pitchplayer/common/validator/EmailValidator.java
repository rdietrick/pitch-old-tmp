package com.pitchplayer.common.validator;

/**
 * Custom Validator for validating email addresses.
 */
public class EmailValidator extends StringValidator {

	public static final String EMAIL_ERR = "Invalid email address";

	public EmailValidator() {
		this.setAllowEmpty(false);
		this.setEmptyErr(EMAIL_ERR);
		this.setDoTrim(true);
		this.setMinLen(5); // x@x.x
		this.setMinLenErr(EMAIL_ERR);
	}

	/**
	 * Does some custom validation, ensuring that the address contains something
	 * before the @, and at least one . in the domain portion.
	 */
	public String validate(String email) throws ValidationException {
		email = super.validate(email);

		boolean validEmail = false;
		email = email.trim();
		int indexOfAt = email.indexOf("@");
		int indexOfDot = email.lastIndexOf(".");
		if ((indexOfAt > 0) && (indexOfDot > indexOfAt)) {
			return email;
		} else
			throw new ValidationException(EMAIL_ERR);

		/*
		 * if using Java Mail v 1.3, replace implementation above with the
		 * following: InternetAddress addr = new InternetAddress(); try {
		 * addr.setAddress(email); addr.validate(); } catch (AddressException
		 * ae) { throw new ValidationException(EMAIL_ERR); } return
		 * addr.getAddress();
		 */
	}

}