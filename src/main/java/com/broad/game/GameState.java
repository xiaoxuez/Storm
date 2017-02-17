package com.broad.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameState implements Serializable {
	private static final long serialVersionUID = 1L;
	private Board board;
	/**
	 * history变量： 记录下游戏树中当前节点的所有祖先节点的棋盘状态，游戏树上这个记录下来的路径，在使用叶子节点的分值对祖先分值进行更新时会用到
	 */
	private List<Board> history;
	private String player;
	static private Random generator = new Random();

	public GameState(Board board, List<Board> history, String player) {
		this.board = board;
		this.history = history;
		this.player = player;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("GAME [");
		sb.append(board.toKey()).append("]");
		sb.append(": player(").append(player).append(")\n");
		sb.append("   history [");
		for (Board b : history) {
			sb.append(b.toKey()).append(",");
		}
		sb.append("]");
		return sb.toString();
	}

	public Board getBoard() {
		return board;
	}

	public List<Board> getHistory() {
		return history;
	}

	public String getPlayer() {
		return player;
	}

	/**
	 * 从当前的棋盘状态，获得所有下一步的可能，从中随机选取一步走，直到EndState
	 * @param currentBoard
	 * @param player
	 * @return 
	 */
	public static GameState playAtRandom(Board currentBoard, String player) {
		List<Board> history = new ArrayList<Board>();
		while (!currentBoard.isEndState()) {
			List<Board> boards = currentBoard.nextBoards(player);
			/**
			 * 产生一个小于boards.size()的随机数
			 */
			int move = generator.nextInt(boards.size());

			history.add(currentBoard);
			player = Player.next(player);
			currentBoard = boards.get(move);
		}
		return new GameState(currentBoard, history, player);
	}

	public int score() {
		return this.board.score(this.player);
	}
}
