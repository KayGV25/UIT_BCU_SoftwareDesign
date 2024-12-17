package vn.edu.uit.csbu.software_design.software_design_backend.chat;

import lombok.NoArgsConstructor;

// @Getter
// @Setter
@NoArgsConstructor
public class Message {
    private String user;
    private String roomId;
    private String message;

    public String getUser(){
        return this.user;
    }
    public void setUser(String user){
        this.user = user;
    }
    public String getRoomId(){
        return this.roomId;
    }
    public void setRoomId(String roomId){
        this.roomId = roomId;
    }
    public String getMessage(){
        return this.message;
    }
    public void setMessage(String message){
        this.message = message;
    }
}
