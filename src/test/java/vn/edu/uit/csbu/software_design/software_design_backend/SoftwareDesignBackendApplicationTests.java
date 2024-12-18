package vn.edu.uit.csbu.software_design.software_design_backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import vn.edu.uit.csbu.software_design.software_design_backend.account.accountRepository;
import vn.edu.uit.csbu.software_design.software_design_backend.livestream.LivestreamService;

/**
 * The type Software design backend application tests.
 */
@SpringBootTest
class SoftwareDesignBackendApplicationTests {

    /**
     * The Account repository.
     */
    @Autowired
	accountRepository accountRepository;

    /**
     * The Livestream service.
     */
    @Autowired
	LivestreamService livestreamService;

    /**
     * Context loads.
     */
    @Test
	void contextLoads() {
	}
}
