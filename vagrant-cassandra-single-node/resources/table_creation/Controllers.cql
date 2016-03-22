CREATE KEYSPACE IF NOT EXISTS Qbus 
WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};

USE Qbus;

CREATE TABLE IF NOT EXISTS Controllers(
  Id BIGINT PRIMARY KEY,
  SerialNumber TEXT,
  ApiKey TEXT,
  VerificationKey TEXT,
  Name TEXT,
  MacAddress TEXT,
  FirmwareVersion TEXT,
  RegisteredAt TIMESTAMP,
  LastAccess TIMESTAMP,
  Activated BOOLEAN,
  IPAddress TEXT,
  ReceivingRate DECIMAL,
  Timezone TEXT
);
