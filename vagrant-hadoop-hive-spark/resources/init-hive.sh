#!/bin/bash

echo "hadoop fs -mkdir /tmp"
hadoop fs -mkdir /tmp

echo "hadoop fs -mkdir -p /user/hive/warehouse"
hadoop fs -mkdir -p /user/hive/warehouse

echo "hadoop fs -chmod g+w /tmp"
hadoop fs -chmod g+w /tmp

echo "hadoop fs -chmod g+w /user/hive/warehouse"
hadoop fs -chmod g+w /user/hive/warehouse