CREATE EXTERNAL TABLE IF NOT EXISTS Controllers(
    Id BIGINT,
    SerialNumber STRING,
    ApiKey STRING,
    VerificationKey STRING,
    Name STRING,
    MacAdress STRING,
    FirmwareVersion STRING,
    RegisteredAt TIMESTAMP,
    LastAcces TIMESTAMP,
    Activated BOOLEAN,
    IPAdress STRING,
    ReceivingRate DECIMAL,
    Timezone STRING)
  ROW FORMAT DELIMITED
  FIELDS TERMINATED BY ','
  STORED AS TEXTFILE
  location '/input/csv/controllers';
