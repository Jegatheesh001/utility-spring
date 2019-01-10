package com.myweb.utility.aspects;

import java.util.Arrays;
import java.util.Collection;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * This Aspect will log request and response and total process time
 * 
 * @author Jegatheesh <br>
 *         <b>Created</b> On Sep 3, 2018
 *
 */

@Component
@Aspect
public class LoggingAspect {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoggingAspect.class);

	@Pointcut("@annotation(Loggable)")
	public void executeLogging() {

	}

	@Around("executeLogging()")
	public Object logMethodCall(ProceedingJoinPoint joinPoint) throws Throwable {
		// Setting Basic Details
		StringBuilder logInfo = new StringBuilder("Method: ");
		logInfo.append(joinPoint.getSignature().getName());
		logInfo.append(" | Class: ").append(joinPoint.getSignature().getDeclaringType().getName());
		// Setting Request Parameters
		Object[] args = joinPoint.getArgs();
		if (args != null && args.length > 0) {
			logInfo.append(" | Requests ");
			Arrays.asList(args).forEach(arg -> {
				logInfo.append("[").append(arg).append("]");
			});
		}
		// Setting Total Time of Execution
		Object returnValue = null;
		long startTime = System.currentTimeMillis();
		try {
			returnValue = joinPoint.proceed();
		} catch (Throwable e) {
			logInfo.append(" | Total time ").append(System.currentTimeMillis() - startTime).append("ms ");
			logInfo.append(" | Exception: ").append(e.getMessage());
			LOGGER.info(logInfo.toString());
			throw e;
		}
		logInfo.append(" | Total time ").append(System.currentTimeMillis() - startTime).append("ms ");
		// Setting Response
		if (returnValue != null) {
			if (returnValue instanceof Collection) {
				logInfo.append(" | Response [" + ((Collection<?>) returnValue).size() + " records]");
			} else {
				logInfo.append(" | Response [" + returnValue + "]");
			}
		}
		LOGGER.info(logInfo.toString());
		return returnValue;
	}

}
