package com.broad.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storm.trident.operation.BaseFilter;
import storm.trident.tuple.TridentTuple;

public class isEndGame extends BaseFilter {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(isEndGame.class);

    public boolean isKeep(TridentTuple tuple) {
        GameState gameState = (GameState) tuple.get(0);
        boolean keep = (gameState.getBoard().isEndState());
        if (keep) {
            LOG.info("END GAME [" + gameState + "]");
        }
        LOG.info("isEndGame Function " + keep);
        return keep;
    }

}
