package spittr.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@SuppressWarnings("serial")
@ResponseStatus(value=HttpStatus.NOT_FOUND,
	reason="Spittle Not Found")
public class SpittleNotFoundException extends Exception {

}
