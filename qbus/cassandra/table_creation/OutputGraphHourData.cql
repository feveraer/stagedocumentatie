CREATE KEYSPACE IF NOT EXISTS Qbus WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};

USE Qbus;

CREATE TABLE IF NOT EXISTS OutputGraphHourData(
    OutputID BIGINT PRIMARY KEY,
    Time TIMESTAMP,
    Status INT,
    Value TEXT
);
