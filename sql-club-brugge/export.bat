:: On line 10 replace table_names.txt with the name of the txtfile in which you have stored the columnnames
:: To simply get all columnnames perform the following query on the database:
::    SELECT TABLE_NAME
::    FROM information_schema.TABLES
:: Put every columnname on a new line in the txt file
:: On line 12 put the name of the database behind the -d flag.

@echo off
mkdir export
for /f "tokens=*" %%a in (table_names.txt) do (
  echo "Exporting %%a"
  sqlcmd -S . -d database_name -E -s"|"  -W -Q "SET NOCOUNT ON;SELECT * FROM dbo.%%a" | findstr /V /C:"-" /B > export\%%a.csv
)

echo "All columns have been exported to csv."

:: INFO ABOUT THE sqlcmd statement
:: -S:  Defines that the database is located on the localhost
:: -d:  Defines the database name
:: -E:  Defines a trusted connection, this means you don't have to use ""-U username -P password" to make connection
:: -s:  Defines the delimiter
:: -W:  Removes unnecessary spaces
:: -Q:  Defines the inline query

:: findstr /V /C:"-" /B removes strings like "--,-----,--------,--------".
