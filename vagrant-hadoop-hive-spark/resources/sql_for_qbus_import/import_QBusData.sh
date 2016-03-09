#!/bin/bash

echo " ==================================="
echo "| Copying SQL files to VM           |"
echo " ==================================="
mkdir -p /home/vagrant/qbus_import
cp /vagrant/resources/sql_for_qbus_import/create_EXT_table_Controllers.sql /home/vagrant/qbus_import
cp /vagrant/resources/sql_for_qbus_import/create_EXT_table_Locations.sql /home/vagrant/qbus_import
cp /vagrant/resources/sql_for_qbus_import/create_EXT_table_OutputDownloadedLogs.sql /home/vagrant/qbus_import
cp /vagrant/resources/sql_for_qbus_import/create_EXT_table_OutputLogs.sql /home/vagrant/qbus_import
cp /vagrant/resources/sql_for_qbus_import/create_EXT_table_Outputs.sql /home/vagrant/qbus_import
cp /vagrant/resources/sql_for_qbus_import/create_EXT_table_Types.sql /home/vagrant/qbus_import
cp /vagrant/resources/sql_for_qbus_import/create_ORC_table_Controllers.sql /home/vagrant/qbus_import
cp /vagrant/resources/sql_for_qbus_import/create_ORC_table_Locations.sql /home/vagrant/qbus_import
cp /vagrant/resources/sql_for_qbus_import/create_ORC_table_OutputDownloadedLogs.sql /home/vagrant/qbus_import
cp /vagrant/resources/sql_for_qbus_import/create_ORC_table_OutputLogs.sql /home/vagrant/qbus_import
cp /vagrant/resources/sql_for_qbus_import/create_ORC_table_Outputs.sql /home/vagrant/qbus_import
cp /vagrant/resources/sql_for_qbus_import/create_ORC_table_Types.sql /home/vagrant/qbus_import
cp /vagrant/resources/sql_for_qbus_import/moving_from_EXT_to_ORC.sql /home/vagrant/qbus_import

echo
echo " ==================================="
echo "| Copying csv files to VM           |"
echo " ==================================="

mkdir -p /home/vagrant/qbus_import/csv
cp -r /vagrant/resources/csv/controllers /home/vagrant/qbus_import/csv
cp -r /vagrant/resources/csv/locations /home/vagrant/qbus_import/csv
cp -r /vagrant/resources/csv/outputdownloadedlogs /home/vagrant/qbus_import/csv
cp -r /vagrant/resources/csv/outputlogs /home/vagrant/qbus_import/csv
cp -r /vagrant/resources/csv/outputs /home/vagrant/qbus_import/csv
cp -r /vagrant/resources/csv/types /home/vagrant/qbus_import/csv

echo
echo " ==================================="
echo "| Removing headers from csv         |"
echo " ==================================="
sed -i 1d /home/vagrant/qbus_import/csv/controllers/Controllers.csv
sed -i 1d /home/vagrant/qbus_import/csv/locations/Locations.csv
sed -i 1d /home/vagrant/qbus_import/csv/outputdownloadedlogs/OutputDownloadedLogs.csv
sed -i 1d /home/vagrant/qbus_import/csv/outputlogs/OutputLogs.csv
sed -i 1d /home/vagrant/qbus_import/csv/outputs/Outputs.csv
sed -i 1d /home/vagrant/qbus_import/csv/types/Types.csv

echo
echo " ==================================="
echo "| Copying csv files to Hadoop       |"
echo " ==================================="

hadoop fs -mkdir -p /input/csv

hadoop fs -put /home/vagrant/qbus_import/csv/controllers /input/csv
hadoop fs -put /home/vagrant/qbus_import/csv/locations /input/csv
hadoop fs -put /home/vagrant/qbus_import/csv/outputdownloadedlogs /input/csv
hadoop fs -put /home/vagrant/qbus_import/csv/outputlogs /input/csv
hadoop fs -put /home/vagrant/qbus_import/csv/outputs /input/csv
hadoop fs -put /home/vagrant/qbus_import/csv/types /input/csv

echo
echo " ==================================="
echo "| Creating EXT tables               |"
echo " ==================================="

hive -f /home/vagrant/qbus_import/create_EXT_table_Controllers.sql
hive -f /home/vagrant/qbus_import/create_EXT_table_Locations.sql
hive -f /home/vagrant/qbus_import/create_EXT_table_OutputDownloadedLogs.sql
hive -f /home/vagrant/qbus_import/create_EXT_table_OutputLogs.sql
hive -f /home/vagrant/qbus_import/create_EXT_table_Outputs.sql
hive -f /home/vagrant/qbus_import/create_EXT_table_Types.sql

echo
echo " ==================================="
echo "| Creating ORC tables               |"
echo " ==================================="

hive -f /home/vagrant/qbus_import/create_ORC_table_Controllers.sql
hive -f /home/vagrant/qbus_import/create_ORC_table_Locations.sql
hive -f /home/vagrant/qbus_import/create_ORC_table_OutputDownloadedLogs.sql
hive -f /home/vagrant/qbus_import/create_ORC_table_OutputLogs.sql
hive -f /home/vagrant/qbus_import/create_ORC_table_Outputs.sql
hive -f /home/vagrant/qbus_import/create_ORC_table_Types.sql

echo
echo " ==================================="
echo "| Moving from EXT to ORC            |"
echo " ==================================="

hive -f /home/vagrant/qbus_import/moving_from_EXT_to_ORC.sql

echo
echo " ==================================="
echo "| Removing import files             |"
echo " ==================================="

rm -r /home/vagrant/qbus_import
