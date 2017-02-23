package com.trident;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import storm.trident.TridentState;
import storm.trident.TridentTopology;
import storm.trident.operation.builtin.Count;
import storm.trident.operation.builtin.FilterNull;
import storm.trident.operation.builtin.MapGet;
import storm.trident.testing.FixedBatchSpout;
import storm.trident.testing.MemoryMapState;
import trident.test.TopologyBuilder;
import trident.test.Utils;

public class QueryStateTest {
	public static void main(String[] args) {
		TridentTopology topology = new TridentTopology();
		TridentState state = topology.newStream("sentence-spout", new FixedBatchSpout(new Fields("sentence"), 5, 
				new Values("the cow jumped over the moon"),
				new Values("the man went to the store and bought some candy"),
				new Values("four score and seven years ago"),
				new Values("how many apples can you eat")))
			.shuffle()
			.each(new Fields("sentence"), new WordSplitFunction(), new Fields("word"))
			.groupBy(new Fields("word"))
			.persistentAggregate(new MemoryMapState.Factory(), new Count(), new Fields("count"));
		topology.newStream("state-query", new WordSpout())
			.each(new Fields("sentence"), new WordSplitFunction(), new Fields("word"))
			.stateQuery(state, new Fields("word"), new MyMapGet(), new Fields("count"))
			.each(new Fields("count"), new FilterNull())
			.each(new Fields("word", "count"), new Utils.PrintFilter());
		Config config = new Config();
		config.setDebug(false);
		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology("messages-to-operation", config,topology.build());
		
//		Utils.submitTopology(topology.build());
	}
}
