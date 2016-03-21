CREATE TABLE IF NOT EXISTS Locations(
  Id BIGINT PRIMARY KEY,
  Name TEXT,
  Userid BIGINT,
  OriginalID BIGINT,
  ControllerID BIGINT,
  ParentID BIGINT
);
