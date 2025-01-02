package vn.edu.uit.csbu.software_design.software_design_backend.account;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import vn.edu.uit.csbu.software_design.software_design_backend.following.followingModel;

/**
 * The type Account model.
 */
@Entity
@AllArgsConstructor
@Table(name = "user", schema = "public")
public class accountModel {
    
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "password")
    @JsonIgnore
    private String password;

    @Column(name = "\"streamKey\"")
    @JsonIgnore
    private String streamKey;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "accountId")
    private Set<followingModel> following;

    @OneToMany(mappedBy = "streamerId")
    private Set<followingModel> follower;

    /**
     * Get id string.
     *
     * @return the string
     */
    public String getId(){
        return this.id;
    }

    /**
     * Get name string.
     *
     * @return the string
     */
    public String getName(){
        return this.name;
    }

    /**
     * Get password string.
     *
     * @return the string
     */
    public String getPassword(){
        return this.password;
    }

    /**
     * Get stream key string.
     *
     * @return the string
     */
    public String getStreamKey(){
        return this.streamKey;
    }

    /**
     * Get title string.
     *
     * @return the string
     */
    public String getTitle(){
        return this.title;
    }

    /**
     * Get description string.
     *
     * @return the string
     */
    public String getDescription(){
        return this.description;
    }

    /**
     * Set id.
     *
     * @param id the id
     */
    public void setId(String id){
        this.id = id;
    }

    /**
     * Set name.
     *
     * @param name the name
     */
    public void setName(String name){
        this.name = name;
    }

    /**
     * Set password.
     *
     * @param password the password
     */
    public void setPassword(String password){
        this.password = password;
    }

    /**
     * Set stream key.
     *
     * @param streamKey the stream key
     */
    public void setStreamKey(String streamKey){
        this.streamKey = streamKey;
    }

    /**
     * Set title.
     *
     * @param title the title
     */
    public void setTitle(String title){
        this.title = title;
    }

    /**
     * Set description.
     *
     * @param description the description
     */
    public void setDescription(String description){
        this.description = description;
    }

    /**
     * Get following stream id list.
     *
     * @return the list
     */
    @JsonIgnore
    public List<String> getFollowingStreamId(){
        List<String> followingStreamId = new ArrayList<>();
        for(followingModel f : following) {
            followingStreamId.add(f.getStreamerId());
        }
        return followingStreamId;
    }

    /**
     * The function `getFollowerCount` returns the size of a collection named `follower` while being
     * ignored during serialization.
     * 
     * @return The `follower.size()` is being returned, which represents the number of elements in the
     * `follower` collection.
     */
    @JsonIgnore
    public Integer getFollowerCount(){
        return follower.size();
    }

    /**
     * Instantiates a new Account model.
     */
    public accountModel(){

    }

    /**
     * Instantiates a new Account model.
     *
     * @param name      the name
     * @param password  the password
     * @param streamKey the stream key
     */
    public accountModel(String name, String password, String streamKey){
        this.name = name;
        this.password = password;
        this.streamKey = streamKey;
    }

    /**
     * Instantiates a new Account model.
     *
     * @param uuid      the uuid
     * @param name      the name
     * @param password  the password
     * @param streamKey the stream key
     */
    public accountModel(String uuid, String name, String password, String streamKey){
        this.id = uuid;
        this.name = name;
        this.password = password;
        this.streamKey = streamKey;
    }
}
