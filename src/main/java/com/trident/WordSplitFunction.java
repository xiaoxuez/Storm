package com.trident;

import java.util.Map;

import backtype.storm.tuple.Values;
import storm.trident.operation.Function;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.TridentOperationContext;
import storm.trident.tuple.TridentTuple;

public class WordSplitFunction implements Function{

	@Override
	public void prepare(Map conf, TridentOperationContext context) {
		
	}

	@Override
	public void cleanup() {
		
	}

	@Override
	public void execute(TridentTuple tuple, TridentCollector collector) {
		String sentence = tuple.getStringByField("sentence");
		String[] words = sentence.split(" ");
		for (String word : words) {
			collector.emit(new Values(word));
		}
	}

}
