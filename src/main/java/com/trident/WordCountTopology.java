package com.trident;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.tuple.Fields;
import storm.trident.Stream;
import storm.trident.TridentState;
import storm.trident.TridentTopology;
import storm.trident.operation.builtin.Count;
import storm.trident.operation.builtin.MapGet;
import storm.trident.testing.MemoryMapState;
import trident.test.Utils;

public class WordCountTopology {
	
	public static TridentState getWordCountState() {
		TridentTopology  topology = new TridentTopology();
		TridentState state = topology.newStream("sentence-spout", new WordSpout())
			.parallelismHint(3)
			.each(new Fields("sentence"), new WordSplitFunction(), new Fields("word"))
			.shuffle()
			.groupBy(new Fields("word"))
			.persistentAggregate(new MemoryMapState.Factory(), new Count(), new Fields("count"));
		return state;
	}
	public static void main(String[] args) {
		TridentTopology topology = new TridentTopology();
		topology.newStream("sentence-spout", new WordSpout())
			.shuffle()
			.each(new Fields("sentence"), new WordSplitFunction(), new Fields("word"))
//			.partitionBy(new Fields("word"))
//			.partitionAggregate(new Fields("word"), new CountCombinerAggregator(), new Fields("count"))
//			.parallelismHint(3)
			.groupBy(new Fields("word"))
			.aggregate(new Fields("word"), new Count(), new Fields("count"))
//			.partitionAggregate(new Fields("word"), new CountAggragator(), new Fields("count"))
//			.toStream()
			.each(new Fields("word", "count"), new Utils.PrintFilter());
		Config config = new Config();
		config.setDebug(false);
		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology("messages-to-operation", config,topology.build());
		/**
		 * partitionBy + partitionAggregate + parallelismHint(3)
		 * 结果为：("count"), tuple只有这一个字段。字段值的类型是Map.partition为分区，num为分区总数。
			[{think=1, ate=1, don't=1, cow=1, my=2, dog=2}]partition = 0num = 3
			[{a=1, like=2, cold=1, has=1, man=1, fleas=2}]partition = 2num = 3
			[{the=1, beverages=1, homework=1, have=1, i=3, dont't=1}]partition = 1num = 3
		 *  
		 * groupBy + aggregate
		 * 结果是：("word", "count"),因为是按单词进行分组，所以结果为单词和出现的次数
			[a, 1] 
			[think, 1]
			[like, 2] 
			...

		 */
		
		/**
		 * 1. 聚合出来的Steam中只有一个字段"count",没有其余以前的字段。
		 * 所以需要保存聚合结果，以简单数据类型保存的话，肯定就会混淆。此处使用的是Map.  
			2. 最开始理解partitionBy、groupBy、aggregate、partitionAggregate的时候进入了一个误区，
			就是将他们几个结合起来理解，其实应该直接按照各自的功能进行单独理解，partitionBy就是分区，
			将tuples按照指定字段的值分区，groupBy是分组，返回值是GroupedStream，可视为Stream的分组集合，
			主要作用是其后进行聚合的情况下，是在各个小组内分别进行的聚合，
			aggregate和partitionAggregate都是聚合，不同是aggregate有隐含的分区操作。
		 */
	}
}
