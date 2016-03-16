#!/bin/bash

DATABASE_NAME=""

usage() {
  echo "This script needs to know the database name to be queried."
  echo -e "\nUsage: $0 -d <database_name> \n"
}

if [ $# -lt 1  ]
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

echo -e "${DATABASE_NAME}\n"

read -r -p "MySQL username: " MYSQL_USER
read -r -p "MySQL password: " MYSQL_PASSWORD
echo $MYSQL_USER
echo $MYSQL_PASSWORD

