package vn.edu.uit.csbu.software_design.software_design_backend.following;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface followingRepository extends JpaRepository<followingModel, String>{
    Boolean existsByStreamerId(String StreamId);
    void deleteAllByAccountId(String streamerId);
}