CREATE KEYSPACE IF NOT EXITS Qbus WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};

USE Qbus;

CREATE TABLE IF NOT EXISTS OutputLogs(
    ID BIGINT,
    OutputId INT,
    Time TIMESTAMP,
    Status INT,
    Value TEXT
);
