package vn.edu.uit.csbu.software_design.software_design_backend.following;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "following", schema = "public")
public class followingModel {
    @Id
    @Column(name = "account_id")
    private String accountId;

    @Column(name = "streamer_id")
    private String streamerId;
}
