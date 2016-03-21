USE Qbus;

COPY OutputGraphHourData
FROM '/vagrant/resources/csv/OutputGraphHourData.csv'
WITH HEADER = true;
