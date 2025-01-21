package vn.edu.uit.csbu.software_design.software_design_backend.following;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The interface Following repository.
 */
@Repository
public interface followingRepository extends JpaRepository<followingModel, String>{
    /**
     * Exists by streamer id boolean.
     *
     * @param StreamId the stream id
     * @return the boolean
     */
    Boolean existsByAccountIdAndStreamerId(String accountId, String StreamId);

    /**
     * Delete all by account id.
     *
     * @param streamerId the streamer id
     */
    void deleteAllByAccountId(String streamerId);
}