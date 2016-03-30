/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.kafka;

/**
 *
 * @author vagrant
 */
public class ConsumerRun {
    public static void main(String[] args) {
        KafkaConsumerManager consumer = new KafkaConsumerManager();
        consumer.start();
    }
}
