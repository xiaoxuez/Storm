package com.storm.trident;

import storm.trident.operation.CombinerAggregator;
import storm.trident.tuple.TridentTuple;

/**
 *  聚合器： 跟function类似，区别是，会替换tuple的字段和值，将一个集合的tuple组合到一个单独的字段中，通俗一点点我的理解是大概用于将这个集合的tuples统计成一个结果（单独的字段）
 *  Storm对每个tuple调用init方法，然后重复调用combine方法直到一个分片的数据处理完成
 *  有三种聚合器： 
 *  	CombinerAggregator： 传给combine两个参数是局部聚合的结果以及调用了init返回的值
 *   	ReducerAggregator: 	于CombinerAggregator类似，区别在于“遍历”时调用的方法提供的参数不同，
 *   						ReducerAggregator的reduce两个参数是局部聚合的结果和“遍历”到当前的tuple,方法定义如下
 *   						T reduce(T cuur, TridentTuple tuple)
 *   	Aggregator: 最通用的聚合器，重要的方法之一定义为
 *   				void aggerate(T val, TridentTuple tuple, TridentColloctor collector)
 *   				val参数可以在处理tuple的时候累计值
 *   
 *   聚合器的使用时在进行了GroupBy的前提下，GroupBy相当于将tuple分了集合，才能进行集合。使用方法可直接调用aggregate方法或结合持久化操作使用
 */
public class CountAggregator implements CombinerAggregator<Long>{

	@Override
	public Long init(TridentTuple tuple) {
		// TODO Auto-generated method stub
		System.out.println("tuple = "+ tuple.getFields());
		return 1L;
	}

	@Override
	public Long combine(Long val1, Long val2) {
		// TODO Auto-generated method stub
		return val1 + val2;
	}

	@Override
	public Long zero() {
		// TODO Auto-generated method stub
		return 0L;
	}

}
