CREATE TABLE IF NOT EXISTS Controllers(
  Id BIGINT PRIMARY KEY,
  SerialNumber TEXT,
  ApiKey TEXT,
  VerificationKey TEXT,
  Name TEXT,
  MacAdress TEXT,
  FirmwareVersion TEXT,
  RegisteredAt TIMESTAMP,
  LastAcces TIMESTAMP,
  Activated BOOLEAN,
  IPAdress TEXT,
  ReceivingRate DECIMAL,
  Timezone TEXT
);
