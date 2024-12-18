package vn.edu.uit.csbu.software_design.software_design_backend.jwt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The type Jwt model.
 */
public record JWTModel(
    @JsonProperty("token") String token
) {
    /**
     * Instantiates a new Jwt model.
     *
     * @param token the token
     */
    @JsonCreator
    public JWTModel{
    }
}
