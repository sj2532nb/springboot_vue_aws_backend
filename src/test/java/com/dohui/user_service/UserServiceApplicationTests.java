package com.dohui.user_service;

import com.dohui.user_service.domain.post.service.S3Service;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceApplicationTests {
	@MockBean
	private S3Service s3Service;

	@Test
	void contextLoads() {
	}

}
