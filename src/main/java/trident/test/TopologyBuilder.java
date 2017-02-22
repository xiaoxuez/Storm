package trident.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;

import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import storm.trident.TridentTopology;
import storm.trident.operation.Aggregator;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.TridentOperationContext;
import storm.trident.operation.builtin.Count;
import storm.trident.tuple.TridentTuple;

public class TopologyBuilder {
	public static StormTopology buildGroupByTopology() throws IOException {
		FakeTweetsBatchSpout spout = new FakeTweetsBatchSpout(5);
		TridentTopology topology = new TridentTopology();
		
		topology.newStream("spout", spout)
			.shuffle()
			.groupBy(new Fields("location"))
			.aggregate(new Fields("location"), new Count(), new Fields("count"))
			.parallelismHint(5)
			.each(new Fields("location", "count"), new Utils.PrintFilter());
		return topology.build();
	}
	
	public static StormTopology buildGroupPartitionAggrateTopology() throws IOException {
		FakeTweetsBatchSpout spout = new FakeTweetsBatchSpout(5);
		TridentTopology topology = new TridentTopology();
		topology.newStream("spout", spout)
			.shuffle()  
            .groupBy(new Fields("location"))              
            .partitionAggregate(new Fields("location"), new MyCount(), new Fields("count"))  
            .toStream()  
            .parallelismHint(2)  
			.each(new Fields("count"), new Utils.PrintFilter());
		return topology.build();
	}
	
	
	public static class MyCount implements Aggregator<Map<String, Integer>> {

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
			// TODO Auto-generated method stub
			return new HashMap<String, Integer>();
		}

		@Override
		public void aggregate(Map<String, Integer> val, TridentTuple tuple, TridentCollector collector) {
			// TODO Auto-generated method stub
			String loc = tuple.getString(0);
			val.put(loc, MapUtils.getInteger(val, loc, 0) + 1);
//			System.out.println("aggrate aggregate + " + val + "tuple = " + tuple);
		}

		@Override
		public void complete(Map<String, Integer> val, TridentCollector collector) {
			// TODO Auto-generated method stub
//			System.out.println("aggrate complete + " + val);
			collector.emit(new Values(val));
		}
		
	}
}
