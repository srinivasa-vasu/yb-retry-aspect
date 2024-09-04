package io.ysql.RetryAspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RetryAspectConfig {

	private final RetryTemplate retryTemplate;

	RetryAspectConfig(RetryTemplate retryTemplate) {
		this.retryTemplate = retryTemplate;
	}

	@Around("!within(io.ysql.RetryAspect.RetryPolicy) && (@within(org.springframework.stereotype.Service) || @within(org.springframework.stereotype.Component))")
	public Object retryStereotype(ProceedingJoinPoint joinPoint) throws Throwable {
		return retryTemplate.execute(context -> joinPoint.proceed());
	}

//	@Around("!within(io.ysql.RetryAspect.RetryPolicy) && execution(public * io.ysql.RetryAspect.service..*(..))")
//	public Object retryMethod(ProceedingJoinPoint joinPoint) throws Throwable {
//		return retryTemplate.execute(context -> joinPoint.proceed());
//	}
}