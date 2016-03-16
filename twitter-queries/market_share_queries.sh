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

# http://stackoverflow.com/questions/8880603/loop-through-array-of-strings-in-bash-script
# http://www.thegeekstuff.com/2010/06/bash-array-tutorial/

read -pr "MySQL username: " MYSQL_USER
read -prs "MySQL password: " MYSQL_PASSWORD
echo $MYSQL_USER
echo $MYSQL_PASSWORD

declare -a keys=(
  'ps'
  'mcdo'
  );

for tb in $(mysql -u $MYSQL_USER --password=$MYSQL_PASSWORD $DATABASE_NAME -sN -e "SHOW TABLES")
do
  for i in "${keys[@]}"
  do
    echo "${i}" >> /home/twitter/query_results/${i}.txt
    echo "select min(timestamp),max(timestamp),count(*) from ${tb} where lower(tweet) like '%${i}%' and timestamp > '2015-01-01 00:00:00' and timestamp < '2015-04-01 00:00:00'" >> /home/twitter/query_results/${i}.txt
    sudo mysql -B -u $MYSQL_USER --password=$MYSQL_PASSWORD $DATABASE_NAME -e "select min(timestamp),max(timestamp),count(*) from ${tb} where upper(tweet) like '%${i}%' and timestamp > '2015-01-01 00:00:00' and timestamp < '2015-04-01 00:00:00' into outfile '/home/twitter/query_results/${i}_count.txt'"
    cat /home/twitter/query_results/${i}_count.txt >> /home/twitter/query_results/${i}.txt
    echo >> /home/twitter/query_results/${i}.txt
    rm -f /home/twitter/query_results/${i}_count.txt
    cat /home/twitter/query_results/${i}.txt >> /home/twitter/query_results/result_Q1.txt
    rm -f /home/twitter/query_results/${i}.txt
  done
done
