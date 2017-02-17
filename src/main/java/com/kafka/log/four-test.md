## 第四章 实时趋势分析的示例

这个示例的整个流程是：

1. 循环产生不同频率的log日志，并通过Logback将日志写到kafka消息队列里
2. kafka消息队列的这头连接的是spout,
3. spout -> bolt -> ...


具体详解
#### appende包下的代码是写log的作用
 + `RogueApplication` ：以一定频率写log
 + `KafkaAppender` : Logback框架提供的简单扩展机制，允许添加额外的appender. 这个类实现了AppenderBase,将日志信息数据写入Kafka中
 
#### storm包下的代码是从Kafka中读出消息，并采取一系列操作
 + `LogAnalysisTopology` ： 定义Topology
 + 使用的Topology是Trident Topology
 + `XMPPFunction` : 其中xmpp的连接使用，是在Function中，建立xmpp的长连接(prepare中)，为了使这个Function的复用性更强，将从tuple中格式化数据到适用于消息通知字符串的类定为接口，不同消息实现接口的方法即可

Trident的fuction只能添加数据，意味着我们的所有tuple数据流，都是在json提取出的字段值的基础上添加的，都会包含着原始的未解析的JSON字符串，
**stream.project**方法，削减tuple数据流，只保留必需的字段，当重新分片的数据量很大时这个功能非常重要


