USE Qbus;

COPY Outputs
FROM '/vagrant/resources/csv/Outputs.csv'
WITH HEADER = true;
