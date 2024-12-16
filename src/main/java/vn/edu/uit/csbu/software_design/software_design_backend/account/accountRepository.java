package vn.edu.uit.csbu.software_design.software_design_backend.account;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface accountRepository extends JpaRepository<accountModel, String>{
    Optional<accountModel> findByName(String name);
    Optional<accountModel> findByStreamKey(String streamey);
    Boolean existsByStreamKey(String streamKey);
    Boolean existsByName(String name);
    void deleteByName(String name);
}
