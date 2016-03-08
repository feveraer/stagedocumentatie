CREATE EXTERNAL TABLE IF NOT EXISTS OutputLogs(
    Id BIGINT,
    OutputId INT,
    Time TIMESTAMP,
    Status INT,
    Value STRING)
  ROW FORMAT DELIMITED
  FIELDS TERMINATED BY ','
  STORED AS TEXTFILE
  location '/home/vagrant/qbus_import/csv/outputlogs';
