package com.kafka.log.append;

import java.util.Properties;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class KafkaAppender extends AppenderBase<ILoggingEvent> {

	private String zookeeperHost;
	private Producer<String, String> producer;
	private Formatter formatter;
	private String brokerList;
	private String topic;

	public String getZookeeperHost() {
		return zookeeperHost;
	}

	public void setZookeeperHost(String zookeeperHost) {
		this.zookeeperHost = zookeeperHost;
	}

	public Producer<String, String> getProducer() {
		return producer;
	}

	public void setProducer(Producer<String, String> producer) {
		this.producer = producer;
	}

	public Formatter getFormatter() {
		return formatter;
	}

	public void setFormatter(Formatter formatter) {
		this.formatter = formatter;
	}

	public String getBrokerList() {
		return brokerList;
	}

	public void setBrokerList(String brokerList) {
		this.brokerList = brokerList;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	@Override
	protected void append(ILoggingEvent event) {
		String payload = this.formatter.format(event);
		this.producer.send(new KeyedMessage<String, String>(getTopic(), payload));
	}

	@Override
	public void start() {
		if (this.formatter == null) {
			this.formatter = new MessageFormatter();
		}
		super.start();
		Properties props = new Properties();
		props.put("zk.connect", this.zookeeperHost);
		props.put("metadata.broker.list", this.brokerList);
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		ProducerConfig config = new ProducerConfig(props);
		this.producer = new Producer<String, String>(config);
	}

	@Override
	public void stop() {
		super.stop();
		this.producer.close();
	}

}
