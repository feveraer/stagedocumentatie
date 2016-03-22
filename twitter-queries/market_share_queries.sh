#!/bin/bash

DATABASE_NAME=""
YEAR=0
YEAR1=0
QUARTER=""
BEGIN_TIME=""
END_TIME=""

usage() {
  echo "This script needs to know the database name, year and quarter."
  echo -e "\nUsage: $0 -d <database_name> -y <year> -q <quarter>\n"
}

if [ $# -le 3 ]
then
  usage
  exit 1
fi

while getopts ":d:y:q:" opt; do
  case $opt in
    d)
      DATABASE_NAME="${OPTARG}"
      ;;
    q)
      QUARTER=$OPTARG
      if [[ $QUARTER -lt 1 ]] ||  [[ $QUARTER -gt 4 ]]
      then
        echo "quarter should be between 1 and 4"
        exit 1
      fi
      ;;
    y)
      YEAR=$OPTARG
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

echo "Database: ${DATABASE_NAME}"
echo

case $QUARTER in
  1)
    BEGIN_TIME="${YEAR}-01-01 00:00:00"
    END_TIME="${YEAR}-04-01 00:00:00"
    ;;
  2)
    BEGIN_TIME="${YEAR}-04-01 00:00:00"
    END_TIME="${YEAR}-07-01 00:00:00"
    ;;
  3)
    BEGIN_TIME="${YEAR}-07-01 00:00:00"
    END_TIME="${YEAR}-10-01 00:00:00"
    ;;
  4)
    YEAR1=$((YEAR + 1))
    BEGIN_TIME="${YEAR}-10-01 00:00:00"
    END_TIME="${YEAR1}-01-01 00:00:00"
    ;;
esac


# http://stackoverflow.com/questions/8880603/loop-through-array-of-strings-in-bash-script
# http://www.thegeekstuff.com/2010/06/bash-array-tutorial/

echo
read -r -p "MySQL username: " MYSQL_USER
read -s -r -p "MySQL password: " MYSQL_PASSWORD
echo

declare -a keys=(
  # Consoles
  'teamsony' 'gosony' 'playstation' 'ps4'
  'teammicrosoft' 'gomicrosoft' 'xbox' 'xone' 'xbone'
  'teamnintendo' 'gonintendo' 'wii'
  # Fast food restaurants
  'mcdo' 'happymeal' 'imlovinit' 'bigmac' 'mcdstories' 'dollarmenu' 'mccafe'
  'kfc' 'kentuckyfriedchicken' 'doublicious' 'chickenlittles' 'famousbowl' 'yumbrands'
  'burgerking' 'whopper' 'bigking'
  'wendys' 'qualityisourrecipe' 'baconator'
  'starbucks' 'frapuccino' 'fizzio'
  'tacobell' 'tbell'
  'dunkind' 'ddperks' 'dunkaccino'
  'pizzahut'
  'panera'
  'dominos' 'pizzapics'
  'chipotle' 'burritobowl'
  # Social networks
  'facebook'
  'youtube'
  'googleplus'
  'twitter' 'tweet'
  'linkedin'
  'instagram'
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
  # Digital cameras
  'nikon'
  'canon'
  'sony'
  'panasonic'
  'samsung'
  'fujifilm'
  'olympus'
  'ricoh'
  # Sneakers
  'nike' 'teamjordan' 'converse' 'teamconverse' 'jordansneakers' 'jordanshoe' 'allstars'
  'adidas' 'reebok'
  'asics'
  'skechers'
  'underarmour'
  'puma'
  # Airline companies
  'americanairlines'
  'deltaair'
  'unitedair'
  'southwestairlines'
  'jetblue'
  'alaskairlines'
  'spiritairlines'
  'hawaiianairlines'
  'allegiantair'
  'virginamerica'
  # Retailers
  'walmart'
  'kroger'
  'costco'
  'target'
  'thehomedepot'
  'walgreens'
  'caremark'
  'lowes'
  'amazon'
  'mcdonalds'
  'bestbuy'
  'publix'
  'macys'
  'applestore' 'itunes'
  'sears'
  'aholdusa'
  'riteaid'
  'tjx'
  'kohls'
  'dollargeneral'
  # Record labels
  'universalmusicgroup'
  'sonymusic' 'columbiamusic' 'columbiarecords' 'sonybmg' 'sonyatv'
  'warnermusic'
  # Oil and gas companies
  'exxon'
  'chevron'
  'conocophilips'
  'occidentalpetroleum'
  'eogresources'
  'philips66'
  'anadarkopetroleum'
  'apache'
  # (Smart)phones
  'samsung' 'galaxys'
  'iphone' 'apple'
  'nokia' 'microsoft' 'windowsphone' 'lumia'
  'teamlg'
  'lenovo'
  'huawei'
  'xiaomi' 'miphone'
  'zte'
  # Tablets
  'apple' 'ipad'
  'samsung' 'galaxytab' 'tabpro' 'teamgalaxy'
  'asus' 'fonepad' 'transformerpad' 'vivotab' 'memopad'
  'lenovo' 'thinkpad'
  'gpad'
  'huawei' 'mediapad'
  # Cars
  'generalmotors' 'chevrolet' 'buick' 'gmc' 'cadillac' 'opel' 'vauxhall' 'chevy'
  'ford' 'mustang'
  'toyota'
  'chrysler' 'lancia' 'dodge' 'ramtrucks'
  'honda'
  'nissan'
  'hyundai'
  'kia'
  'subaru'
  'bmw'
  'volkswagen'
  'mazda'
  'jaguar' 'landrover'
  'mercedes' 'benz'
  'audi'
  # Film studios
  '21stcenturyfox' '20thcenturyfox'
  'lionsgate'
  'paramount' 'viacom'
  'warnerbros' 'newlinecinema'
  'universal'
  'sonycolumbia' 'columbiapictures' 'sonypictures' 'metrogoldwynnmayer'
  'disney'
  );

for tb in $(mysql -u $MYSQL_USER --password=$MYSQL_PASSWORD $DATABASE_NAME -sN -e "SHOW TABLES")
do
  for i in "${keys[@]}"
  do
    echo "${i}" >> /home/twitter/query_results/${i}.txt
    echo "select min(timestamp),max(timestamp),count(*) from ${tb} where lower(tweet) like '%${i}%' and timestamp > '2015-01-01 00:00:00' and timestamp < '2015-04-01 00:00:00'" >> /home/twitter/query_results/${i}.txt
    sudo mysql -B -u $MYSQL_USER --password=$MYSQL_PASSWORD $DATABASE_NAME -e "select min(timestamp),max(timestamp),count(*) from ${tb} where lower(tweet) like '%${i}%' and timestamp > '2015-01-01 00:00:00' and timestamp < '2015-04-01 00:00:00' into outfile '/home/twitter/query_results/${i}_count.txt'"
    cat /home/twitter/query_results/${i}_count.txt >> /home/twitter/query_results/${i}.txt
    echo >> /home/twitter/query_results/${i}.txt
    rm -f /home/twitter/query_results/${i}_count.txt
    cat /home/twitter/query_results/${i}.txt >> /home/twitter/query_results/result_Q1.txt
    rm -f /home/twitter/query_results/${i}.txt
  done
done
