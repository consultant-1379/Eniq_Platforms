CREATE PROCEDURE alter_DC_E_CUDB_NEID()
ON EXCEPTION RESUME
BEGIN

for loop1 as cursor1 cursor for
select table_name, column_name from systable st,syscolumn sc where table_type='BASE' and table_name like 'DC_E_CUDB%' and st.table_id=sc.table_id and sc.column_name like 'NE_ID%' and sc.nulls='N' and table_name not like 'DC_E_CUDB_ACTCONN%'and table_name not like 'DC_E_CUDB_CLUSTER%' and table_name not like 'DC_E_CUDB_LDAP_OPS%' and table_name not like 'DC_E_CUDB_ELEMBH%' and table_name not like 'DC_E_CUDB_MEMORYUSE%' and table_name not like 'DC_E_CUDB_NEWCONN%' and table_name not like 'DC_E_CUDB_SOAP_NOTIF%'do
execute immediate 'ALTER TABLE ' || ' dc.' || table_name || ' MODIFY ' || column_name || ' NULL ';
end for;
END;
