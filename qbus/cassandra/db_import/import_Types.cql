USE Qbus;

COPY Types(id, name)
FROM '/vagrant/resources/csv/Types.csv'
WITH DELIMITER = ';' AND HEADER = true
AND NULL = 'NULL';
