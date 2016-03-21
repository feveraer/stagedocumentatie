CREATE KEYSPACE IF NOT EXITS Qbus WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};

USE Qbus;

CREATE TABLE IF NOT EXISTS OutputGraphHourData(
    OutputID BIGINT,
    Time TIMESTAMP,
    StatusProperty INT,
    Value STRING
);
