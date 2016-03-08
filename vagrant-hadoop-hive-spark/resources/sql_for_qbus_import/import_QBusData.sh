#!/bin/bash

echo " ==================================="
echo "| Copying SQL files to VM          |"
echo " ==================================="
mkdir -p /home/vagrant/qbus_import
cp /vagrant/resources/sql_for_qbus_import/create_EXT_table_Controllers.sql /home/vagrant/qbus_import
cp /vagrant/resources/sql_for_qbus_import/create_EXT_table_OutputDownloadedLogs.sql /home/vagrant/qbus_import
cp /vagrant/resources/sql_for_qbus_import/create_EXT_table_OutputLogs.sql /home/vagrant/qbus_import
cp /vagrant/resources/sql_for_qbus_import/create_EXT_table_Outputs.sql /home/vagrant/qbus_import
cp /vagrant/resources/sql_for_qbus_import/create_EXT_table_Types.sql /home/vagrant/qbus_import
cp /vagrant/resources/sql_for_qbus_import/create_ORC_table_Controllers.sql /home/vagrant/qbus_import
cp /vagrant/resources/sql_for_qbus_import/create_ORC_table_OutputDownloadedLogs.sql /home/vagrant/qbus_import
cp /vagrant/resources/sql_for_qbus_import/create_ORC_table_OutputLogs.sql /home/vagrant/qbus_import
cp /vagrant/resources/sql_for_qbus_import/create_ORC_table_Outputs.sql /home/vagrant/qbus_import
cp /vagrant/resources/sql_for_qbus_import/create_ORC_table_Types.sql /home/vagrant/qbus_import
cp /vagrant/resources/sql_for_qbus_import/moving_from_EXT_to_ORC.sql /home/vagrant/qbus_import

echo
echo " ==================================="
echo "| Copying csv files to VM          |"
echo " ==================================="

mkdir -p /home/vagrant/qbus_import/csv
cp -r /vagrant/resources/csv/controllers /home/vagrant/qbus_import/csv
cp -r /vagrant/resources/csv/outputdownloadedlogs /home/vagrant/qbus_import/csv
cp -r /vagrant/resources/csv/outputlogs /home/vagrant/qbus_import/csv
cp -r /vagrant/resources/csv/outputs /home/vagrant/qbus_import/csv
cp -r /vagrant/resources/csv/types /home/vagrant/qbus_import/csv

echo
echo " ==================================="
echo "| Creating EXT tables              |"
echo " ==================================="

hive -f /home/vagrant/qbus_import/create_EXT_table_Controllers.sql
hive -f /home/vagrant/qbus_import/create_EXT_table_OutputDownloadedLogs.sql
hive -f /home/vagrant/qbus_import/create_EXT_table_OutputLogs.sql
hive -f /home/vagrant/qbus_import/create_EXT_table_Outputs.sql
hive -f /home/vagrant/qbus_import/create_EXT_table_Types.sql

echo
echo " ==================================="
echo "| Creating ORC tables              |"
echo " ==================================="

hive -f /home/vagrant/qbus_import/create_ORC_table_Controllers.sql
hive -f /home/vagrant/qbus_import/create_ORC_table_OutputDownloadedLogs.sql
hive -f /home/vagrant/qbus_import/create_ORC_table_OutputLogs.sql
hive -f /home/vagrant/qbus_import/create_ORC_table_Outputs.sql
hive -f /home/vagrant/qbus_import/create_ORC_table_Types.sql

echo
echo " ==================================="
echo "| Moving from EXT to ORC           |"
echo " ==================================="

hive -f /home/vagrant/qbus_import/moving_from_EXT_to_ORC.sql
