package com.twitter.graph;

import java.util.List;

import com.twitter.graph.GraphStateFactory.GraphState;

import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseStateUpdater;
import storm.trident.tuple.TridentTuple;

public class GraphStateUpdater extends BaseStateUpdater<GraphState>{
	  private static final long serialVersionUID = 1L;

	  private GraphTupleProcessor graphTupleProcessor;
	  public GraphStateUpdater(GraphTupleProcessor graphTupleProcessor) {
		    this.graphTupleProcessor = graphTupleProcessor;
		  }
	@Override
	public void updateState(GraphState state, List<TridentTuple> tuples, TridentCollector collector) {
		// TODO Auto-generated method stub
		state.update(tuples, collector, this.graphTupleProcessor);
	}

}
