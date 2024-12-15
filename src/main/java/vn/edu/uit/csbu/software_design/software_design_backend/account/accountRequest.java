package vn.edu.uit.csbu.software_design.software_design_backend.account;

import java.util.Optional;

public record accountRequest(
    String name,
    String password,
    Optional<String> data
) {

}
