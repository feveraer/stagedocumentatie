/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.kafka;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

/**
 *
 * @author vagrant
 */
public class KafkaProducerManager {
    private Producer<String, String> producer;
    private static final String KAFKASERVER = "192.168.22.10:9092";
//    private static final String ZOOKEEPER = "192.168.22.10:2181";
    
    public KafkaProducerManager() {
        Properties props = new Properties();
        props.put("bootstrap.servers", KAFKASERVER);
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("auto.commit.interval.ms", 1000);
        props.put("linger.ms", 1);
        props.put("block.on.buffer.full", "true");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//        props.put("broker.id", 0);
//        props.put("zookeeper.connect", ZOOKEEPER);

        producer = new KafkaProducer<>(props);
    }

    public void startCounter(){
        System.out.println("Start Producer Counter");
        for(int i = 0; i < 10000; i++){
            producer.send(new ProducerRecord<>("test", Integer.toString(i),"Package " + i));
            System.out.println("Producer: Send: " + i);
        }
        
        System.out.println("Closing producer");
        producer.close();
    }
    
    public void startTextSender(){
        String path_to_file = "/home/vagrant/NetBeansProjects/Kafka/src/main/java/file.txt";
        int count = 0;
        try {
            for (String line : Files.readAllLines(Paths.get(path_to_file), StandardCharsets.UTF_8)) {
                count++;
                if(count%5 == 0){
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(KafkaProducerManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                producer.send(new ProducerRecord<>("test", Integer.toString(count), line));
                System.out.println(line);
            }
        } catch (IOException ex) {
            Logger.getLogger(KafkaProducerManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("Closing producer");
        producer.close();
    }
}
