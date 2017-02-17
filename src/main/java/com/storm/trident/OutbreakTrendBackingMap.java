package com.storm.trident;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storm.trident.state.map.IBackingMap;

public class OutbreakTrendBackingMap implements IBackingMap<Long> {
	private static final Logger LOGGER = LoggerFactory.getLogger(OutbreakTrendBackingMap.class);
	//ConcurrentHashMap 比Hashtable更容易写并发，
	Map<String, Long> storage = new ConcurrentHashMap<>();
	
	/**
	 * 这里的数据其实是存在了storage里，实际上并没有固话存储，然而BackingMap是一个非常巧妙的抽象，只需要将传入MapState对象的backing map的实例替换就可以更换持久层的实现
	 */

	public List<Long> multiGet(List<List<Object>> keys) {
		List<Long> values = new ArrayList<>();
		for (List key : keys) {
			Long value = storage.get(key.get(0));
			if (value == null) {
				values.add(0L);
			} else {
				values.add(value);
			}
		}
		return values;
	}

	public void multiPut(List<List<Object>> keys, List<Long> vals) {
		for (int i = 0; i < keys.size(); i++) {
			LOGGER.info("Persisting [" + keys.get(i).get(0) + "] ==> [" + vals.get(i) + "]");
			storage.put((String) keys.get(i).get(0), vals.get(i));
		}

	}

}
