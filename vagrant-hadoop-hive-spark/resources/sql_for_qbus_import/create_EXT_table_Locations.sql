CREATE EXTERNAL TABLE IF NOT EXISTS Locations(
    Id BIGINT,
    Name STRING,
    Userid BIGINT,
    OriginalID BIGINT,
    ControllerID BIGINT,
    ParentID BIGINT)
  ROW FORMAT DELIMITED
  FIELDS TERMINATED BY ','
  STORED AS TEXTFILE
  location '/input/csv/locations';
