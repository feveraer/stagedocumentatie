USE Qbus;

COPY Outputlogs
FROM '/vagrant/resources/csv/OutputLogs.csv'
WITH HEADER = true;
