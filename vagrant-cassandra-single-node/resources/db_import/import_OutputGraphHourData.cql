USE Qbus;

COPY OutputGraphHourData(outputid, time, statusproperty, value)
FROM '/vagrant/resources/csv/OutputGraphHourData.csv'
WITH DELIMITER = ';' AND HEADER = true
AND NULL = 'NULL';
