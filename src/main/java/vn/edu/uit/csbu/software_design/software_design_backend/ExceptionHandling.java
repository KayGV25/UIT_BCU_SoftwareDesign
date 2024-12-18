package vn.edu.uit.csbu.software_design.software_design_backend;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import vn.edu.uit.csbu.software_design.software_design_backend.livestream.LivestreamNotFoundException;

/**
 * The type Exception handling.
 */
@ControllerAdvice
public class ExceptionHandling extends ResponseEntityExceptionHandler{

    /**
     * Handle livestream not found exception string.
     *
     * @param ex the ex
     * @return the string
     */
    @ExceptionHandler(LivestreamNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleLivestreamNotFoundException(LayerInstantiationException ex){
        return ex.getMessage();
    }

}
