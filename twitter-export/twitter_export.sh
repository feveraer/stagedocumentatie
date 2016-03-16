#!/bash/bin

DATABASE_NAME="collecttweet"
MYSQL_USER="root"
MYSQL_PASSWORD="Ugent2016"
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
