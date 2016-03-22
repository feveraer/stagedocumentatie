CREATE KEYSPACE IF NOT EXISTS Qbus WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};

USE Qbus;

CREATE TABLE IF NOT EXISTS Outputs(
    Id BIGINT PRIMARY KEY,
    Uid TEXT,
    Active BOOLEAN,
    Address INT,
    SubAddress INT,
    ControllerId BIGINT,
    TypeId BIGINT,
    LocationId BIGINT,
    ProgramId BIGINT,
    Status TEXT,
    CreatedAt TIMESTAMP,
    UpdatedAt TIMESTAMP,
    CustomIcon INT,
    ReadOnly BOOLEAN,
    VirtualOutput BOOLEAN,
    Multiplier FLOAT
);
