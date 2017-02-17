package com.kafka.log.storm;

import java.util.Date;

import storm.trident.tuple.TridentTuple;

public class NotifyMessageMapper implements MessageMapper{
	 public String toMessageBody(TridentTuple tuple) {
		    StringBuilder sb = new StringBuilder();
		    sb.append("On " + new Date(tuple.getLongByField("timestamp")) + " ");
		    sb.append("the application \"" + tuple.getStringByField("logger") + "\" ");
		    sb.append("changed alert state based on a threshold of " + tuple.getDoubleByField("threshold") + ".\n");
		    sb.append("The last value was " + tuple.getDoubleByField("average") + "\n");
		    sb.append("The last message was \"" + tuple.getStringByField("message") + "\"");
		    return sb.toString();
		  }
}
