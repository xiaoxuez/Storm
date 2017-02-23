package trident.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.generated.StormTopology;
import storm.trident.operation.Filter;
import storm.trident.operation.TridentOperationContext;
import storm.trident.tuple.TridentTuple;

public class Utils {

	/**
	 * A filter that filters nothing but prints the tuples it sees. Useful to
	 * test and debug things.
	 */
	@SuppressWarnings({ "serial", "rawtypes" })
	public static class PrintFilter implements Filter {

		TridentOperationContext context;
		@Override
		public void prepare(Map conf, TridentOperationContext context) {
			this.context = context;
		}

		@Override
		public void cleanup() {
		}

		@Override
		public boolean isKeep(TridentTuple tuple) {
			
			System.out.println("**result: " + tuple +"partition = " + this.context.getPartitionIndex() + "num = " + this.context.numPartitions());
			return true;
		}
	}

	/**
	 * Given a hashmap with string keys and integer counts, returns the "top"
	 * map of it. "n" specifies the size of the top to return.
	 */
	public final static Map<String, Integer> getTopNOfMap(Map<String, Integer> map, int n) {
		List<Map.Entry<String, Integer>> entryList = new ArrayList<Map.Entry<String, Integer>>(map.size());
		entryList.addAll(map.entrySet());
		Collections.sort(entryList, new Comparator<Map.Entry<String, Integer>>() {

			@Override
			public int compare(Entry<String, Integer> arg0, Entry<String, Integer> arg1) {
				return arg1.getValue().compareTo(arg0.getValue());
			}
		});
		Map<String, Integer> toReturn = new HashMap<String, Integer>();
		for (Map.Entry<String, Integer> entry : entryList.subList(0, Math.min(entryList.size(), n))) {
			toReturn.put(entry.getKey(), entry.getValue());
		}
		return toReturn;
	}
	
	
	
	public static void submitTopology(StormTopology topology, String name) {
		submitTopology(topology, name, false);
	}
	
	public static void submitTopology(StormTopology topology) {
		submitTopology(topology, "messages-to-operation", true);
	}
	
	public static void submitTopology(StormTopology topology, String name, boolean isLocal) {
		Config config = new Config();
		config.setDebug(false);
		if(isLocal) {
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("messages-to-operation", config,topology);
		} else {
			try {
				StormSubmitter.submitTopologyWithProgressBar(name, config, topology);
			} catch (AlreadyAliveException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidTopologyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
