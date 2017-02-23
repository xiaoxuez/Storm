package com.trident;

import java.util.Map;

import backtype.storm.task.IMetricsContext;
import storm.trident.state.State;
import storm.trident.state.StateFactory;

public class CountStateFactory implements StateFactory{

	@Override
	public State makeState(Map conf, IMetricsContext metrics, int partitionIndex, int numPartitions) {
		// TODO Auto-generated method stub
		return null;
	}

}
