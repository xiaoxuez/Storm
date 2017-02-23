package com.trident;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;

import storm.trident.operation.CombinerAggregator;
import storm.trident.tuple.TridentTuple;

public class CountCombinerAggregator implements CombinerAggregator<Map<String, Integer>>{

	@Override
	public Map<String, Integer> init(TridentTuple tuple) {
		// TODO Auto-generated method stub
		Map<String, Integer> map = new HashMap<>();
		if(tuple.getString(0) != null) {
			map.put(tuple.getString(0), 1);
		}
		return map;
	}

	@Override
	public Map<String, Integer> combine(Map<String, Integer> val1, Map<String, Integer> val2) {
		// TODO Auto-generated method stub
		if(val2 != null){
			for (String word : val2.keySet()) {
				Integer number = MapUtils.getInteger(val1, word);
				Integer increase = number == null ? 1 : number + 1;
//				System.out.println("val2.keySet + " + word);
				val1.put(word,  increase);
			}
		}
		return val1;
	}

	@Override
	public Map<String, Integer> zero() {
		// TODO Auto-generated method stub
		return new HashMap<String, Integer>();
	}

}
