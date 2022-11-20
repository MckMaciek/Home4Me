package io.home4Me.Security.authentication;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
class LogUserCreatedEventListener implements ApplicationListener<LogUserCreatedEvent> {

	private static final Logger logger = LogManager.getLogger(LogUserCreatedEventListener.class);
	
	@Async
	@Override
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void onApplicationEvent(LogUserCreatedEvent event) {
		logger.debug(formatUserLog(event));
	}
	
	private String formatUserLog(LogUserCreatedEvent event) {
		return String.format("%s - NEW-USER-INSERTED - %s, ", event.getTimestamp(), event.getDetails());
	}

}
