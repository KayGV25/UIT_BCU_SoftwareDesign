package vn.edu.uit.csbu.software_design.software_design_backend;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import vn.edu.uit.csbu.software_design.software_design_backend.livestream.LivestreamNotFoundException;

@ControllerAdvice
public class ExceptionHandling extends ResponseEntityExceptionHandler{
    
    @ExceptionHandler(LivestreamNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleLivestreamNotFoundException(LayerInstantiationException ex){
        return ex.getMessage();
    }

}
