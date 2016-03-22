USE Qbus;

COPY Outputs(id, uid, active, address, subaddress, controllerid, typeid, locationid, originalname, customname, programid, status, createdat, updatedat, customicon, readonly, virtualoutput, multiplier)
FROM '/vagrant/resources/csv/Outputs.csv'
WITH DELIMITER = ';' AND HEADER = true
AND NULL = 'NULL';
