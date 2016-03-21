#!/bin/bash

DATABASE_NAME=""

usage() {
  echo "This script needs to know the database name to be queried."
  echo -e "\nUsage: $0 -d <database_name> \n"
}

if [ $# -le 1  ]
then
  usage
  exit 1
fi

while getopts ":d:" opt; do
  case $opt in
    d)
      DATABASE_NAME="${OPTARG}"
      ;;
    \?)
      echo "invalid option: -$OPTARG" >&2
      exit 1
      ;;
    :)
      echo "option -$OPTARG requires an argument" >&2
      exit 1
      ;;
  esac
done

echo
read -r -p "MySQL username: " MYSQL_USER
read -s -r -p "MySQL password: " MYSQL_PASSWORD
echo
read -r -p "Time column name: " TIME_COLUMN
echo

for tb in $(mysql -u $MYSQL_USER --password=$MYSQL_PASSWORD $DATABASE_NAME -sN -e "SHOW TABLES")
do
  echo "${tb}" >> /home/twitter/query_results/${DATABASE_NAME}_Qs.txt
  echo "select min(${TIME_COLUMN}),max(${TIME_COLUMN}),count(*) from ${tb} where and ${TIME_COLUMN} > '2015-01-01 00:00:00' and ${TIME_COLUMN} < '2015-04-01 00:00:00'" >> /home/twitter/query_results/${DATABASE_NAME}_Qs.txt
  sudo mysql -B -u $MYSQL_USER --password=$MYSQL_PASSWORD $DATABASE_NAME -e "select count(*) from ${tb} where ${TIME_COLUMN} > '2015-01-01 00:00:00' and ${TIME_COLUMN} < '2015-04-01 00:00:00' into outfile '/home/twitter/query_results/${tb}_count.txt'"
  cat /home/twitter/query_results/${tb}_count.txt >> /home/twitter/query_results/${DATABASE_NAME}_Qs.txt
  echo >> /home/twitter/query_results/${DATABASE_NAME}_Qs.txt
  rm -f /home/twitter/query_results/${tb}_count.txt

  echo "select min(${TIME_COLUMN}),max(${TIME_COLUMN}),count(*) from ${tb} where and ${TIME_COLUMN} > '2015-04-01 00:00:00' and ${TIME_COLUMN} < '2015-07-01 00:00:00'" >> /home/twitter/query_results/${DATABASE_NAME}_Qs.txt
  sudo mysql -B -u $MYSQL_USER --password=$MYSQL_PASSWORD $DATABASE_NAME -e "select count(*) from ${tb} where ${TIME_COLUMN} > '2015-04-01 00:00:00' and ${TIME_COLUMN} < '2015-07-01 00:00:00' into outfile '/home/twitter/query_results/${tb}_count.txt'"
  cat /home/twitter/query_results/${tb}_count.txt >> /home/twitter/query_results/${DATABASE_NAME}_Qs.txt
  echo >> /home/twitter/query_results/${DATABASE_NAME}_Qs.txt
  rm -f /home/twitter/query_results/${tb}_count.txt

  echo "select min(${TIME_COLUMN}),max(${TIME_COLUMN}),count(*) from ${tb} where and ${TIME_COLUMN} > '2015-07-01 00:00:00' and ${TIME_COLUMN} < '2015-10-01 00:00:00'" >> /home/twitter/query_results/${DATABASE_NAME}_Qs.txt
  sudo mysql -B -u $MYSQL_USER --password=$MYSQL_PASSWORD $DATABASE_NAME -e "select count(*) from ${tb} where ${TIME_COLUMN} > '2015-07-01 00:00:00' and ${TIME_COLUMN} < '2015-10-01 00:00:00' into outfile '/home/twitter/query_results/${tb}_count.txt'"
  cat /home/twitter/query_results/${tb}_count.txt >> /home/twitter/query_results/${DATABASE_NAME}_Qs.txt
  echo >> /home/twitter/query_results/${DATABASE_NAME}_Qs.txt
  rm -f /home/twitter/query_results/${tb}_count.txt

  echo >> /home/twitter/query_results/${DATABASE_NAME}_Qs.txt
done
