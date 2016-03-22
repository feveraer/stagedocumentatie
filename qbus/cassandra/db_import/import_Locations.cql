USE Qbus;

COPY Locations(id, name, userid, originalid, controllerid, parentid)
FROM '/vagrant/resources/csv/Locations.csv'
WITH DELIMITER = ';' AND HEADER = true
AND NULL = 'NULL';
