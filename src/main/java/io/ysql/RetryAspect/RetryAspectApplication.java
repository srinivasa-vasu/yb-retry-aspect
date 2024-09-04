package io.ysql.RetryAspect;

import java.sql.SQLException;

import io.ysql.RetryAspect.service.DemoService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableAspectJAutoProxy
public class RetryAspectApplication {

	public static void main(String[] args) {
		SpringApplication.run(RetryAspectApplication.class, args);
	}

	@Bean
	public RetryTemplate retryTemplate(RetryPolicy todoRetryPolicy) {
		RetryTemplate retryTemplate = new RetryTemplate();
		ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
		backOffPolicy.setMaxInterval(5000); // in seconds (just a reference; needs to be updated as per the need)
		retryTemplate.setRetryPolicy(todoRetryPolicy);
		retryTemplate.setBackOffPolicy(backOffPolicy);
		return retryTemplate;
	}

	@RestController()
	@RequestMapping("/v1")
	public static class DemoController {
		private final DemoService demoService;

		DemoController(DemoService demoService) {
			this.demoService = demoService;
		}

		@GetMapping("/demo")
		String demo() throws SQLException {
			return demoService.demo();
		}
	}

}
