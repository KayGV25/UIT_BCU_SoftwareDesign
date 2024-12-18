package vn.edu.uit.csbu.software_design.software_design_backend.jwt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record JWTModel(
    @JsonProperty("token") String token
) {
    @JsonCreator
    public JWTModel{
    }
}
