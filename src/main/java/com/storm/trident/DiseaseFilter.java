package com.storm.trident;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.storm.trident.DiagnosisEventSpout.DiagnosisEvent;

import storm.trident.operation.BaseFilter;
import storm.trident.tuple.TridentTuple;

public class DiseaseFilter extends BaseFilter{
	/**
	 * BaseFilter 的 isKeep方法返回false，则表明将该tuple过滤掉
	 */
	private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(DiseaseFilter.class);
	public boolean isKeep(TridentTuple tuple) {
		  DiagnosisEvent diagnosis = (DiagnosisEvent) tuple.getValue(0);
	        Integer code = Integer.parseInt(diagnosis.diagnosisCode);
	        if (code.intValue() <= 322) {
	            LOG.debug("Emitting disease [" + diagnosis.diagnosisCode + "]");
	            return true;
	        } else {
	            LOG.debug("Filtering disease [" + diagnosis.diagnosisCode + "]");
	            return false;
	        }
	}

}
