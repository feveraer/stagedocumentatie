CREATE TABLE IF NOT EXISTS Outputs(
    Id BIGINT,
    Uid STRING,
    Active BOOLEAN,
    Address INT,
    SubAdress INT,
    ControllerId BIGINT,
    TypeId BIGINT,
    LocationId BIGINT,
    ProgramId BIGINT,
    Status STRING,
    CreatedAt TIMESTAMP,
    UpdatedAt TIMESTAMP,
    CustomIcon INT,
    ReadOnly BOOLEAN,
    VirtualOutput BOOLEAN,
    Multiplier FLOAT)
  ROW FORMAT DELIMITED
  FIELDS TERMINATED BY ','
  STORED AS ORC;
