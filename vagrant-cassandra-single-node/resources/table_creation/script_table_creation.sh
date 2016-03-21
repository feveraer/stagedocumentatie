#!/bin/bash

cqlsh -f Controllers.cql
cqlsh -f OutputGraphHourData.cql
cqlsh -f OutputLogs.cql
cqlsh -f Outputs.cql
cqlsh -f Types.cql
