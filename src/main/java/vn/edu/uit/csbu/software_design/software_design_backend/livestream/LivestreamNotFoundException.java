package vn.edu.uit.csbu.software_design.software_design_backend.livestream;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * The type Livestream not found exception.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class LivestreamNotFoundException extends RuntimeException {
    /**
     * Instantiates a new Livestream not found exception.
     */
    public LivestreamNotFoundException(){
        super("No Livestream found");
    }
}
