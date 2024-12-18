package vn.edu.uit.csbu.software_design.software_design_backend.account;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The interface Account repository.
 */
@Repository
public interface accountRepository extends JpaRepository<accountModel, String>{
    /**
     * Find by name optional.
     *
     * @param name the name
     * @return the optional
     */
    Optional<accountModel> findByName(String name);

    /**
     * Find by stream key optional.
     *
     * @param streamey the streamey
     * @return the optional
     */
    Optional<accountModel> findByStreamKey(String streamey);

    /**
     * Exists by stream key boolean.
     *
     * @param streamKey the stream key
     * @return the boolean
     */
    Boolean existsByStreamKey(String streamKey);

    /**
     * Exists by name boolean.
     *
     * @param name the name
     * @return the boolean
     */
    Boolean existsByName(String name);

    /**
     * Delete by name.
     *
     * @param name the name
     */
    void deleteByName(String name);
}
