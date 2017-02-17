package com.broad.game;

import java.util.ArrayList;
import java.util.List;

import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

/**
 * 对当前棋盘状态计算分值，然后将分值发送给它所有历史记录中的棋盘状态
 * @author xiaoxuez
 *
 */
public class ScoreFunction extends BaseFunction{
	private static final long serialVersionUID = 1L;

	public void execute(TridentTuple tuple, TridentCollector collector) {
		GameState gameState = (GameState) tuple.get(0);
		String player = gameState.getPlayer();
		int score = gameState.score();

		List<Object> values = new ArrayList<Object>();
		values.add(gameState.getBoard());
		values.add(score);
		values.add(player);
		collector.emit(values);

		for (Board b : gameState.getHistory()) {
			player = Player.next(player);
			values = new ArrayList<Object>();
			values.add(b);
			values.add(score);
			values.add(player);
			collector.emit(values);
		}
	}
}
