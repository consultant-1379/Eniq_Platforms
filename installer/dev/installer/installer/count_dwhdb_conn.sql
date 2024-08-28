DECLARE @run_date date
set @run_date = CAST( DATEFORMAT( getdate(), 'YYYY-MM-DD' ) AS DATETIME )
DECLARE @run_time datetime
set @run_time = CAST( DATEFORMAT( getdate(), 'HH:NN' ) AS TIMESTAMP )
select 'insert into dwh_repdb_count values("'||@run_date||'","'||@run_time||'","dwh",'||count(*)||')' from sa_conn_activity()
go
