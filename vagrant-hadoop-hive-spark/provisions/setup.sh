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

export PATH=$PATH:/usr/local/lib/hadoop-2.7.2/bin/
export PATH=$PATH:/usr/local/lib/hadoop-2.7.2/sbin/

hdfs namenode -format

echo "Starting hadoop"

start-dfs.sh
start-yarn.sh

echo "Make HDFS home directory"
hadoop fs mkdir -p /user/vagrant

echo "Downloading Hive"
wget ftp://apache.belnet.be/mirrors/ftp.apache.org/hive/hive-2.0.0/apache-hive-2.0.0-bin.tar.gz

echo "Extracting Hive"
sudo tar -xzvf apache-hive-2.0.0-bin.tar.gz -C /usr/local/lib
sudo chown -R vagrant /usr/local/lib/apache-hive-2.0.0-bin

echo "Setting up environment for Hive"
echo "export HIVE_HOME=/usr/local/lib/apache-hive-2.0.0-bin" >> /home/vagrant/.bashrc
source /home/vagrant/.bashrc
export PATH=$PATH:$HIVE_HOME/bin
export CLASSPATH=$CLASSPATH:/usr/local/lib/hadoop-2.7.2/lib/*:.
export CLASSPATH=$CLASSPATH:/usr/local/lib/apache-hive-2.0.0-bin /lib/*:
cp -f /vagrant/resources/hive-env.sh /usr/local/lib/apache-hive-2.0.0-bin/conf

echo "Downloading Apache Derby"
wget http://apache.cu.be//db/derby/db-derby-10.12.1.1/db-derby-10.12.1.1-bin.tar.gz
echo "Extracting Apache derby"
sudo tar -xzvf db-derby-10.12.1.1-bin.tar.gz -C /usr/local/lib
sudo chown -R vagrant /usr/local/lib/db-derby-10.12.1.1-bin

echo "Configuring Apache Derby"
echo "export DERBY_HOME=/usr/local/lib/db-derby-10.12.1.1-bin" >> /home/vagrant/.bashrc
source /home/vagrant/.bashrc
export PATH=$PATH:$DERBY_HOME/bin
export CLASSPATH=$CLASSPATH:$DERBY_HOME/lib/derby.jar:$DERBY_HOME/lib/derbytools.jar
mkdir $DERBY_HOME/data

echo "Configuring Metastore of Hive"
cp -f /vagrant/resources/hive-site.xml /usr/local/lib/apache-hive-2.0.0-bin/conf
cp -f /vagrant/resources/jpox.properties /usr/local/lib/apache-hive-2.0.0-bin/conf

echo "Verifying Hive"
hadoop fs -mkdir /tmp
hadoop fs -mkdir /user/hive/warehouse
hadoop fs -chmod g+w /tmp
hadoop fs -chmod g+w /user/hive/warehouse

echo "Downloading Spark 1.6.0"


echo "I'm alive and kicking"
