#!/bin/bash

echo "Provisioning virtual machine..."
echo "Update the source list"
sudo apt-get update

echo "Installing java"
sudo apt-get install -y default-jdk

#echo "Installing SSH"
#sudo apt-get install -y openssh-server

# echo "Adding a dedicated Hadoop user"
# sudo addgroup hadoop
# sudo adduser --ingroup hadoop hadoop

echo "Download and extract Hadoop"
wget http://apache.cu.be/hadoop/common/hadoop-2.7.2/hadoop-2.7.2.tar.gz
sudo tar -xzvf hadoop-2.7.2.tar.gz -C /usr/local/lib/
sudo chown -R vagrant /usr/local/lib/hadoop-2.7.2

# echo "Create HDFS directories"
# sudo mkdir -p /var/lib/hadoop/hdfs/namenode
# sudo mkdir -p /var/lib/hadoop/hdfs/datanode
# sudo chown -R hadoop /var/lib/hadoop
#
# echo "Log in as the hadoop user"
# sudo su - hadoop

#echo "Create SSH key and add it to authorized keys"
#ssh-keygen -t rsa -P "" -f ~/.ssh/id_rsa
# ssh-copy-id -i ~/.ssh/id_rsa localhost
echo "Configure hadoop"

echo "export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64/jre" >> /home/vagrant/.bashrc
echo "export HADOOP_INSTALL=/usr/local/lib/hadoop-2.7.2" >> /home/vagrant/.bashrc
# echo "export PATH=$PATH:/usr/local/lib/hadoop-2.7.2/bin" >> /home/vagrant/.bashrc
# echo "export PATH=$PATH:/usr/local/lib/hadoop-2.7.2/sbin" >> /home/vagrant/.bashrc
echo "export HADOOP_MAPRED_HOME=/usr/local/lib/hadoop-2.7.2" >> /home/vagrant/.bashrc
echo "export HADOOP_COMMON_HOME=/usr/local/lib/hadoop-2.7.2" >> /home/vagrant/.bashrc
echo "export HADOOP_HDFS_HOME=/usr/local/lib/hadoop-2.7.2" >> /home/vagrant/.bashrc
echo "export YARN_HOME=/usr/local/lib/hadoop-2.7.2" >> /home/vagrant/.bashrc
echo "export HADOOP_COMMON_LIB_NATIVE_DIR=/usr/local/lib/hadoop-2.7.2/lib/native" >> /home/vagrant/.bashrc
echo "export HADOOP_OPTS="-Djava.library.path=/usr/local/lib/hadoop-2.7.2/lib/native"" >> /home/vagrant/.bashrc

source /home/vagrant/.bashrc

cp -f /vagrant/resources/hadoop-env.sh /usr/local/lib/hadoop-2.7.2/etc/hadoop
cp -f /vagrant/resources/core-site.xml /usr/local/lib/hadoop-2.7.2/etc/hadoop
cp -f /vagrant/resources/yarn-site.xml /usr/local/lib/hadoop-2.7.2/etc/hadoop
cp -f /vagrant/resources/mapred-site.xml /usr/local/lib/hadoop-2.7.2/etc/hadoop
cp -f /vagrant/resources/hdfs-site.xml /usr/local/lib/hadoop-2.7.2/etc/hadoop

echo "Do nodeformat"
hdfs namenode -format

echo "Starting hadoop"

start-dfs.sh
start-yarn.sh

echo "Make HDFS home directory"
hadoop fs -mkdir -p /user/vagrant

echo "Downloading Hive"
wget ftp://apache.belnet.be/mirrors/ftp.apache.org/hive/hive-1.2.1/apache-hive-1.2.1-bin.tar.gz

echo "Extracting Hive"
sudo tar -xzf apache-hive-1.2.1-bin.tar.gz -C /usr/local/lib
sudo chown -R vagrant /usr/local/lib/apache-hive-1.2.1-bin

echo "Setting up environment for Hive"
cp -f /vagrant/resources/profile /etc/profile
source /etc/profile

echo "Verifying Hive"
hadoop fs -mkdir /tmp
hadoop fs -mkdir -p /user/hive/warehouse
hadoop fs -chmod g+w /tmp
hadoop fs -chmod g+w /user/hive/warehouse

echo "Downloading Spark 1.6.0"
wget http://apache.cu.be/spark/spark-1.6.0/spark-1.6.0-bin-hadoop2.6.tgz

echo "Extracting Spark"
sudo tar -xf spark-1.6.0-bin-hadoop2.6.tgz -C /opt
sudo chown -R vagrant /opt/spark-1.6.0-bin-hadoop2.6

echo "Configuration in spark-env.sh"
cp /opt/spark-1.6.0-bin-hadoop2.6/conf/spark-env.sh.template /opt/spark-1.6.0-bin-hadoop2.6/conf/spark-env.sh

echo "I'm alive and kicking"
echo "If errors with Hadoop:"
echo "  sudo chown -R vagrant /usr/local/lib/hadoop-2.7.2"
echo "  hdfs namenode -format"
echo "  start-dfs.sh"
echo "  start-yarn.sh"
echo "If errors with Hive:"
echo "  sudo chown -R vagrant /usr/local/lib/apache-hive-1.2.1-bin"
echo "  source /etc/profile"
echo "  hadoop fs -mkdir /tmp"
echo "  hadoop fs -mkdir -p /user/hive/warehouse"
echo "  hadoop fs -chmod g+w /tmp"
echo "  hadoop fs -chmod g+w /user/hive/warehouse"
