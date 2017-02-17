package com.storm.trident;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;
import storm.trident.Stream;
import storm.trident.TridentTopology;

public class OutbreakDetectionTopology {
	public static void main(String[] args) throws Exception {
		Config config = new Config();
		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology("cdc", config, buildTopology());
		Thread.sleep(200000);
		cluster.shutdown();
	}

	public static StormTopology buildTopology() {
		TridentTopology topology = new TridentTopology();
		// 1. 构建数据发射Spout
		DiagnosisEventSpout spout = new DiagnosisEventSpout();
		Stream inputStream = topology.newStream("event", spout);
		
		// 2. 加以运算,以下使用的运算类型有两种：filter和function，
		//  filter是过滤接口，可根据情况过滤tuple,
		//  function读取tuple并且发送新的tuple, 在发送数据时，将新字段添加在tuple中，并不会删除或者变更已有的字段
		
		// 2.1 添加过滤,将不关心的疾病过滤掉，each方法的参数一为inputFields，为tuple输入时带的字段
		inputStream.each(new Fields("event"), new DiseaseFilter())
		// .each方法添加function运算时，参数一为inputFields，为tuple输入时带的字段， 参数三为发射出值的字段
		// 2.2  CityAssignment和HourAssignment为function运算，作用是添加city hour cityDiseaseHour字段，字段含义见具体类定义， 
				.each(new Fields("event"), new Function.CityAssignment(), new Fields("city"))
				.each(new Fields("event", "city"), new Function.HourAssignment(), new Fields("hour", "cityDiseaseHour"))
		// 2.3 为了其后进行数据统计，所以进行分组，将cityDiseaseHour字段值相同的tuple分给同一个jvm进行统计
				.groupBy(new Fields("cityDiseaseHour"))
		// 2.4 持久化操作，进行groupBy之后的GroupedStream对象的persistentAggregate方法为对批tuple进行聚合统计，并且将收集到的信息持久化在state中
				//注意是先聚合统计，再写入state，这里聚合统计 是通过Count对象实现，persistentAggregate返回的是state对象，所以调用newValuesStream生成stream
				.persistentAggregate(new OutbreakTrendFactory(), new CountAggregator(), new Fields("count")).newValuesStream()
		// 2.6  检查数据，疾病数量超过阈值时报警退出程序
				.each(new Fields("cityDiseaseHour", "count"), new Function.OutBreakDetector(), new Fields("alert"))
				.each(new Fields("alert"), new Function.DispatchAlert(), new Fields());
		return topology.build();
	}
}
