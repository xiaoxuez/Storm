package com.twitter.graph;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;

public class TwitterGraphAnalysisApplication {
	public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException {
	    Config conf = new Config();
	    conf.put(TitanTools.storage_backend_key, "cassandra");
	    conf.put(TitanTools.storage_hostname_key, "localhost");

	    if (args.length == 0) {
	      LocalCluster cluster = new LocalCluster();
	      cluster.submitTopology(TwitterGraphTopology.NAME, conf, TwitterGraphTopology.topology());
	    } else {
	      conf.setNumWorkers(3);
	      StormSubmitter.submitTopology(args[0], conf, TwitterGraphTopology.topology());
	    }
	  }
}
