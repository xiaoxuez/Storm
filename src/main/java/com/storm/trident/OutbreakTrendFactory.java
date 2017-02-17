package com.storm.trident;

import java.util.Map;

import backtype.storm.task.IMetricsContext;
import storm.trident.state.State;
import storm.trident.state.StateFactory;
import storm.trident.state.map.IBackingMap;
import storm.trident.state.map.NonTransactionalMap;

public class OutbreakTrendFactory implements StateFactory{
	/**
	 * 既然要存储状态，就需要配置存储状态的状态，在Storm中，有三种类型的状态，
	 * 	非事务型，没有回滚能力，更新操作是永久的，commit操作会被忽略
	 * 	重复事务型，由同一批tuple提供的结果是等幂的
	 * 	不透明事务型， 更新操作基于先前的值，这样一批数据组成不同，持久化的数据也会变，
	 * 	我对这几种的类型都不能理解，需要求解++
	 */
	
	/**
	 * 在初始化时，Storm调用StateFactory.makeState()方法为每个批次分片数据来建立一个State实例，批次分片的个数取决于storm的并发量，storm将信息通过partitionIndex
	 * 和numPartitions参数传递给makeState方法，使state实现在需要时可以进行分区的特殊逻辑
	 */
	public State makeState(Map conf, IMetricsContext metrics, int partitionIndex, int numPartitions) {
		return new OutbreakTrendState(new OutbreakTrendBackingMap());
	}
	
	
	/**
	 * 
	 * 例子里没有事务型保证，所以选用非事务型  
	 */
	public static class OutbreakTrendState extends NonTransactionalMap<Long> {

		protected OutbreakTrendState(IBackingMap<Long> backing) {
			super(backing);
		}
		
	}
	
	

}
