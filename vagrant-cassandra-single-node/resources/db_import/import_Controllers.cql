USE Qbus;

COPY Controllers(id, serialnumber, apikey, verificationkey, name, macaddress, firmwareversion, registeredat, lastaccess, activated, ipaddress, receivingrate, timezone)
FROM '/vagrant/resources/csv/Controllers.csv'
WITH DELIMITER = ';' AND HEADER = true
AND NULL = 'NULL';
