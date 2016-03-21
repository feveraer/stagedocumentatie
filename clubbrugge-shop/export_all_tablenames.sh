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
  echo "${tb}" >> ${TARGET_FOLDER}all_tables.txt
done
