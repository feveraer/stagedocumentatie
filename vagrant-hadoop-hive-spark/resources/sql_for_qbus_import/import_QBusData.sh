#!/bin/bash

echo " ==================================="
echo "| Creating EXT tables              |"
echo " ==================================="

hive -f /vagrant/resources/sql_for_qbus_import/create_EXT_table_Controllers.sql
hive -f /vagrant/resources/sql_for_qbus_import/create_EXT_table_OutputDownloadedLogs.sql
hive -f /vagrant/resources/sql_for_qbus_import/create_EXT_table_OutputLogs.sql
hive -f /vagrant/resources/sql_for_qbus_import/create_EXT_table_Outputs.sql
hive -f /vagrant/resources/sql_for_qbus_import/create_EXT_table_Types.sql

echo
echo " ==================================="
echo "| Creating ORC tables              |"
echo " ==================================="

hive -f /vagrant/resources/sql_for_qbus_import/create_ORC_table_Controllers.sql
hive -f /vagrant/resources/sql_for_qbus_import/create_ORC_table_OutputDownloadedLogs.sql
hive -f /vagrant/resources/sql_for_qbus_import/create_ORC_table_OutputLogs.sql
hive -f /vagrant/resources/sql_for_qbus_import/create_ORC_table_Outputs.sql
hive -f /vagrant/resources/sql_for_qbus_import/create_ORC_table_Types.sql

echo
echo " ==================================="
echo "| Moving from EXT to ORC           |"
echo " ==================================="

hive -f /vagrant/resources/sql_for_qbus_import/moving_from_EXT_to_ORC.sql
