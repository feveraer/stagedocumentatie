CREATE TABLE IF NOT EXISTS OutputGraphHourData(
    OutputID BIGINT,
    Time TIMESTAMP,
    StatusProperty INT,
    Value STRING
    )
  ROW FORMAT DELIMITED
  FIELDS TERMINATED BY ','
  STORED AS ORC;
