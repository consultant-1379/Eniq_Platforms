CREATE PROCEDURE remove_key_dc_e_bss_bscbh()
ON EXCEPTION RESUME
BEGIN

for loop1 as cursor1 cursor for
select table_name, column_name from systable st,syscolumn sc where table_type='BASE' and table_name like 'DC_E_BSS_BSCBH%' and st.table_id=sc.table_id and sc.column_name in ('MOID' , 'SN') and sc.nulls='N' 
do
execute immediate 'ALTER TABLE ' || ' dc.' || table_name || ' MODIFY ' || column_name || ' NULL ';
end for;
for loop2 as cursor2 cursor for
select  table_name, column_name from systable st,syscolumn sc where table_type='BASE' and  table_name like '%BH_RANK%' and st.table_id=sc.table_id and sc.column_name='MOID' and sc.nulls='N'
do
execute immediate 'ALTER TABLE ' || ' dc.' || table_name || ' MODIFY ' || column_name || ' NULL ';
end for;
END;
