#!/bin/bash

echo " ==================================="
echo "| Updating the source list ...      |"
echo " ==================================="
sudo apt-get -qq update

echo " ==================================="
echo "| Installing Java 7 ...             |"
echo " ==================================="
sudo apt-get install -y default-jdk > /dev/null

echo " ==================================="
echo "| Downloading Kafka ...             |"
echo " ==================================="
wget --progress=bar:force http://apache.belnet.be/kafka/0.9.0.0/kafka_2.11-0.9.0.0.tgz

echo " ==================================="
echo "| Extracting Kafka ...              |"
echo " ==================================="
sudo tar -xzf kafka_2.11-0.9.0.0.tgz -C /opt
rm -f kafka_2.11-0.9.0.0.tgz
sudo chown -R vagrant /opt/kafka_2.11-0.9.0.0

echo " ==================================="
echo "| Setting up /etc/profile ...       |"
echo " ==================================="
cp -f /vagrant/resources/profile /etc/profile
source /etc/profile
