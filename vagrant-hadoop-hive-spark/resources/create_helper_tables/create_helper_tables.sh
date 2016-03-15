#bin/bash
echo "This script will only execute correctly if the hive tables are created"

spark-shell -i createCombinedTables.scala

echo "Tables setTemps and measuredTemps are created in Hive"
