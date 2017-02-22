package trident.test;

import java.io.IOException;

import backtype.storm.Config;
import backtype.storm.LocalCluster;

public class TestEntrance {
	public static void main(String[] args) throws IOException {
		LocalCluster cluster = new LocalCluster();
		Config config = new Config();
		config.setDebug(true);
		
		
//		cluster.submitTopology("test-group", config, TopologyBuilder.buildGroupByTopology());
		/**
		 * [France, {France=1}]partition = 2num = 5
		 * [USA, {USA=3}]partition = 4num = 5
		 * [Spain, {Spain=1}]partition = 3num = 5
		 */
		
		cluster.submitTopology("test-group", config, TopologyBuilder.buildGroupPartitionAggrateTopology());
		/**  parallelismHint = 5
		 * **	[USA, {USA=1}]partition = 0num = 5
		 **		[USA, {USA=1}]partition = 2num = 5
		 *		[USA, {USA=1}]partition = 1num = 5
		 *		[Spain, {Spain=1}]partition = 3num = 5
		 *		 [USA, {USA=1}]partition = 4num = 5
		 *
		 * parallelismHint = 2
		 * [USA, {USA=2}]partition = 1num = 2
		 * [USA, {USA=2}]partition = 0num = 2
		 * [France, {France=1}]partition = 1num = 2
		 * 
		 * 
		 * without groupBy parallelismHint = 5
		 * 
		 *  [{USA=1}]partition = 0num = 5
		 *  [{UK=1}]partition = 3num = 5
		 *  [{Spain=1}]partition = 2num = 5
		 *  [{USA=1}]partition = 4num = 5
		 *  [{UK=1}]partition = 1num = 5
		 *  
		 *  
		 *  without groupBy parallelismHint = 2
		 *   [{USA=1, Spain=2}]partition = 0num = 2
		 *   [{Spain=2}]partition = 1num = 2
		 */
	}
}
