#!/bin/bash

echo "Download zeppelin"
wget http://apache.cu.be/incubator/zeppelin/0.5.6-incubating/zeppelin-0.5.6-incubating-bin-all.tgz

echo
echo "Extract zeppelin"
sudo tar xf zeppelin-0.5.6-incubating-bin-all.tgz -C /opt/
rm zeppelin-0.5.6-incubating-bin-all.tgz

echo
echo "Configure zeppelin"
sudo chown -R vagrant /opt/zeppelin-0.5.6-incubating-bin-all/
export SPARK_HOME=/opt/spark-1.6.0-bin-hadoop.2.6/
sudo echo "SPARK_HOME=/opt/spark-1.6.0-bin-hadoop.2.6/" >> /etc/profile
cp /opt/zeppelin-0.5.6-incubating-bin-all/conf/zeppelin-env.sh.template /opt/zeppelin-0.5.6-incubating-bin-all/conf/zeppelin-env.sh
echo "export SPARK_HOME=$SPARK_HOME" >> /opt/zeppelin-0.5.6-incubating-bin-all/conf/zeppelin-env.sh
echo "export HADOOP_CONF_DIR=$HADOOP_INSTALL/etc/hadoop/" >> /opt/zeppelin-0.5.6-incubating-bin-all/conf/zeppelin-env.sh
echo "export HADOOP_HOME=$HADOOP_INSTALL" >> /opt/zeppelin-0.5.6-incubating-bin-all/conf/zeppelin-env.sh
cp /usr/local/lib/apache-hive-1.2.1-bin/conf/hive-site.xml /opt/zeppelin-0.5.6-incubating-bin-all/conf/
export PATH=$PATH:/opt/zeppelin-0.5.6-incubating-bin-all/bin/
sudo echo "PATH=$PATH:/opt/zeppelin-0.5.6-incubating-bin-all/bin/" >> /etc/profile
