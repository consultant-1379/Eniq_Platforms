create table dwh_repdb_count (
 execution_date date NOT NULL,
 execution_time datetime NOT NULL,
 rep_dwh_name varchar(5) NOT NULL,
 no_of_connection unsigned integer NOT NULL
)
go
