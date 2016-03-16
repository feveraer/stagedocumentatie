#!/bin/bash

DATABASE_NAME=""

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

MYSQL_USER="root"
MYSQL_PASSWORD="Ugent2016"

# echo "MySQL user"
# read MYSQL_USER
# echo "Enter password"
# read MYSQL_PASSWORD

for tb in $(mysql -u $MYSQL_USER --password=$MYSQL_PASSWORD $DATABASE_NAME -sN -e "SHOW TABLES")
do
  echo "${tb}" >> /home/twitter/query_results/${DATABASE_NAME}_Qs.txt
  echo "select count(*) from ${tb} where and timestamp > '2015-01-01 00:00:00' and timestamp < '2015-04-01 00:00:00'" >> /home/twitter/query_results/${DATABASE_NAME}_Qs.txt
  sudo mysql -B -u $MYSQL_USER --password=$MYSQL_PASSWORD $DATABASE_NAME -e "select count(*) from ${tb} where timestamp > '2015-01-01 00:00:00' and timestamp < '2015-04-01 00:00:00' into outfile '/home/twitter/query_results/${tb}_count.txt'"
  cat /home/twitter/query_results/${tb}_count.txt >> /home/twitter/query_results/${DATABASE_NAME}_Qs.txt
  echo >> /home/twitter/query_results/${DATABASE_NAME}_Qs.txt
  rm -f /home/twitter/query_results/${tb}_count.txt

  echo "select count(*) from ${tb} where and timestamp > '2015-04-01 00:00:00' and timestamp < '2015-07-01 00:00:00'" >> /home/twitter/query_results/${DATABASE_NAME}_Qs.txt
  sudo mysql -B -u $MYSQL_USER --password=$MYSQL_PASSWORD $DATABASE_NAME -e "select count(*) from ${tb} where timestamp > '2015-04-01 00:00:00' and timestamp < '2015-07-01 00:00:00' into outfile '/home/twitter/query_results/${tb}_count.txt'"
  cat /home/twitter/query_results/${tb}_count.txt >> /home/twitter/query_results/${DATABASE_NAME}_Qs.txt
  echo >> /home/twitter/query_results/${DATABASE_NAME}_Qs.txt
  rm -f /home/twitter/query_results/${tb}_count.txt

  echo "select count(*) from ${tb} where and timestamp > '2015-07-01 00:00:00' and timestamp < '2015-10-01 00:00:00'" >> /home/twitter/query_results/${DATABASE_NAME}_Qs.txt
  sudo mysql -B -u $MYSQL_USER --password=$MYSQL_PASSWORD $DATABASE_NAME -e "select count(*) from ${tb} where timestamp > '2015-07-01 00:00:00' and timestamp < '2015-10-01 00:00:00' into outfile '/home/twitter/query_results/${tb}_count.txt'"
  cat /home/twitter/query_results/${tb}_count.txt >> /home/twitter/query_results/${DATABASE_NAME}_Qs.txt
  echo >> /home/twitter/query_results/${DATABASE_NAME}_Qs.txt
  rm -f /home/twitter/query_results/${tb}_count.txt

  echo >> /home/twitter/query_results/${DATABASE_NAME}_Qs.txt
done
