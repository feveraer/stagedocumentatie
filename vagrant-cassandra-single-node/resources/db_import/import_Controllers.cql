USE Qbus;

COPY Controllers
FROM '/vagrant/resources/csv/Controllers.csv'
WITH HEADER = true;
