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
echo "export PATH=$PATH:/usr/local/lib/hadoop-2.7.2/bin" >> /home/vagrant/.bashrc
echo "export PATH=$PATH:/usr/local/lib/hadoop-2.7.2/sbin" >> /home/vagrant/.bashrc
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

#export PATH=$PATH:/usr/local/lib/hadoop-2.7.2/bin/

hdfs namenode -format

start-dfs.sh
start-yarn.sh

echo "I'm alive and kicking"
