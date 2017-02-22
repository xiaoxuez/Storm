package com.storm.trident;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.storm.trident.DiagnosisEventSpout.DiagnosisEvent;

import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

public class Function {
	/**
	 * Storm提供的Function接口： 和bolt类似，读取tuple并且发送新的tuple，区别之一是Trident Function
	 * 只能添加数据， 在发送数据时，将新字段添加在tuple中，并不会删除或者变更已有的字段
	 */

	/**
	 * 计算经纬度，获得所在城市，emit(为城市名的List)
	 */
	public static class CityAssignment extends BaseFunction {
		private static final long serialVersionUID = 1L;
		private static final Logger LOG = LoggerFactory.getLogger(CityAssignment.class);

		private static Map<String, double[]> CITIES = new HashMap<String, double[]>();

		{ // Initialize the cities we care about.
			double[] phl = { 39.875365, -75.249524 };
			CITIES.put("PHL", phl);
			double[] nyc = { 40.71448, -74.00598 };
			CITIES.put("NYC", nyc);
			double[] sf = { -31.4250142, -62.0841809 };
			CITIES.put("SF", sf);
			double[] la = { -34.05374, -118.24307 };
			CITIES.put("LA", la);
		}

		public void execute(TridentTuple tuple, TridentCollector collector) {
			DiagnosisEvent diagnosis = (DiagnosisEvent) tuple.getValue(0);
			double leastDistance = Double.MAX_VALUE;
			String closestCity = "NONE";
			for (Entry<String, double[]> city : CITIES.entrySet()) {
				double R = 6371; // km
				double x = (city.getValue()[0] - diagnosis.lng) * Math.cos((city.getValue()[0] + diagnosis.lng) / 2);
				double y = (city.getValue()[1] - diagnosis.lat);
				double d = Math.sqrt(x * x + y * y) * R;
				if (d < leastDistance) {
					leastDistance = d;
					closestCity = city.getKey();
				}
			}
			List<Object> values = new ArrayList<Object>();
			values.add(closestCity);
			LOG.debug("Closest city to lat=[" + diagnosis.lat + "], lng=[" + diagnosis.lng + "] == [" + closestCity
					+ "], d=[" + leastDistance + "]");
			collector.emit(values);
		}

	}

	/**
	 * 转换时间戳
	 *
	 */
	public static class HourAssignment extends BaseFunction {
		private static final Logger LOGGER = LoggerFactory.getLogger(HourAssignment.class);

		public void execute(TridentTuple tuple, TridentCollector collector) {
			DiagnosisEvent diagnosis = (DiagnosisEvent) tuple.getValue(0);
			String city = (String) tuple.getValue(1);
			long timestamp = diagnosis.time;
			long hourSinceEpoch = timestamp / 1000 / 60 / 60;
			LOGGER.debug("Key = [" + city + ":" + hourSinceEpoch + "]");
			String key = city + ":" + diagnosis.diagnosisCode + ":" + hourSinceEpoch;
			List<Object> values = new ArrayList<Object>();
			values.add(hourSinceEpoch);
			// 由城市疾病代码小时组合而成的key, 实际上这个组合值会作为聚合计数的唯一标识符
			values.add(key);
			collector.emit(values);
		}
	}

	public static class OutBreakDetector extends BaseFunction {
		private static final int THRESHOLD = 1000;

		public void execute(TridentTuple tuple, TridentCollector collector) {
			String key = (String) tuple.getValue(0);
			Long count = (Long) tuple.getValue(1);
			//当疾病数量超过阈值，添加新的字段
			if (count > THRESHOLD) {
				ArrayList<Object> values = new ArrayList<Object>();
				values.add("Outbreak detected for [" + key + "]");
				collector.emit(values);
			}

		}
	}

	public static class DispatchAlert extends BaseFunction {
		private static final Logger LOGGER = LoggerFactory.getLogger(DispatchAlert.class);

		public void execute(TridentTuple tuple, TridentCollector collector) {
			String alert = (String) tuple.getValue(0);
			LOGGER.error("ALERT RECEIVED [" + alert + "]");
			LOGGER.error("Dispatch the national guard!");
			//  结束程序
//			System.exit(0);
		}

	}

}
