package com.broad.game;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;
import storm.trident.Stream;
import storm.trident.TridentTopology;

/**
 * 检视 RecursiveTopology 用来向下递归游戏树
 * @author xiaoxuez
 *
 */
public class RecursiveTopology {
private static final Logger LOG = LoggerFactory.getLogger(RecursiveTopology.class);
	
	public static StormTopology buildTopology(){
		
		TridentTopology topology =new TridentTopology();
		
		//work queue spout
		LocalQueueEmitter<GameState> workSpoutEmitter = new LocalQueueEmitter<GameState>("WorkQueue");
		LocalQueueSpout<GameState> workSpout = new LocalQueueSpout<GameState>(workSpoutEmitter);
		GameState initialState = new GameState(new Board(), new ArrayList<Board>(),"X");
		workSpoutEmitter.enqueue(initialState);
		
		//scring queue spout
		LocalQueueEmitter<GameState> scoringSpoutEmitter = new LocalQueueEmitter<GameState>("ScoringQueue");
		
		Stream inputStream = topology.newStream("gamestate",workSpout);
		/**
		 * Q:
		 * 数据流被分成两支，第一个分支的数据被过滤，只留下游戏结束的棋盘状态，这些数据会传递到Scoring Queue中，第二个分支的数据用来生成新的棋盘状态，把所有后代的节点都写入Worker Queue中
		 * 这两个分支的数据流一样么？
		 */
		inputStream.each(new Fields("gamestate"),new isEndGame())
				   .each(new Fields("gamestate"),
						 new LocalQueuerFunction<GameState>(scoringSpoutEmitter), 
						 new Fields(""));
		/**
		 * GenerateBoards 和 isEndGame或者LocalQueuerFunction的执行先后是？
		 */
		inputStream.each(new Fields("gamestate"), new GenerateBoards(), new Fields("children"))
		           .each(new Fields("children"), new LocalQueuerFunction<GameState>(workSpoutEmitter), new Fields());
		
		return topology.build();
		
	}
	
	public static void main(String[] args) throws Exception{
		final Config conf = new Config();
		final LocalCluster cluster = new LocalCluster();
		
		LOG.info("submitting topology.");
		cluster.submitTopology("recursiveTopology", conf, RecursiveTopology.buildTopology());
		LOG.info("Topology submitted.");
		Thread.sleep(600000);
	}
}
