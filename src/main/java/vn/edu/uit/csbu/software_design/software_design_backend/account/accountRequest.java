package vn.edu.uit.csbu.software_design.software_design_backend.account;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The type Account request.
 */
public record accountRequest(
    @JsonProperty("name") String name,
    @JsonProperty("password")String password,
    @JsonProperty("data") Optional<String> data
) {
    /**
     * Instantiates a new Account request.
     *
     * @param name     the name
     * @param password the password
     * @param data     the data
     */
    @JsonCreator
    public accountRequest{
    }
}
