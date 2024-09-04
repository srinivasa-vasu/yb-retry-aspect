package io.ysql.RetryAspect.service;

import java.sql.SQLException;

import org.springframework.stereotype.Service;

@Service
//@Component
public class DemoService {

	public String demo() throws SQLException {
		System.out.println("Executing method...");
		// Simulate a failure that requires a retry
		if (Math.random() < 0.7) {
			throw new SQLException("Failed operation.", "08006");
		}
		return "Hello World!";
	}
}
