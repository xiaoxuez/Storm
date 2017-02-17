package com.kafka.log.append;

import ch.qos.logback.classic.spi.ILoggingEvent;

public interface Formatter {
	String format(ILoggingEvent event);
}
