package vn.edu.uit.csbu.software_design.software_design_backend.following;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * The type Following model.
 */
@Entity
@Table(name = "following", schema = "public")
public class followingModel {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Integer id;

    @Column(name = "account_id")
    private String accountId;

    @Column(name = "streamer_id")
    private String streamerId;

    /**
     * Gets account id.
     *
     * @return the account id
     */
    public String getAccountId() {
        return accountId;
    }

    /**
     * Sets account id.
     *
     * @param accountId the account id
     */
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    /**
     * Gets streamer id.
     *
     * @return the streamer id
     */
    public String getStreamerId() {
        return streamerId;
    }

    /**
     * Sets streamer id.
     *
     * @param streamerId the streamer id
     */
    public void setStreamerId(String streamerId) {
        this.streamerId = streamerId;
    }

    /**
     * Instantiates a new Following model.
     */
    public followingModel() {

    }


    /**
     * Instantiates a new Following model.
     *
     * @param accountId  the account id
     * @param streamerId the streamer id
     */
    public followingModel(String accountId, String streamerId) {
        this.accountId = accountId;
        this.streamerId = streamerId;
    }

}
