package com.partition;

import java.util.Map;

import com.broad.game.DefaultCoordinator;

import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import storm.trident.operation.TridentCollector;
import storm.trident.spout.ITridentSpout;
import storm.trident.topology.TransactionAttempt;

public class WordSpout implements ITridentSpout<Long>{

	@Override
	public storm.trident.spout.ITridentSpout.BatchCoordinator<Long> getCoordinator(String txStateId, Map conf,
			TopologyContext context) {
		// TODO Auto-generated method stub
		return new DefaultCoordinator();
	}

	@Override
	public storm.trident.spout.ITridentSpout.Emitter<Long> getEmitter(String txStateId, Map conf,
			TopologyContext context) {
		return new WordEmitter();
	}

	@Override
	public Map getComponentConfiguration() {
		return null;
	}

	@Override
	public Fields getOutputFields() {
		return new Fields("sentence");
	}
	
	
	public static class WordEmitter implements Emitter<Long> {

		private String[] sentences = {
				"my dog has fleas",
				"i like cold beverages",
				"the dog ate my homework",
				"dont't have a cow man",
				"i don't think i like fleas",
		};
		
		@Override
		public void emitBatch(TransactionAttempt tx, Long coordinatorMeta, TridentCollector collector) {
			// TODO Auto-generated method stub
			for (String sentence : sentences) {
				collector.emit(new Values(sentence));
			}
		}

		@Override
		public void success(TransactionAttempt tx) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void close() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
}
