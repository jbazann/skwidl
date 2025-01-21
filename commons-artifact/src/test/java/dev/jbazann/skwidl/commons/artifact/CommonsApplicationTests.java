package dev.jbazann.skwidl.commons.artifact;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(classes = {CommonsApplication.class}) // TODO fix testing with main application context
@Import(CommonsTestConfiguration.class)
class CommonsApplicationTests {

	private final WebTestClient webClient;

	@Autowired
	CommonsApplicationTests(WebTestClient webClient) {
		this.webClient = webClient;
	}

	@Test
	void contextLoads() {
	}



}
