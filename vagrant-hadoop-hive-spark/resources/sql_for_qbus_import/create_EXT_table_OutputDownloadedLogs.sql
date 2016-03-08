CREATE EXTERNAL TABLE IF NOT EXISTS OutputDownloadedLogs(
    Id BIGINT,
    OutputID BIGINT,
    Time TIMESTAMP,
    StatusProperty INT,
    Value STRING)
  ROW FORMAT DELIMITED
  FIELDS TERMINATED BY ','
  STORED AS TEXTFILE
  location '/home/vagrant/qbus_import/csv/outputdownloadedlogs';
