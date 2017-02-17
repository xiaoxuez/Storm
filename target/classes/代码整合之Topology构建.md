#代码整合之Topology构建
####Topology提交
	
	// 集群
	StormSubmitter.submitTopologyWithProgressBar(arg[0], config, builder.createTopology());
	// 本地模拟
	LocalCluster cluster = new LocalCluster();
			cluster.submitTopology(TOPOLOGY_NAME, config, builder.createTopology());

####普通Topology构建
		
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout(SENTENCE_SPOUT_ID, spout, 2);
		builder.setBolt(SPLIT_BOLT_ID, spliteSentenceBolt, 2).setNumTasks(4).shuffleGrouping(SENTENCE_SPOUT_ID);
		builder.setBolt(COUNT_BOLT_ID, wordCountBolt, 4).fieldsGrouping(SPLIT_BOLT_ID, new Fields("word"));		builder.setBolt(REPORT_BOLT_ID, reportBolt).globalGrouping(COUNT_BOLT_ID);


简要介绍

+ setSpout/setBolt方法的***参数***之一是可设置并发为几个task,每个task指派各自的executor线程(自己的理解这个参数设置就是executor线程的数量)
+ setSpout/setBolt方法的***返回值***类型为Declarer。
	
	1. 可通过此对象调用***setNumTasks***设置task的数量，  
	如builder.setBolt(SPLIT_BOLT_ID, spliteSentenceBolt, 2).setNumTasks(4)的结果就是2个线程，每个线程指派2个task).   
	2. 另外，此对象的***fieldsGrouping和shuffleGrouping***方法可对数据流进行分组，数据分组的方式还有很多，可见1.5章节，此处使用了shuffleGrouping随机分和fieldsGrouping按字段分

#### Trident Topology构建
	public static StormTopology buildTopology() {
		TridentTopology topology = new TridentTopology();
		DiagnosisEventSpout spout = new DiagnosisEventSpout();
		Stream inputStream = topology.newStream("event", spout);
		inputStream.each(new Fields("event"), new DiseaseFilter())
				.each(new Fields("event"), new Function.CityAssignment(), new Fields("city"))
				.each(new Fields("event", "city"), new Function.HourAssignment(), new Fields("hour", "cityDiseaseHour"))
				.groupBy(new Fields("cityDiseaseHour"))
				.persistentAggregate(new OutbreakTrendFactory(), new CountAggregator(), new Fields("count")).newValuesStream()
				.each(new Fields("cityDiseaseHour", "count"), new Function.OutBreakDetector(), new Fields("alert"))
				.each(new Fields("alert"), new Function.DispatchAlert(), new Fields());
		return topology.build();
	}
	
简要介绍

+ Trident运算类型有两种：filter和function
	1. filter是过滤接口，可根据情况过滤tuple,
	2. function读取tuple并且发送新的tuple, 在发送数据时，将新字段添加在tuple中，并不会删除或者变更已有的字段
+ 添加运算是通过调用Stream的.each方法，通常来说.each方法参数一是inputFields,参数二是运算，若运算是function，则需要参数三是outputfields
+ 同样，Trident Topology也有对流进行分组，通过调用.groupBy方法，返回类型为GroupStream
+ Trident Topology的持久化数据有两种途径
	1. partitionPersist
	2. persistentAggregate
	具体使用见***示例中的代码整合之持久化操作***