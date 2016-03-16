#bin/bash

# http://stackoverflow.com/questions/8880603/loop-through-array-of-strings-in-bash-script
# http://www.thegeekstuff.com/2010/06/bash-array-tutorial/

tb="collecttweetuni"
MYSQL_USER="root"
MYSQL_PASSWORD="Ugent2016"
DATABASE_NAME="ugenttwitter5"

declare -a keys=(
  # Consoles
  'teamplaystation' 'goplaystation' 'teamsony' 'gosony' 'playstation' 'ps' 'ps4' 'sonyplaystation'
  'teammicrosoft' 'teamxbox' 'goxbox' 'gomicrosoft' 'xbox' 'xone' 'xbone'
  'teamnintendo' 'gonintendo' 'wii' 'wiiu'
  # Fast food restaurants
  'mcdonalds' 'mcdo' 'happymeal' 'imlovinit' 'bigmac' 'mcdstories' 'dollarmenu' 'mccafe' 'teammcdo'
  'kfc' 'kentuckyfriedchicken' 'howdoyoukfc' 'doublicious' 'chickenlittles' 'famousbowl' 'teamkfc' 'yumbrands'
  'burgerking' 'whopper' 'bigking' 'doublewhopper' 'triplewhopper' 'fourcheesewhopper'
  'wendys' 'qualityisourrecipe' 'baconator'
  'starbucks' 'frapuccino' 'fizzio' 'teamstarbucks'
  'tacobell' 'tbell' 'yumbrands'
  'dunkind' 'ddperks' 'dunkaccino'
  'pizzahut' 'yumbrands'
  'panera'
  'dominos' 'pizzapics'
  'chipotle' 'burritobowl'
  # Social networks
  'facebook' 'fb'
  'youtube'
  'googleplus'
  'twitter' 'tweet'
  'linkedin'
  'instagram' 'insta'
  'pinterest'
  'tumblr'
  'reddit'
  # Banks
  'bankofamerica'
  'wellsfargo'
  'jpmorgan'
  'citibank'
  'usbank'
  'pncbank'
  'capitalone'
  'tdbank'
  'bbtbank'
  'suntrustbank'
  'bnymellon'
  '53bank' 'fiththird'
  'regionsbank'
  'charlesschwab'
  );

for i in "${keys[@]}"
do
  echo "${i}" >> /home/twitter/query_results/${i}.txt
  echo "select min(timestamp),max(timestamp),count(*) from ${tb} where lower(tweet) like '%${i}%' and timestamp > '2015-01-01 00:00:00' and timestamp < '2015-07-01 00:00:00' into outfile '/home/twitter/query_results/${i}_count.txt'" >> /home/twitter/query_results/${i}.txt
  sudo mysql -B -u $MYSQL_USER --password=$MYSQL_PASSWORD $DATABASE_NAME -e "select min(timestamp),max(timestamp),count(*) from ${tb} where lower(tweet) like '%${i}%' and timestamp > '2015-01-01 00:00:00' and timestamp < '2015-07-01 00:00:00' into outfile '/home/twitter/query_results/${i}_count.txt'"
  cat /home/twitter/query_results/${i}_count.txt >> /home/twitter/query_results/${i}.txt
  echo "\n" >> /home/twitter/query_results/${i}.txt
  rm -f /home/twitter/query_results/${i}_count.txt
done
