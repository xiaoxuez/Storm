package com.broad.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storm.trident.operation.TridentCollector;
import storm.trident.spout.ITridentSpout.Emitter;
import storm.trident.topology.TransactionAttempt;

/**
 * Spout Emitter。 静态属性有队列queues，方法有入队
 * @author xiaoxuez
 *
 * @param <T>
 */
public class LocalQueueEmitter<T> implements Emitter<Long>, Serializable {
	public static final int MAX_BATCH_SIZE = 1000;
	public static AtomicInteger successfulTransactions = new AtomicInteger(0);
	private static Map<String, BlockingQueue<Object>> queues = new HashMap<String, BlockingQueue<Object>>();
	private static final Logger LOG = LoggerFactory.getLogger(LocalQueueEmitter.class);
	private String queueName;

	public LocalQueueEmitter(String queueName) {
		queues.put(queueName, new LinkedBlockingQueue<Object>());
		this.queueName = queueName;
	}

	public void close() {
		// TODO Auto-generated method stub

	}

	public void emitBatch(TransactionAttempt tx, Long corrdinatorMeta, TridentCollector collector) {
		int size = 0;
		LOG.debug("Getting batch for [" + tx.getTransactionId() + "]");
		//BlockingQueue.peek返回队列头部的元素 如果队列为空，则返回null
		while (getQueue().peek() != null && size <= MAX_BATCH_SIZE) {
			List<Object> values = new ArrayList<Object>();
			try {
				LOG.debug("Waiting on work from [" + this.queueName + "]:[" + getQueue().size() + "]");
				//BlockingQueue.take 取走BlockingQueue里排在首位的对象,若BlockingQueue为空,阻断进入等待状态直到Blocking有新的对象被加入为止 
				values.add(getQueue().take());
				LOG.debug("Got work from [" + this.queueName + "]:[" + getQueue().size() + "]");
			} catch (InterruptedException ex) {
				LOG.debug("BlockingQueue.take meet InterruptedException");
			}
			collector.emit(values);
			size++;
		}
		LOG.info("Emitted [" + size + "] elements in [" + tx.getTransactionId() + "], [" + getQueue().size()
				+ "] remain in queue.");
	}

	public void success(TransactionAttempt tx) {
		successfulTransactions.incrementAndGet();

	}

	public BlockingQueue<Object> getQueue() {
		return LocalQueueEmitter.queues.get(this.queueName);
	}

	/**
	 * 加入队列
	 * @param work
	 */
	public void enqueue(T work) {
		LOG.debug("Adding work to [" + this.queueName + "]:[" + getQueue().size() + "]");
		if (getQueue().size() % 1000 == 0)
			LOG.info("[" + this.queueName + "] size = [" + getQueue().size() + "].");
		this.getQueue().add(work);
	}
}
