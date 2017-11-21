package spittr.web;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import spittr.exception.DuplicateSpittleException;


@ControllerAdvice
public class SpittrWebAppExceptionHandler {
	@ExceptionHandler(DuplicateSpittleException.class)
	public String duplicateSpittleHandler() {
		return "error/duplicate";
	}
}