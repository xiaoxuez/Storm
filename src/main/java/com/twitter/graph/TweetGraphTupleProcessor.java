package com.twitter.graph;

import java.util.HashMap;
import java.util.Map;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

public class TweetGraphTupleProcessor implements GraphTupleProcessor {
	private static final long serialVersionUID = -7713899748427900687L;

	@Override
	public void process(Graph graph, TridentTuple tuple, TridentCollector collector) {
		Long timestamp = tuple.getLong(0);
		// SAMPLE:
		// {'name': 'Silver Shoes', 'text': 'RT @KirkKus: Indirect cost of the
		// UK being in the EU is
		// estimated to be costing Britain \xc2\xa3170 billion per year!
		// #BetterOffOut #UKIP',
		// 'hashtags': ['BetterOffOut', 'UKIP'], 'location': 'Coningsby, Lincs',
		// 'urls': [], 'mentions':
		// ['KirkKus'], 'user': 'vienesewaltz'}
		JSONObject json = (JSONObject) tuple.get(1);
		Vertex user = findOrCreateUser(graph, (String) json.get("user"), (String) json.get("name"));

		JSONArray hashtags = (JSONArray) json.get("hashtags");

		for (int i = 0; i < hashtags.size(); i++) {
			Vertex v = findOrCreateHashtag(graph, ((String) hashtags.get(i)).toLowerCase());
			createEdgeAtTime(graph, user, v, "mentions", timestamp);
		}
	}

	// 查找或者创建用户节点
	private Vertex findOrCreateUser(Graph graph, String userScreenName, String userName) {
		Vertex found = TitanTools.findV(graph, TweetGraphModel.V_USER, userScreenName);
		if (found == null) {
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put(TweetGraphModel.V_USER_USER, userScreenName);
			properties.put(TweetGraphModel.V_USER_NAME, userName);
			found = TitanTools.createV(graph, TweetGraphModel.V_USER, properties);
		}
		return found;
	}

	// 创建Hashtag节点
	private Vertex findOrCreateHashtag(Graph graph, String text) {
		Vertex found = TitanTools.findV(graph, TweetGraphModel.V_HASHTAG_TEXT, text);
		if (found == null) {
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put(TweetGraphModel.V_HASHTAG_TEXT, text);
			found = TitanTools.createV(graph, TweetGraphModel.V_HASHTAG, properties);
		}
		return found;
	}

	// 创建用户提到Hashtag边
	private void createEdgeAtTime(Graph graph, Vertex user, Vertex v, String eName, Long timestamp) {
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(TweetGraphModel.E_MENTIONS_HASHTAG_TIMESTAMP, timestamp);
		TitanTools.createE(graph, TweetGraphModel.E_MENTIONS_HASHTAG, user, v, properties);
	}

}
