#!/bin/bash

bold=$(tput bold)
normal=$(tput sgr0)

echo " ==================================="
echo "| Updating the source list ...      |"
echo " ==================================="
sudo apt-get -qq update

echo
echo " ==================================="
echo "| Installing Java 7 ...             |"
echo " ==================================="
sudo apt-get -qq install -y default-jdk

#echo "Installing SSH"
#sudo apt-get install -y openssh-server

# echo "Adding a dedicated Hadoop user"
# sudo addgroup hadoop
# sudo adduser --ingroup hadoop hadoop

echo
echo " ==================================="
echo "| Downloading Hadoop 2.7.2 ...      |"
echo " ==================================="
wget -q http://apache.cu.be/hadoop/common/hadoop-2.7.2/hadoop-2.7.2.tar.gz

echo
echo " ==================================="
echo "| Extracting Hadoop ...             |"
echo " ==================================="
sudo tar -xzf hadoop-2.7.2.tar.gz -C /usr/local/lib/

# echo "Create HDFS directories"
# sudo mkdir -p /var/lib/hadoop/hdfs/namenode
# sudo mkdir -p /var/lib/hadoop/hdfs/datanode
# sudo chown -R hadoop /var/lib/hadoop
#
# echo "Log in as the hadoop user"
# sudo su - hadoop

echo
echo " ==================================="
echo "| Configuring Hadoop ...            |"
echo " ==================================="

echo "export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64/jre" >> /home/vagrant/.bashrc
echo "export HADOOP_INSTALL=/usr/local/lib/hadoop-2.7.2" >> /home/vagrant/.bashrc
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

mkdir /usr/local/lib/hadoop-2.7.2/logs
sudo chown -fR vagrant /usr/local/lib/hadoop-2.7.2

echo
echo " ==================================="
echo "| Setting up /etc/profile ...       |"
echo " ==================================="
cp -f /vagrant/resources/profile /etc/profile
source /etc/profile

echo
echo " ==================================="
echo "| Downloading Hive 1.2 ...          |"
echo " ==================================="
wget -q ftp://apache.belnet.be/mirrors/ftp.apache.org/hive/hive-1.2.1/apache-hive-1.2.1-bin.tar.gz

echo
echo " ==================================="
echo "| Extracting Hive ...               |"
echo " ==================================="
sudo tar -xzf apache-hive-1.2.1-bin.tar.gz -C /usr/local/lib
sudo chown -R vagrant /usr/local/lib/apache-hive-1.2.1-bin

echo
echo " ==================================="
echo "| Downloading Spark 1.6 ...         |"
echo " ==================================="
wget -q http://apache.cu.be/spark/spark-1.6.0/spark-1.6.0-bin-hadoop2.6.tgz

echo
echo " ==================================="
echo "| Extracting Spark ...              |"
echo " ==================================="
sudo tar -xf spark-1.6.0-bin-hadoop2.6.tgz -C /opt
sudo chown -R vagrant /opt/spark-1.6.0-bin-hadoop2.6

echo
echo " ==================================="
echo "| Configuring Spark ...             |"
echo " ==================================="
cp -f /vagrant/resources/spark-env.sh /opt/spark-1.6.0-bin-hadoop2.6/conf
cp -f /vagrant/resources/spark-defaults.conf /opt/spark-1.6.0-bin-hadoop2.6/conf

echo
echo " ==================================="
echo "| Preparing help scripts    ...     |"
echo " ==================================="
cp -f /vagrant/resources/init-hadoop.sh /usr/local/lib/hadoop-2.7.2/sbin
cp -f /vagrant/resources/ssh-passphraseless.sh /usr/local/lib/hadoop-2.7.2/sbin
cp -f /vagrant/resources/init-hive.sh /usr/local/lib/hadoop-2.7.2/sbin

echo
echo "${bold}SYSTEM ALIVE AND KICKING!"
echo "${normal}"
echo "Now to get Hadoop up and running, execute:"
echo "${bold}  ssh-passphraseless.sh"
echo "${normal}"
echo "${bold}  init-hadoop.sh"
echo "${normal}  this will format the namenode and execute start-all.sh"
echo
echo "To initialize Hive directories, execute:"
echo "${bold}  init-hive.sh"
echo "${normal}"