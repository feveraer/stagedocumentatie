CREATE KEYSPACE IF NOT EXISTS Qbus 
WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};

USE Qbus;

CREATE TABLE IF NOT EXISTS Locations(
  Id BIGINT PRIMARY KEY,
  Name TEXT,
  Userid BIGINT,
  OriginalID BIGINT,
  ControllerID BIGINT,
  ParentID BIGINT
);
