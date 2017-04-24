package com.pitchplayer.common.validator;

/**
 * Prototype for validating web data.
 */
public interface Validator {

	public String validate(String s) throws ValidationException;

}