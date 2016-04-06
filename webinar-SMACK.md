# How to build data pipelines for Real-time applications with SMACK an apache Kafka
## SMACK

Spark, Mesos, Akka, Cassandra, Kafka

- Organize: Kafka
- Process: Spark, Akka
- Store: Cassandra

Mesos organizes organizes the resources in an effectively

### Kafka
Kafka decouples data pipelines

Guarantees
- Messages are ordered as they are sent by the producer
- Consumers see messages in the order they were inserted by the producer

Durability
- Messages are delivered at least once

4 may Spark streaming

18 may storage strategies

1 june analysing data with spark

www.datastax.com/resources/webinars

@PatrickMcFadin

Kafka Multidatacenter is not a good idea, keep things as local as possible.
Run Kafka on another place.
You don't run Spark in a multiple datacenters
