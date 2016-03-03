CREATE TABLE IF NOT EXISTS OutputLogs(
        Id BIGINT, 
        OutputId INT,
        Time TIMESTAMP,
        Status INT,
        Value STRING)
    ROW FORMAT DELIMITED
    FIELDS TERMINATED BY ','
    STORED AS ORC;