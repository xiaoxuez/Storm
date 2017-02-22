package com.partition;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;

import backtype.storm.tuple.Values;
import storm.trident.operation.Aggregator;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.TridentOperationContext;
import storm.trident.tuple.TridentTuple;

public class CountAggragator implements Aggregator<Map<String, Integer>>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void prepare(Map conf, TridentOperationContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, Integer> init(Object batchId, TridentCollector collector) {
		return new HashMap<String, Integer>();
	}

	@Override
	public void aggregate(Map<String, Integer> val, TridentTuple tuple, TridentCollector collector) {
		// TODO Auto-generated method stub
		String loc = tuple.getString(0);
		val.put(loc, MapUtils.getInteger(val, loc, 0) + 1);
	}

	@Override
	public void complete(Map<String, Integer> val, TridentCollector collector) {
		// TODO Auto-generated method stub
		collector.emit(new Values(val));
	}

}
