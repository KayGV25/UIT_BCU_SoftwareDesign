package vn.edu.uit.csbu.software_design.software_design_backend.chat;

/**
 * The type Message.
 */
// @Getter
// @Setter
public class Message {
    private String user;
    private String roomId;
    private String message;

    /**
     * Get user string.
     *
     * @return the string
     */
    public String getUser(){
        return this.user;
    }

    /**
     * Set user.
     *
     * @param user the user
     */
    public void setUser(String user){
        this.user = user;
    }

    /**
     * Get room id string.
     *
     * @return the string
     */
    public String getRoomId(){
        return this.roomId;
    }

    /**
     * Set room id.
     *
     * @param roomId the room id
     */
    public void setRoomId(String roomId){
        this.roomId = roomId;
    }

    /**
     * Get message string.
     *
     * @return the string
     */
    public String getMessage(){
        return this.message;
    }

    /**
     * Set message.
     *
     * @param message the message
     */
    public void setMessage(String message){
        this.message = message;
    }

    /**
     * Instantiates a new Message.
     */
    public Message(){
    }
}
