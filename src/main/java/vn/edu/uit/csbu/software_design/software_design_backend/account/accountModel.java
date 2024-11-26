package vn.edu.uit.csbu.software_design.software_design_backend.account;

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
@Table(name = "user", schema = "public")
public class accountModel {
    
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "password")
    private String password;

    @Column(name = "\"streamKey\"")
    private String streamKey;

    public accountModel(String name, String password, String streamKey){
        this.name = name;
        this.password = password;
        this.streamKey = streamKey;
    }
}
