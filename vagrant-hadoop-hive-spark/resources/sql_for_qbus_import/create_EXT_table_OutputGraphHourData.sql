CREATE EXTERNAL TABLE IF NOT EXISTS OutputGraphHourData(
    OutputID BIGINT,
    Time TIMESTAMP,
    StatusProperty INT,
    Value STRING
    )
  ROW FORMAT DELIMITED
  FIELDS TERMINATED BY ','
  STORED AS TEXTFILE
  location '/input/csv/outputgraphhourdata';
