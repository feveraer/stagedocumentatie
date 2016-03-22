USE Qbus;

COPY OutputLogs(id, outputid, time, status, value)
FROM '/vagrant/resources/csv/OutputLogs.csv'
WITH DELIMITER = ';' AND HEADER = true
AND NULL = 'NULL';
