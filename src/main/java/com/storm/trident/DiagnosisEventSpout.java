package com.storm.trident;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Fields;
import storm.trident.operation.TridentCollector;
import storm.trident.spout.ITridentSpout;
import storm.trident.topology.TransactionAttempt;

public class DiagnosisEventSpout implements ITridentSpout<Long>{
	/**
	 * spout并没有真正发射tuple，只是将这项工作分配给BatchCoordinator和Emitter
	 * Emitter负责发送tuple，BatchCoordinator负责管理批次和元数据，Emitter需要依靠元数据来恰当地进行批次的数据重放
	 * TridentSpout需要实现的是，提供BatchCoordinator和Emitter，并且声明发射的tuple包含的字段
	 */
	private static final long serialVersionUID = 1L;
	
	BatchCoordinator<Long> coordinator = new DefaultCoordinator();
	
	Emitter<Long> emitter = new DiagnosisEventEmitter();
	public storm.trident.spout.ITridentSpout.BatchCoordinator<Long> getCoordinator(String txStateId, Map conf,
			TopologyContext context) {
		return coordinator;
	}

	public storm.trident.spout.ITridentSpout.Emitter<Long> getEmitter(String txStateId, Map conf,
			TopologyContext context) {
		return emitter;
	}

	public Map getComponentConfiguration() {
		return null;
	}

	public Fields getOutputFields() {
		return new Fields("event");
	}
	
	
	/**
	 * 以下是BatchCoordinator和Emitter的类定义
	 */
	
	
	public static class DiagnosisEventEmitter implements Emitter<Long>, Serializable{
		/**
		 * 数据是实现了Serializable接口的实体对象
		 * 
		 * 
		 * Emitter只有一个功能，将tuple打包发射出去，
		 */
		private static final long serialVersionUID = 1L;
		// 线程安全的原子操作Integer的类
		AtomicInteger successfulTransactions = new AtomicInteger(0);
		/**
		 * 打包发射tuple，参数包括事务信息，batch元数据，和用来发射的collector
		 */
		public void emitBatch(TransactionAttempt tx, Long coordinatorMeta, TridentCollector collector) {
			/**
			 * 数据产生
			 */
			for (int i = 0; i < 10000; i++) {
	            List<Object> events = new ArrayList<Object>();
	            double lat = new Double(-30 + (int) (Math.random() * 75));
	            double lng = new Double(-120 + (int) (Math.random() * 70));
	            long time = System.currentTimeMillis();

	            String diag = new Integer(320 + (int) (Math.random() * 7)).toString();
	            DiagnosisEvent event = new DiagnosisEvent(lat, lng, time, diag);
	            events.add(event);
	            collector.emit(events);
	            //发射tuple，这里是将对象封装成独立的字段
	        }
			
		}

		public void success(TransactionAttempt tx) {
			successfulTransactions.incrementAndGet();
		}

		public void close() {
			
		}
		
	}
	
	public static class DiagnosisEvent implements Serializable {
	    private static final long serialVersionUID = 1L;
	    public double lat;
	    public double lng;
	    public long time;
	    public String diagnosisCode;

	    public DiagnosisEvent(double lat, double lng, long time, String diagnosisCode) {
	        super();
	        this.time = time;
	        this.lat = lat;
	        this.lng = lng;
	        this.diagnosisCode = diagnosisCode;
	    }
	}
	
	
	public static class DefaultCoordinator implements ITridentSpout.BatchCoordinator<Long>, Serializable {
		/**
		 * BatchCoordinator是一个泛型类，这个泛型类是重放一个batch所需要的元数据。本例中spout发送的是随机事件，因此元数据可以忽略
		 * 在实际系统中，元数据可能包含组成了这个batch的消息或者对象的标识符，通过这个信息，非透明型和事务型spout可以实现约定，确保batch的内容不出现重复，在事务型spout中，batch的内容不会出现变化
		 * 
		 * BatchCoordinator类作为一个Storm Bolt运行在一个单线程中，Storm会在ZooKeeper中持久化存储这个 元数据，当事务处理完成时会通知到对应的coordinator
		 */
	    private static final long serialVersionUID = 1L;
	    private static final Logger LOG = LoggerFactory.getLogger(DefaultCoordinator.class);

	    public Long initializeTransaction(long l, Long aLong, Long x1) {
	        LOG.info("Initializing Transaction [" + l + "]");
	        return null;
	    }

	    public void success(long l) {
	        LOG.info("Successful Transaction [" + l + "]");
	    }

	    public boolean isReady(long l) {
	        return true;
	    }

	    public void close() {

	    }
	}

}
