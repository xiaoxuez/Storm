package com.broad.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.LocalDRPC;
import backtype.storm.StormSubmitter;
import backtype.storm.tuple.Fields;
import backtype.storm.utils.DRPCClient;
import storm.trident.TridentTopology;

public class DrpcTopology {
	private static final Logger LOG = LoggerFactory.getLogger(DrpcTopology.class);

	public static void main(String[] args) throws Exception {
//		final LocalCluster cluster = new LocalCluster();
		final Config conf = new Config();

//		LocalDRPC client = new LocalDRPC();
//		DRPCClient client = new DRPCClient("localhost", 3772);
		TridentTopology drpcTopology = new TridentTopology();

		drpcTopology.newDRPCStream("drpc").each(new Fields("args"), new ArgsFunction(), new Fields("gamestate"))
				.each(new Fields("gamestate"), new GenerateBoards(), new Fields("children"))
				.each(new Fields("children"), new ScoreFunction(), new Fields("board", "score", "player"))
				.groupBy(new Fields("gamestate"))
				.aggregate(new Fields("board", "score"), new FindBestMove(), new Fields("bestMove"))
				.project(new Fields("bestMove"));
		StormSubmitter.submitTopology("drpc-test", conf, drpcTopology.build());

//		cluster.submitTopology("drpcTopology", conf, drpcTopology.build());

//		Board board = new Board();
//		board.board[1][1] = "O";
//		board.board[2][2] = "X";
//		board.board[0][1] = "O";
//		board.board[0][0] = "X";
//		LOG.info("Determing best move for O on:" + board.toString());
//		LOG.info("RECEIVED RESPONSE [" + client.execute("drpc", board.toKey()) + "]");
	}
}
