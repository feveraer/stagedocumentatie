select l.Time, o.CustomName, o.Active, o.Address, o.SubAddress, t.Name, l.Value
from Outputs as o
join OutputLogs as l on o.id = l.OutputID
join Types as t on o.TypeId = t.id
where t.Name = 'THERMO'
order by o.CustomName, l.Time asc

select o.CustomName, t.Name, l.Value, l.Time
from Outputs as o
join OutputDownloadedLogs as l on o.id = l.OutputID
join Types as t on o.TypeId = t.id
where t.Name = 'THERMO'
order by o.CustomName, l.Time asc


select l.Time, o.CustomName, o.Address, o.SubAddress, l.Value, Count(*) as 'Occurences'
from Outputs as o
join OutputLogs as l on o.id = l.OutputID
join Types as t on o.TypeId = t.id
where t.Name = 'THERMO'
group by l.Time, o.CustomName, o.Address, o.SubAddress, l.Value having COUNT(*) > 1
order by o.CustomName, l.Time asc

select COUNT(*)
from OutputLogs