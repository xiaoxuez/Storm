package com.broad.game;

import java.util.ArrayList;
import java.util.List;

import com.esotericsoftware.minlog.Log;

import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

/**
 * 棋盘下棋，根据当前gameState，下下一步棋，nextBoards，返回值为下一步所有的可能性，遍历可能性，依次emit
 * @author xiaoxuez
 *这个function将当前棋盘状态添加到历史列表中，然后将一个带有子节点棋盘状态的新的GameState对象加入队列
 */
public class GenerateBoards extends BaseFunction {
    private static final long serialVersionUID = 1L;


    public void execute(TridentTuple tuple, TridentCollector collector) {
 
        GameState gameState = (GameState) tuple.get(0);
        Board currentBoard = gameState.getBoard();
        List<Board> history = new ArrayList<Board>();
        history.addAll(gameState.getHistory());
        history.add(currentBoard);
        // 执行了跟isEndGame类相同的检查/过滤的功能
        if (!currentBoard.isEndState()) {
            String nextPlayer = Player.next(gameState.getPlayer());
            List<Board> boards = gameState.getBoard().nextBoards(nextPlayer);
            Log.debug("Generated [" + boards.size() + "] children boards for [" + gameState.toString() + "]");
            for (Board b : boards) {
                GameState newGameState = new GameState(b, history, nextPlayer);
                List<Object> values = new ArrayList<Object>();
                values.add(newGameState);
                collector.emit(values);
            }
        } else {
            Log.debug("End game found! [" + currentBoard + "]");
        }
    }

}
