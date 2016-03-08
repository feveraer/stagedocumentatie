CREATE EXTERNAL TABLE IF NOT EXISTS Types(
    Id BIGINT,
    Name STRING)
  ROW FORMAT DELIMITED
  FIELDS TERMINATED BY ','
  STORED AS TEXTFILE
  location '/home/vagrant/qbus_import/csv/types';
