package com.kafka.log.append;

import ch.qos.logback.classic.spi.ILoggingEvent;

public class MessageFormatter implements Formatter{

	@Override
	public String format(ILoggingEvent event) {
		return event.getFormattedMessage();
	}

}
