#!/bin/bash

cqlsh -f import_Controllers.cql
cqlsh -f import_Locations.cql
cqlsh -f import_OutputGraphHourData.cql
cqlsh -f import_Outputlogs.cql
cqlsh -f import_Outputs.cql
cqlsh -f import_Types.cql