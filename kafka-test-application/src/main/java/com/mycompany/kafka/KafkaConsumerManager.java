/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.kafka;

import java.util.Arrays;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

/**
 *
 * @author vagrant
 */
public class KafkaConsumerManager {

    private KafkaConsumer<String, String> consumer;
    
    private static final String KAFKASERVER = "192.168.22.10:9092";
    //private static final String ZOOKEEPER = "192.168.22.10:2181";

    public KafkaConsumerManager() {
        Properties props = new Properties();
        props.put("bootstrap.servers", KAFKASERVER);
        props.put("group.id", "testGroup");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
//        props.put("broker.id", 0);
//        props.put("zookeeper.connect", ZOOKEEPER);
        
        consumer = new KafkaConsumer<>(props);
    }

    public void start() {
        System.out.println("Start Consumer");
        consumer.subscribe(Arrays.asList("test"));
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("Consumer: offset = %d, key = %s, value = %s \n", record.offset(), record.key(), record.value());
            }
        }
    }
}
