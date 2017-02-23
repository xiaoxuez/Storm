package com.trident;

import java.util.List;

import backtype.storm.tuple.Values;
import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseQueryFunction;
import storm.trident.state.map.ReadOnlyMapState;
import storm.trident.tuple.TridentTuple;

public class MyMapGet extends BaseQueryFunction<ReadOnlyMapState, Object> {
	/**
	 * keys 是 查询的 单词(新来的)， map是供查询state的结构。
	 */
    @Override
    public List<Object> batchRetrieve(ReadOnlyMapState map, List<TridentTuple> keys) {
    	System.out.println("map - get batch retrieve keys :" +  keys);
    	System.out.println("map - get batch retrieve values :" +  map.multiGet((List) keys));
    	
        return map.multiGet((List) keys);
    }    
    
    
    @Override
    public void execute(TridentTuple tuple, Object result, TridentCollector collector) {
        collector.emit(new Values(result));
    }    
}