package com.hospital.api_gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "eureka.client.enabled=false",
        "spring.cloud.config.enabled=false"
    }
)
@ActiveProfiles("test")
class ApiGatewayApplicationTests {

	@Test
	void contextLoads() {
		// This test verifies that the application context loads successfully
	}

}
