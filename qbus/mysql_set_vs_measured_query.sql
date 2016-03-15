select time, value, userid, l.Name, 'set' as tablename
from OutputLogs ol
join Outputs o on ol.OutputID = o.Id
join Location l on o.LocationId = l.Id
join Types t on o.TypeId = t.Id
where userid = '53' and l.Name = 'Badkamer' and t.Name = 'THERMO' and time > '2016-02-22'
union
select time, value, userid, l.Name, 'measured' as tablename
from OutputGraphHourData oghd
join Outputs o on oghd.OutputID = o.Id
join Location l on o.LocationId = l.Id
join Types t on o.TypeId = t.Id
where userid = '53' and l.Name = 'Badkamer' and t.Name = 'THERMO' and time > '2016-02-22'
order by time;