package vn.edu.uit.csbu.software_design.software_design_backend.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The type Account secure response dto.
 */
public record accountSecureResponseDTO(
    @JsonProperty("id") String id,
    @JsonProperty("name") String name,
    @JsonProperty("title") String title,
    @JsonProperty("description") String description
) {
    /**
     * Instantiates a new Account secure response dto.
     *
     * @param id          the id
     * @param name        the name
     * @param title       the title
     * @param description the description
     */
    @JsonCreator
    public accountSecureResponseDTO{
    }
}
