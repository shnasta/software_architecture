package org.ucu.apps;

import com.hazelcast.config.Config;
import com.hazelcast.config.QueueConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

public class BoundedQueue {
    public static void main(String[] args) {
        Config config = new Config();
        config.setClusterName("dev");

        HazelcastInstance hazelcastInstance1 = Hazelcast.newHazelcastInstance(config);
        HazelcastInstance hazelcastInstance2 = Hazelcast.newHazelcastInstance(config);
        HazelcastInstance hazelcastInstance3 = Hazelcast.newHazelcastInstance(config);

        QueueConfig queueConfig = new QueueConfig();
        queueConfig.setName("boundedQueue").setMaxSize(10);
        config.addQueueConfig(queueConfig);

        var queue = hazelcastInstance1.getQueue("boundedQueue");
        var threads = new Thread[3];

        threads[0] = new Thread(() -> {
            for (int i = 0; i < 120; i++) {
                queue.offer(i);
                System.out.println("Offered: " + i);
            }
        });
        for (int i = 1; i < 3; i++) {
            int finalI = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 50; j++) {
                    try {
                        System.out.println(Thread.currentThread().getId() + " (" + finalI + "): " + queue.take());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        for (int i = 0; i < 3; i++) {
            threads[i].start();
        }

        for (int i = 0; i < 3; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
