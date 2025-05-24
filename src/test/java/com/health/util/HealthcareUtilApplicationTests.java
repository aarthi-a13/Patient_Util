/*
package com.health.util;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.mockito.Mockito;
import com.health.util.kafka.producer.EventProducerService;

@SpringBootTest
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
class HealthcareUtilApplicationTests {

	@TestConfiguration
	static class TestConfig {
		@Bean
		public EventProducerService eventProducerService() {
			return Mockito.mock(EventProducerService.class);
		}
	}

	@Test
	void contextLoads() {
	}

}
*/
