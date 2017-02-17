package com.twitter.graph;

import com.kafka.log.storm.JsonProjectFunction;
import com.twitter.graph.GraphStateFactory.GraphState;
import com.twitter.graph.TitanTools.GraphFactory;

import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;
import storm.kafka.trident.OpaqueTridentKafkaSpout;
import storm.trident.Stream;
import storm.trident.TridentTopology;
import storm.trident.state.StateFactory;
import storm.trident.state.StateUpdater;

public class TwitterGraphTopology {
	public static final String NAME = TwitterGraphTopology.class.getSimpleName();

	public static StormTopology topology() {
		TridentTopology topology = new TridentTopology();

		String zkHosts = "localhost:2188";
		String topic = "log-analysis";
		long startOffsetTime = -2l;
		OpaqueTridentKafkaSpout spout = Kafkas.spout(zkHosts, topic, startOffsetTime);

		String txId = "kafka-stream";
		Stream inputStream = topology.newStream(txId, spout);

		Fields jsonFields = new Fields(LogModel.KEY_TIMESTAMP, LogModel.KEY_MESSAGE);
		Stream parsedStream = inputStream.each(//
				inputStream.getOutputFields(), new JsonProjectFunction(jsonFields), jsonFields);
		parsedStream = parsedStream.project(jsonFields);

		// Trident State
		StateUpdater<GraphState> graphUpdater = new GraphStateUpdater(new TweetGraphTupleProcessor());
		GraphFactory graphFactory = new TitanTools.TitanGraphFactory();
		StateFactory stateFactory = new GraphStateFactory(graphFactory);
		parsedStream.partitionPersist(stateFactory, parsedStream.getOutputFields(), graphUpdater, new Fields());

		return topology.build();
	}
}
