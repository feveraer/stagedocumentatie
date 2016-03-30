# Kafka-test-application

This application is meant to be run on the develop machine that is provided in this repository.

The application connects to the vagrant-kafka machine, also provided in this repo.

## Issue and solution

In order for this application to connect to the kafka server (vagrant-kafka) you need to change the server.properties file located in /opt/kafka_2.11-0.9.0.0/config/ on the kafka server.

The following properties need to be changed:

```text
port=9092

host.name=192.168.22.10

advertised.host.name=192.168.22.10
```
