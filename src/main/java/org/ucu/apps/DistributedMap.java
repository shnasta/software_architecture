package org.ucu.apps;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.config.Config;

public class DistributedMap {

    public static void main(String[] args) {
        Config config = new Config();
        config.setClusterName("dev");

        HazelcastInstance hazelcastInstance1 = Hazelcast.newHazelcastInstance(config);
        HazelcastInstance hazelcastInstance2 = Hazelcast.newHazelcastInstance(config);
        HazelcastInstance hazelcastInstance3 = Hazelcast.newHazelcastInstance(config);

        var map = hazelcastInstance1.getMap("map");

        for (int i = 0; i < 1000; i++) {
            map.put(i, "value" + i);
        }

//        hazelcastInstance2.getLifecycleService().terminate();
//        hazelcastInstance3.getLifecycleService().terminate();
    }
}
