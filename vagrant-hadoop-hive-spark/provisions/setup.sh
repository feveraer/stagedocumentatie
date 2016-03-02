#!/bin/bash

echo "Provisioning virtual machine..."
echo "Update the source list"
sudo apt-get update

echo "Installing java"
sudo apt-get install -y default-jdk

echo "Installing SSH"
sudo apt-get install -y openssh-server

echo "Adding a dedicated Hadoop user"
sudo addgroup hadoop
sudo adduser --ingroup hadoop hadoop

echo "Download and extract Hadoop"
wget http://apache.rediris.es/hadoop/common/hadoop-2.7.0/hadoop-2.7.0.tar.gz
sudo tar -xzvf hadoop-2.7.0.tar.gz -C /usr/local/lib/
sudo chown -R hadoop:hadoop /usr/local/lib/hadoop-2.7.0

echo "Create HDFS directories"
sudo mkdir -p /var/lib/hadoop/hdfs/namenode
sudo mkdir -p /var/lib/hadoop/hdfs/datanode
sudo chown -R hadoop /var/lib/hadoop

echo "Log in as the hadoop user"
sudo su - hadoop

echo "Create SSH key and add it to authorized keys"
ssh-keygen -t rsa -P ""
ssh-copy-id -i ~/.ssh/id_rsa localhost

echo "I'm alive and kicking
