package com.kafka.log.storm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kafka.log.storm.EWMA.Time;

import backtype.storm.tuple.Values;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

public class MovingAverageFunction extends BaseFunction {
	  private static final Logger LOG = LoggerFactory.getLogger(BaseFunction.class);

	  private EWMA ewma;
	  private Time emitRatePer;

	  public MovingAverageFunction(EWMA ewma, Time emitRatePer) {
	    this.ewma = ewma;
	    this.emitRatePer = emitRatePer;
	  }

	  public void execute(TridentTuple tuple, TridentCollector collector) {
	    this.ewma.mark(tuple.getLong(0));
	    LOG.debug("Rate: {}", this.ewma.getAverageRatePer(this.emitRatePer));
	    collector.emit(new Values(this.ewma.getAverageRatePer(this.emitRatePer)));
	  }
	}
