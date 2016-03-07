#!/bin/bash

echo "hdfs namenode -format"
hdfs namenode -format

echo "Starting hadoop ..."
start-all.sh