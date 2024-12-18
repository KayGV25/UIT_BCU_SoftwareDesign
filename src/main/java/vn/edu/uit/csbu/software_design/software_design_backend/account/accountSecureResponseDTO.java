package vn.edu.uit.csbu.software_design.software_design_backend.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record accountSecureResponseDTO(
    @JsonProperty("id") String id,
    @JsonProperty("name") String name,
    @JsonProperty("title") String title,
    @JsonProperty("description") String description
) {
    @JsonCreator
    public accountSecureResponseDTO{
    }
}
