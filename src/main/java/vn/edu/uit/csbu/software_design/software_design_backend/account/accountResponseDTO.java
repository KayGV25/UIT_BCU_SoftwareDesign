package vn.edu.uit.csbu.software_design.software_design_backend.account;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class accountResponseDTO {
    private String data;
    private String response;

    public accountResponseDTO(String data, String optData ,accountResponseType type){
        switch (type) {
            case DATA -> this.data = data;
            case RESPONSE -> this.response = data;
            case ALL -> {this.data = data; this.response = optData;}
            default -> throw new IllegalArgumentException("Invalid type: " + type);
        }
    }
}