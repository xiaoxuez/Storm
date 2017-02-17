package com.broad.game;

import com.esotericsoftware.minlog.Log;

import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

/**
 * 加入队列Function,将棋盘状态加入队列
 * @author xiaoxuez
 *
 * @param <T>
 */
public class LocalQueuerFunction <T> extends BaseFunction {
	private static final long serialVersionUID = 1L;
    LocalQueueEmitter<T> emitter;

    public LocalQueuerFunction(LocalQueueEmitter<T> emitter) {
        this.emitter = emitter;
    }

    @SuppressWarnings("unchecked")
    public void execute(TridentTuple tuple, TridentCollector collector) {
        T object = (T) tuple.get(0);
        Log.info("LocalQueuerFunction Queueing [" + object + "]");
        this.emitter.enqueue(object);
    }

}
