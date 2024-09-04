package io.ysql.RetryAspect;

import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.sql.SQLTransientConnectionException;

import jakarta.annotation.PostConstruct;

import org.springframework.dao.TransientDataAccessException;
import org.springframework.retry.policy.ExceptionClassifierRetryPolicy;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.stereotype.Component;

@Component
public class RetryPolicy extends ExceptionClassifierRetryPolicy {

	// 40001 - optimistic concurrency or serialization_failure
	// 40P01 - deadlock
	// 08006 - connection issues (error sending data back);"kill -9" failures, socket timeouts
	// 57P01 - broken pool conn (invalidated connections because of node-failure, node-restart, etc.);"kill -15" failures
	// XX000 - RPC failures (could be intermittent)
	private final String SQL_STATE = "^(40001)|(40P01)|(57P01)|(08006)|(XX000)";

	// oom-killer or a process crash in the middle of a tx
	private final String SQL_MSG = "^(connection is closed)";

	private final org.springframework.retry.RetryPolicy sp = new SimpleRetryPolicy(5);

	private final org.springframework.retry.RetryPolicy np = new NeverRetryPolicy();

	@PostConstruct
	public void init() {
		this.setExceptionClassifier(cause -> {
			do {
				// SQLTransientConnectionException: (08001)|(08003)
				// - intermittent issues because of a backend failure like connection refused
				// - hikari connection time out
				// - 08001, 08003 - connection does not exist (pool connection timeout)
				if (cause instanceof SQLRecoverableException || cause instanceof SQLTransientConnectionException ||
						cause instanceof TransientDataAccessException) {
					return sp;
				}
				else if (cause instanceof SQLException exception) {
					if ((exception.getSQLState() != null && exception.getSQLState().matches(SQL_STATE))
							|| (exception.getMessage() != null
							&& exception.getMessage().toLowerCase().matches(SQL_MSG))) {
						return sp;
					}
				}
				cause = cause.getCause();
			}
			while (cause != null);
			return np;
		});
	}

}
