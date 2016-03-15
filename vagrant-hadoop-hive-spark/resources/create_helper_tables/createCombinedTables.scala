val setTemps = sqlContext.sql("select ol.time as Time, ol.value as Value, l.userid as UserId, l.name as LocationName "
    + "from OutputLogs ol "
    + "join Outputs o on ol.OutputID = o.id "
    + "join Locations l on o.locationid = l.id "
    + "join Types t on o.typeid = t.id "
    + "where t.Name = 'THERMO'"
    )

setTemps.saveAsTable("setTemps")

val measuredTemps = sqlContext.sql("select oghd.time as Time, oghd.value as Value, l.userid as UserId, l.name as LocationName "
    + "from OutputGraphHourData oghd "
    + "join Outputs o on oghd.outputid = o.id "
    + "join Locations l on o.locationid = l.id "
    + "join Types t on o.typeid = t.id "
    + "where t.Name = 'THERMO'"
    )

measuredTemps.saveAsTable("measuredTemps")

System.exit(0)
