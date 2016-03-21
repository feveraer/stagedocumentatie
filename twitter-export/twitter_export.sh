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

TARGET_FOLDER=/home/twitter/csv/

for tb in $(mysql -u $MYSQL_USER --password=$MYSQL_PASSWORD $DATABASE_NAME -sN -e "SHOW TABLES")
do
  echo "Alter table ${tb}"
  mysql -B -u $MYSQL_USER --password=$MYSQL_PASSWORD $DATABASE_NAME -e "alter table ${tb} modify Tweet varchar(300)"
  mysql -B -u $MYSQL_USER --password=$MYSQL_PASSWORD $DATABASE_NAME -e "alter table ${tb} CHANGE stockticker stocksymbol varchar(10)"
  mysql -B -u $MYSQL_USER --password=$MYSQL_PASSWORD $DATABASE_NAME -e "alter table ${tb} modify stocksymbol varchar(10)"

  echo "Replace new lines in table ${tb}"
  mysql -B -u $MYSQL_USER --password=$MYSQL_PASSWORD $DATABASE_NAME -e "update ${tb} set Tweet = replace(Tweet, '\n','\\\\n')"
  mysql -B -u $MYSQL_USER --password=$MYSQL_PASSWORD $DATABASE_NAME -e "update ${tb} set UserName = replace(UserName, '\n','\\\\n')"
   mysql -B -u $MYSQL_USER --password=$MYSQL_PASSWORD $DATABASE_NAME -e "update ${tb} set stocksymbol = replace(stocksymbol, '\n','\\\\n')"

  echo "Export table ${tb}"
  mysql -B -u $MYSQL_USER --password=$MYSQL_PASSWORD $DATABASE_NAME -e "select * from ${tb} into outfile '${TARGET_FOLDER}${tb}.csv' fields enclosed by '' terminated by '´~¨\`^' escaped by '' lines terminated by '\r\n';"
done
