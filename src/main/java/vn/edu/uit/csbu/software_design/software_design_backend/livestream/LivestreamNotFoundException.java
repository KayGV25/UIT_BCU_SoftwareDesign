package vn.edu.uit.csbu.software_design.software_design_backend.livestream;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class LivestreamNotFoundException extends RuntimeException {
    public LivestreamNotFoundException(){
        super("No Livestream found");
    }
}
