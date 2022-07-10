package io.home4Me.Security.Events.Listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import io.home4Me.Security.Events.LogUserCreated;
import io.home4Me.Security.authentication.dto.LoginDetailsDto;

@Component
public class LogUserCreatedEventListener implements ApplicationListener<LogUserCreated> {

	private static final Logger logger = LogManager.getLogger(LogUserCreatedEventListener.class);
	
	@Override
	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void onApplicationEvent(LogUserCreated event) {
		logger.info(formatUserLog(event));
	}
	
	private String formatUserLog(LogUserCreated event) {
		return String.format("%s - NEW-USER-INSERTED - %s, ", event.getTimestamp(), event.getDetails());
	}

}
