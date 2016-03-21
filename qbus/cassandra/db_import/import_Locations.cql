USE Qbus;

COPY Locations
FROM '/vagrant/resources/csv/Locations.csv'
WITH HEADER = true;
