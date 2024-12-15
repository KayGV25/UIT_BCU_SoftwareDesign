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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.uit.csbu.software_design.software_design_backend.following.followingModel;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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

    public List<String> getFollowingStreamId(){
        List<String> followingStreamId = new ArrayList<>();
        for(followingModel f : following) {
            followingStreamId.add(f.getStreamerId());
        }
        return followingStreamId;
    }

    public accountModel(String name, String password, String streamKey){
        this.name = name;
        this.password = password;
        this.streamKey = streamKey;
    }

    public accountModel(String uuid, String name, String password, String streamKey){
        this.id = uuid;
        this.name = name;
        this.password = password;
        this.streamKey = streamKey;
    }
}
