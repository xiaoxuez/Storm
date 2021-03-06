## 第三章示例

这个示例的内容是，计算疾病在某个城市的发病率，大于某个阈值时报警

1. spout 模拟发病的是经纬度和病种
2. filter => 过滤关心的病种
3. 统计计算

注意的是，一旦有统计计算的步骤，就需要进行按某个identity分组，这样按identity进行统计的结果才是正确的，因为确保了相同identity分到同一个bolt中。

这个示例使用的是Trident topology, 与一般topology相比，trident的运算（类似bolt)有两种： filter和function， 另外还有聚合器（统计作用）。


### Trident Topology
 Trident 在Storm上提供了高层抽象，Trident抽象调了**事务处理**和**状态管理**的细节，特别是，它可以让一批tuple进行离散的事务处理,此外，Trident还提供了抽象操作，允许topology在数据上执行函数功能、过滤和聚合操作

##### Spout
实现的接口之一是ITridentSpout,需要重写的方法是getCoordinator, getEmitter, getOutputFields, ITridentSpout并没有真正发送tuple, 而是把这项任务分配给了Emitter, 所以需要提供实现Emitter的对象，BatchCoordinator接口是重放一个batch所需要的元数据，实现见DefaultCoordinator,

##### 运算

 运算分为filter和function
 
 + Storm提供的Function接口： 和bolt类似，读取tuple并且发送新的tuple，区别之一是Trident Function 只能添加数据， 在发送数据时，将新字段添加在tuple中，并不会删除或者变更已有的字段
 + BaseFilter 的 isKeep方法返回false，则表明将该tuple过滤掉
 
使用stream.each(inputfields, filter) 或 stream.each(inputfields, function, outputfields)应用运算。

.each返回值为Stream

***warning*** : Trident的fuction只能添加数据，意味着我们的所有tuple数据流，都是在json提取出的字段值的基础上添加的，都会包含着原始的未解析的JSON字符串，
**stream.project**方法，削减tuple数据流，只保留必需的字段，当重新分片的数据量很大时这个功能非常重要

##### 聚合器
 跟function类似，区别是，会替换tuple的字段和值，将一个集合的tuple组合到一个单独的字段中，通俗一点点我的理解是大概用于将这个集合的tuples统计成一个结果（单独的字段）.
 Storm对每个tuple调用init方法，然后重复调用combine方法直到一个分片的数据处理完成
 
 有三种聚合器
 
 + CombinerAggregator： 传给combine两个参数是局部聚合的结果以及调用了init返回的值
 + ReducerAggregator: 	于CombinerAggregator类似，区别在于“遍历”时调用的方法提供的参数不同，ReducerAggregator的reduce两个参数是局部聚合的结果和“遍历”到当前的tuple,方法定义如下
  `T reduce(T cuur, TridentTuple tuple)`
  
 + Aggregator: 最通用的聚合器，重要的方法之一定义为
  `void aggerate(T val, TridentTuple tuple, TridentColloctor collector)`
  val参数可以在处理tuple的时候累计值
  
   聚合器的使用时在进行了GroupBy的前提下，GroupBy相当于将tuple分了集合，才能进行集合。使用方法可直接调用aggregate方法或结合持久化操作使用
   `.persistentAggregate(new OutbreakTrendFactory(), new CountAggregator(), new Fields("count")).newValuesStream()`
 
 `CountAggregator` : 聚合器
 
 `OutbreakTrendFactory `：状态管理
 ##### 状态管理
 既然要存储状态，就需要配置存储状态的状态，在Storm中，有三种类型的状态，
 + 非事务型，没有回滚能力，更新操作是永久的，commit操作会被忽略
 + 重复事务型，由同一批tuple提供的结果是等幂的
 + 不透明事务型， 更新操作基于先前的值，这样一批数据组成不同，持久化的数据也会变，
 `OutbreakTrendFactory`
 
 一个Trident state 实现包括下面三个组件：
 + StateFactory: StateFactory接口**定义了**Trident用来建立持久**state对象**的方法 (实现者实现获得state对象的方法)
 + State: Trident state 接口定义了beginCommit() 和 commit()方法，分别在Trident 一批分区数据写到后端存储之前和之后调用。如果写入成功（意味着， 所有的处理都没有报错）, Trident会调用commit(）方法
 + StateUpdater: StateUpdater接口定义了updateState()方法用来调用更新state, 假定处理了一批tuple, Trident将三个参数传递给这个方法，需要更新的state对象，一个批次分区数据中Trident Tuple 对象的列表，和Trident Collector实例用来视需要发送额外的tuple作为state更新的结果。updateState()方法的调用在在beginCommit()之后调用，（处理失败或者数据重放会导致重复调用）
 
 
 