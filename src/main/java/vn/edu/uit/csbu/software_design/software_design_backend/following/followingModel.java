package vn.edu.uit.csbu.software_design.software_design_backend.following;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "following", schema = "public")
public class followingModel {
    @Id
    @Column(name = "account_id")
    private String accountId;

    @Column(name = "streamer_id")
    private String streamerId;

    public String getAccountId() {
        return accountId;
    }
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
    public String getStreamerId() {
        return streamerId;
    }
    public void setStreamerId(String streamerId) {
        this.streamerId = streamerId;
    }

    public followingModel() { 

    }



    public followingModel(String accountId, String streamerId) {
        this.accountId = accountId;
        this.streamerId = streamerId;
    }

}
