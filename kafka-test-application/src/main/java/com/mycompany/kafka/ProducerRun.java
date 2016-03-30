/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.kafka;

import java.io.IOException;

/**
 *
 * @author vagrant
 */
public class ProducerRun {
    public static void main(String[] args) {
        KafkaProducerManager producer = new KafkaProducerManager();
        producer.startTextSender();
    }
}
