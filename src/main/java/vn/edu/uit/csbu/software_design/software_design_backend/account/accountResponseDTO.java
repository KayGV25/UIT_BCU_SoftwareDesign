package vn.edu.uit.csbu.software_design.software_design_backend.account;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class accountResponseDTO {
    @JsonProperty("data")
    private String data;
    @JsonProperty("response")
    private String response;
    @JsonProperty("dataArray")
    private ArrayList<accountModel> dataArray;

    @Override
    public String toString() {
        return "accountResponseDTO{data='" + data + "', response='" + response + "', dataArray=" + dataArray + "}";
    }

    public accountResponseDTO(String data, String optData ,accountResponseType type){
        switch (type) {
            case DATA -> this.data = data;
            case RESPONSE -> this.response = data;
            case ALL -> {this.data = data; this.response = optData;}
            default -> throw new IllegalArgumentException("Invalid type: " + type);
        }
    }

    public accountResponseDTO(ArrayList<accountModel> dataArray, String optData ,accountResponseType type){
        switch (type) {
            case DATA -> this.dataArray = dataArray;
            case RESPONSE -> this.response = optData;
            case ALL -> {this.dataArray = dataArray; this.response = optData;}
            default -> throw new IllegalArgumentException("Invalid type: " + type);
        }
    }
}