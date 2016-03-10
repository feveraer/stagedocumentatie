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

select l.Time, c.Id, o.Uid, o.CustomName, l.Value
from Outputs as o
join OutputLogs as l on o.id = l.OutputID
join Types as t on o.TypeId = t.id
join Controllers as c on o.ControllerId = c.Id
where t.Name = 'THERMO'
order by o.Uid, l.Time

select COUNT(*)
from OutputLogs

select top 10000 ol.time settime, oghd.time measuredtime, 
ol.value setvalue, oghd.value measuredvalue, l.userid, o.id, l.name 
from A_QbusCloud.dbo.OutputGraphHourData oghd
join A_QbusCloud.dbo.Outputs o on oghd.outputid = o.id
join A_QbusCloud.dbo.Location l on o.locationid = l.id
left join A_QbusCloud.dbo.OutputLogs ol on o.id = ol.outputid
join A_QbusCloud.dbo.Types t on o.typeid = t.id

select ol.time time, ol.value value, l.userid, o.id, l.name
from OutputLogs ol
join Outputs o on ol.OutputID = o.id
join Location l on o.locationid = l.id
join Types t on o.typeid = t.id
where l.Userid = '53' and t.Name = 'THERMO'
union
select oghd.time, oghd.value, l.userid, o.id, l.name 
from OutputGraphHourData oghd
join Outputs o on oghd.outputid = o.id
join Location l on o.locationid = l.id
join Types t on o.typeid = t.id
where l.Userid = '53'
order by time desc, name