package org.ucu.apps;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

public class Locks {
    public static void main(String[] args) {
        Config config = new Config();
        config.setClusterName("dev");

        HazelcastInstance hazelcastInstance1 = Hazelcast.newHazelcastInstance(config);
        HazelcastInstance hazelcastInstance2 = Hazelcast.newHazelcastInstance(config);
        HazelcastInstance hazelcastInstance3 = Hazelcast.newHazelcastInstance(config);

        final int TIMES_TO_INCREMENT = 10000;

        var map = hazelcastInstance1.getMap("map1");
        var threads = new Thread[3];
        map.put("no_lock", 0);
        for (int i = 0; i < 3; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < TIMES_TO_INCREMENT; j++) {
                    var value = map.get("no_lock");
                    map.put("no_lock", (int) value + 1);
                }
            });
            threads[i].start();
        }
        for (int i = 0; i < 3; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        var map2 = hazelcastInstance1.getMap("map2");
        var threads2 = new Thread[3];
        map2.put("optimistic_lock", 0);
        for (int i = 0; i < 3; i++) {
            threads2[i] = new Thread(() -> {
                for (int j = 0; j < TIMES_TO_INCREMENT; j++) {
                    for (; ; ) {
                        var value = map2.get("optimistic_lock");
                        if (map2.replace("optimistic_lock", value, (int) value + 1)) {
                            break;
                        }
                    }
                }
            });
            threads2[i].start();
        }
        for (int i = 0; i < 3; i++) {
            try {
                threads2[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        var map3 = hazelcastInstance1.getMap("map3");
        var threads3 = new Thread[3];
        map3.put("pessimistic_lock", 0);
        for (int i = 0; i < 3; i++) {
            threads3[i] = new Thread(() -> {
                for (int j = 0; j < TIMES_TO_INCREMENT; j++) {
                    map3.lock("pessimistic_lock");
                    var value = map3.get("pessimistic_lock");
                    map3.put("pessimistic_lock", (int) value + 1);
                    map3.unlock("pessimistic_lock");
                }
            });
            threads3[i].start();
        }
        for (int i = 0; i < 3; i++) {
            try {
                threads3[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Value processed without lock: " + map.get("no_lock"));
        System.out.println("Value processed with optimistic lock: " + map2.get("optimistic_lock"));
        System.out.println("Value processed with pessimistic lock: " + map3.get("pessimistic_lock"));
    }
}
