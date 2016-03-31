# Cassandra I/O Scala test app

In this app a scala application connects to the vagrant-cassandra machine also provided in this repo.

Following steps are taken by the app:

1. Creates a new table, persons, in the keyspace testdata.
2. Writes 3 persons to the db.
3. Checks if persons are written to the db.
4. Deletes the persons table.

## Resolved Issue
An issue with the connection can occur.
If this is the case please follow these steps:

- Enter de vagrant-cassandra machine by ssh.
- sudo vim /etc/cassandra/cassandra.yaml
- change the following lines:
  - start_native_transport: true
  - rpc_address: IP or hostname reachable from the client in this case 192.168.22.101

Note that in order to acces the cqlsh on the vagrant-cassandra machine you now need the execute the following command:

```shell
cqlsh 192.168.22.101 9042
```
