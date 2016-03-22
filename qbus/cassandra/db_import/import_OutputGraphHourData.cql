USE Qbus;

COPY OutputGraphHourData(outputid, time, status, value)
FROM '/vagrant/resources/csv/OutputGraphHourData.csv'
WITH DELIMITER = ';' AND HEADER = true
AND NULL = 'NULL';
