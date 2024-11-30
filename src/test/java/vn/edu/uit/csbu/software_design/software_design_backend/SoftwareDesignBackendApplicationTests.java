package vn.edu.uit.csbu.software_design.software_design_backend;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import vn.edu.uit.csbu.software_design.software_design_backend.account.accountRepository;
import vn.edu.uit.csbu.software_design.software_design_backend.livestream.LivestreamService;

@SpringBootTest
class SoftwareDesignBackendApplicationTests {

	@Autowired
	accountRepository accountRepository;

	@Autowired
	LivestreamService livestreamService;

	@Test
	void contextLoads() {
	}

	@Test
	public void testExistsByStreamKey() {
        String testStreamKey = "demostream";
        // boolean exists = accountRepository.existsByStreamKey(testStreamKey);
        boolean exists = livestreamService.isValidStreamKey(testStreamKey);
        assertEquals(true, exists);
    }

}
