package vn.edu.uit.csbu.software_design.software_design_backend.chat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Message {
    private String user;
    private String roomId;
    private String message;
}
