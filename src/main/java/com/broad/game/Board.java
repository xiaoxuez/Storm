package com.broad.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Board implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String EMPTY = " ";
	/**
	 * 棋谱，棋谱中棋子为String类型，二人对棋则棋子的种类为2种
	 */
	public String[][] board = { { EMPTY, EMPTY, EMPTY }, { EMPTY, EMPTY, EMPTY }, { EMPTY, EMPTY, EMPTY } };

	public Board () {
		
	}
	/**
	 *根据  key 通过charAt将每个字符组成3*3的棋谱。
	 * @param key
	 */
	public Board(String key) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				this.board[i][j] = "" + key.charAt(i * 3 + j);
			}
		}
	}
	/**
	 * 循环遍历当前3*3棋谱，当中有空格的地方，就复制出一份棋谱，并且将其中空的那一格置为player，随后将复制出来的棋谱放入List中
	 * 从节点的意义上来说，这一步的意义是寻找所有的子节点
	 * @param player
	 * @return List
	 */
	public List<Board> nextBoards(String player) {
        List<Board> boards = new ArrayList<Board>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j].equals(EMPTY)) {
                    Board newBoard = this.clone();
                    newBoard.board[i][j] = player;
                    boards.add(newBoard);
                }
            }
        }
        return boards;
    }

	/**
	 * 判断方法是查看是否有子节点或者分数是否>1000
	 * @return
	 */
    public boolean isEndState() {
        return (nextBoards("X").size() == 0 || Math.abs(score("X")) > 1000);
    }
    
    public int score(String player) {
        return scoreLines(player) - scoreLines(Player.next(player));
    }

    /**
     * 根据棋盘算分，规则是分别列，行，斜遍历，如果某列/行/斜/有一颗来自player的棋子 +10，两颗 + 100， 三颗 +10000
     * @param player
     * @return
     */
    public int scoreLines(String player) {
        int score = 0;
        // Columns
        score += scoreLine(board[0][0], board[1][0], board[2][0], player);
        score += scoreLine(board[0][1], board[1][1], board[2][1], player);
        score += scoreLine(board[0][2], board[1][2], board[2][2], player);

        // Rows
        score += scoreLine(board[0][0], board[0][1], board[0][2], player);
        score += scoreLine(board[1][0], board[1][1], board[1][2], player);
        score += scoreLine(board[2][0], board[2][1], board[2][2], player);

        // Diagonals
        score += scoreLine(board[0][0], board[1][1], board[2][2], player);
        score += scoreLine(board[2][0], board[1][1], board[0][2], player);
        return score;
    }

    public int scoreLine(String pos1, String pos2, String pos3, String player) {
        int score = 0;
        if (pos1.equals(player) && pos2.equals(player) && pos3.equals(player)) {
            score = 10000;
        } else if (pos1.equals(player) && pos2.equals(player) && pos3.equals(EMPTY) ||
                pos1.equals(EMPTY) && pos2.equals(player) && pos3.equals(player)) {
            score = 100;
        } else {
            if (pos1.equals(player) && pos2.equals(EMPTY) && pos3.equals(EMPTY) ||
                    pos1.equals(EMPTY) && pos2.equals(player) && pos3.equals(EMPTY) ||
                    pos1.equals(EMPTY) && pos2.equals(EMPTY) && pos3.equals(player)) {
                score = 10;
            }
        }
        return score;
    }

    public Board clone() {
        Board clone = new Board();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                clone.board[i][j] = this.board[i][j];
            }
        }
        return clone;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("\n---------\n");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                sb.append("|").append(board[i][j]).append("|");
            }
            sb.append("\n---------\n");
        }
        return sb.toString();
    }

    /**
     * 这个key唯一地表示了一个棋盘状态，是我们的持久化存储机制中使用的唯一标识符，在这个例子中，唯一标识符是简单地将棋盘上每个格子的分值串联起来组成的值
     * @return
     */
    public String toKey() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                sb.append(board[i][j]);
            }
        }
        return sb.toString();
    }
}
