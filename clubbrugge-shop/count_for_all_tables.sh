#!/bin/bash

DATABASE_NAME=""

usage() {
  echo "This script needs to know the database name."
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

TARGET_FOLDER=/home/clubbrugge_shop/results/

for tb in $(mysql -u $MYSQL_USER --password=$MYSQL_PASSWORD $DATABASE_NAME -sN -e "SHOW TABLES")
do

  echo "Counting table ${tb}"

  echo "select count(*) from ${tb}" >> ${TARGET_FOLDER}count_for_all_tables.txt
  mysql -B -u $MYSQL_USER --password=$MYSQL_PASSWORD $DATABASE_NAME -e "select count(*) from ${tb} into outfile '${TARGET_FOLDER}${tb}.txt';"
  cat ${TARGET_FOLDER}${tb}.txt >> ${TARGET_FOLDER}count_for_all_tables.txt
  rm -f ${TARGET_FOLDER}${tb}.txt
  echo >> ${TARGET_FOLDER}count_for_all_tables.txt
  echo
done
