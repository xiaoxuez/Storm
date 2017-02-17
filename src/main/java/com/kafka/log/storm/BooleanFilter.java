package com.kafka.log.storm;

import storm.trident.operation.BaseFilter;
import storm.trident.tuple.TridentTuple;

public class BooleanFilter extends BaseFilter{
	 public boolean isKeep(TridentTuple tuple) {
		    return tuple.getBoolean(0);
		  }
}
