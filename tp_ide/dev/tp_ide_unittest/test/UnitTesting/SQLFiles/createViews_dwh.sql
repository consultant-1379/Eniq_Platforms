
create view SYS.SYSCAPABILITIES
  as select t1.capid,srvid,capname,capvalue from
    SYS.SYSCAPABILITY as t1 join SYS.SYSCAPABILITYNAME as t2 on
    t1.capid = t2.capid;

create view SYS.SYSREMOTEOPTION2
  as select option_id,
    user_id,
    SYS.HIDE_FROM_NON_DBA(setting) as setting from
    SYS.SYSREMOTEOPTION;

create view SYS.SYSREMOTEOPTIONS
  as select type_name,
    user_name,
    "option",
    SYS.HIDE_FROM_NON_DBA(setting) as setting from
    SYS.SYSREMOTETYPE as srt,
    SYS.SYSREMOTEOPTIONTYPE as srot,
    SYS.SYSREMOTEOPTION as sro,
    SYS.SYSUSERPERM as sup where
    srt.type_id = srot.type_id and
    srot.option_id = sro.option_id and
    sro.user_id = sup.user_id;

create view SYS.SYSSYNCS
  as select p.publication_name,s.progress,s.site_name,
    SYS.HIDE_FROM_NON_DBA(s."option") as "option",
    SYS.HIDE_FROM_NON_DBA(s.server_connect) as server_connect,
    s.server_conn_type,s.last_download_time,
    s.last_upload_time,s.created,s.log_sent,s.generation_number,
    s.extended_state from
    SYS.SYSSYNC as s left outer join
    SYS.SYSPUBLICATION as p on
    p.publication_id = s.publication_id;

create view SYS.SYSSYNC2
  as select sync_id,
    type,
    publication_id,
    progress,
    site_name,
    SYS.HIDE_FROM_NON_DBA("option") as "option",
    SYS.HIDE_FROM_NON_DBA(server_connect) as server_connect,
    server_conn_type,
    last_download_time,
    last_upload_time,
    created,
    log_sent,
    generation_number,
    extended_state from
    SYS.SYSSYNC;

create view SYS.SYSSYNCUSERS
  as select sync_id,
    site_name,
    SYS.HIDE_FROM_NON_DBA("option") as "option",
    SYS.HIDE_FROM_NON_DBA(server_connect) as server_connect,
    server_conn_type from
    SYS.SYSSYNC where
    publication_id is null;

create view SYS.SYSSYNCPUBLICATIONDEFAULTS
  as select s.sync_id,
    p.publication_name,
    SYS.HIDE_FROM_NON_DBA(s."option") as "option",
    SYS.HIDE_FROM_NON_DBA(s.server_connect) as server_connect,
    s.server_conn_type from
    SYS.SYSSYNC as s key join SYS.SYSPUBLICATION as p where
    s.site_name is null;

create view SYS.SYSSYNCSUBSCRIPTIONS
  as select s.sync_id,
    p.publication_name,
    s.progress,
    s.site_name,
    SYS.HIDE_FROM_NON_DBA(s."option") as "option",
    SYS.HIDE_FROM_NON_DBA(s.server_connect) as server_connect,
    s.server_conn_type,
    s.last_download_time,
    s.last_upload_time,
    s.created,
    s.log_sent,
    s.generation_number,
    s.extended_state from
    SYS.SYSSYNC as s key join SYS.SYSPUBLICATION as p where
    s.publication_id is not null and
    s.site_name is not null and
    exists(select 1 from SYS.SYSSYNCUSERS as u where
      s.site_name = u.site_name);

create view SYS.SYSUSERPERMS
  as select user_id,user_name,resourceauth,dbaauth,
    scheduleauth,user_group,publishauth,remotedbaauth,remarks from
    SYS.SYSUSERPERM;

create view SYS.SYSSYNCDEFINITIONS
  as select s.sync_id,p.publication_name,s.progress,s.site_name,
    SYS.HIDE_FROM_NON_DBA(s."option") as "option",
    SYS.HIDE_FROM_NON_DBA(s.server_connect) as server_connect,
    s.server_conn_type,
    s.last_download_time from
    SYS.SYSSYNC as s left outer join
    SYS.SYSPUBLICATION as p on
    p.publication_id = s.publication_id where
    s.type = 'D';

create view SYS.SYSSYNCTEMPLATES
  as select s.sync_id,p.publication_name,
    SYS.HIDE_FROM_NON_DBA(s."option") as "option",
    SYS.HIDE_FROM_NON_DBA(s.server_connect) as server_connect,
    s.server_conn_type from
    SYS.SYSSYNC as s left outer join
    SYS.SYSPUBLICATION as p on
    p.publication_id = s.publication_id where
    s.type = 'T';

create view SYS.SYSSYNCSITES
  as select s.sync_id,p.publication_name,s.site_name,
    SYS.HIDE_FROM_NON_DBA(s."option") as "option",
    SYS.HIDE_FROM_NON_DBA(s.server_connect) as server_connect,
    s.server_conn_type from
    SYS.SYSSYNC as s left outer join
    SYS.SYSPUBLICATION as p on
    p.publication_id = s.publication_id where
    s.type = 'S';

create view SYS.SYSCOLUMNS( creator,cname,tname,coltype,nulls,length,
  syslength,in_primary_key,colno,default_value,
  column_kind,remarks) 
  as select up.user_name,col.column_name,tab.table_name,dom.domain_name,
    col.nulls,col.width,col.scale,col.pkey,col.column_id,
    col."default",col.column_type,col.remarks from
    SYS.SYSCOLUMN as col key join
    SYS.SYSTABLE as tab key join
    SYS.SYSDOMAIN as dom join
    SYS.SYSUSERPERM as up on up.user_id = tab.creator;

create view SYS.SYSCATALOG( creator,
  tname,dbspacename,tabletype,ncols,primary_key,"check",
  remarks) 
  as select up.user_name,tab.table_name,file.dbspace_name,
    if tab.table_type = 'BASE' then 'TABLE' else tab.table_type
    endif,(select count(*) from SYS.SYSCOLUMN where
      table_id = tab.table_id),
    if tab.primary_root = 0 then 'N' else 'Y' endif,
    if tab.table_type <> 'VIEW' then tab.view_def endif,
    tab.remarks from
    SYS.SYSTABLE as tab key join
    SYS.SYSFILE as file join
    SYS.SYSUSERPERM as up on up.user_id = tab.creator;

create view SYS.SYSVIEWS( vcreator,
  viewname,viewtext) 
  as select user_name,table_name,view_def from
    SYS.SYSTABLE join SYS.SYSUSERPERM where
    table_type = 'VIEW';

create view SYS.SYSINDEXES( icreator,
  iname,fname,creator,tname,indextype,
  colnames,interval,level_num) 
  as select up.user_name,idx.index_name,file.file_name,up.user_name,
    tab.table_name,
    if idx."unique" = 'N' then 'Non-unique' else
      if idx."unique" = 'U' then 'UNIQUE constraint' else 'Unique' endif
    endif,
    (select list(string(column_name,
      if "order" = 'A' then ' ASC' else ' DESC' endif) order by
      ixc.table_id asc,ixc.index_id asc,ixc.sequence asc) from
      SYS.SYSIXCOL as ixc join
      SYS.SYSCOLUMN as c on(
      c.table_id = ixc.table_id and
      c.column_id = ixc.column_id) where
      index_id = idx.index_id and
      ixc.table_id = idx.table_id),
    0,0 from
    SYS.SYSTABLE as tab key join
    SYS.SYSFILE as file key join
    SYS.SYSINDEX as idx join
    SYS.SYSUSERPERM as up on up.user_id = idx.creator;

create view SYS.SYSUSERAUTH( name,
  password,resourceauth,dbaauth,scheduleauth,user_group) 
  as select user_name,password,resourceauth,dbaauth,scheduleauth,user_group from
    SYS.SYSUSERPERM;

create view SYS.SYSUSERLIST( name,
  resourceauth,dbaauth,scheduleauth,user_group) 
  as select user_name,resourceauth,dbaauth,scheduleauth,user_group from
    SYS.SYSUSERPERM;

create view SYS.SYSGROUPS( group_name,
  member_name) 
  as select g.user_name,u.user_name from
    SYS.SYSGROUP,SYS.SYSUSERPERM as g,SYS.SYSUSERPERM as u where
    group_id = g.user_id and group_member = u.user_id;

create view SYS.SYSCOLAUTH( grantor,grantee,creator,tname,colname,
  privilege_type,is_grantable) 
  as select up1.user_name,up2.user_name,up3.user_name,tab.table_name,
    col.column_name,cp.privilege_type,cp.is_grantable from
    SYS.SYSCOLPERM as cp join
    SYS.SYSUSERPERM as up1 on up1.user_id = cp.grantor join
    SYS.SYSUSERPERM as up2 on up2.user_id = cp.grantee join
    SYS.SYSTABLE as tab on tab.table_id = cp.table_id join
    SYS.SYSUSERPERM as up3 on up3.user_id = tab.creator join
    SYS.SYSCOLUMN as col on col.table_id = cp.table_id and
    col.column_id = cp.column_id;

create view SYS.SYSTABAUTH( grantor,
  grantee,screator,stname,tcreator,ttname,
  selectauth,insertauth,deleteauth,
  updateauth,updatecols,alterauth,referenceauth) 
  as select up1.user_name,up2.user_name,up3.user_name,tab1.table_name,
    up4.user_name,tab2.table_name,tp.selectauth,tp.insertauth,
    tp.deleteauth,tp.updateauth,tp.updatecols,tp.alterauth,
    tp.referenceauth from
    SYS.SYSTABLEPERM as tp join
    SYS.SYSUSERPERM as up1 on up1.user_id = tp.grantor join
    SYS.SYSUSERPERM as up2 on up2.user_id = tp.grantee join
    SYS.SYSTABLE as tab1 on tab1.table_id = tp.stable_id join
    SYS.SYSUSERPERM as up3 on up3.user_id = tab1.creator join
    SYS.SYSTABLE as tab2 on tab2.table_id = tp.ttable_id join
    SYS.SYSUSERPERM as up4 on up4.user_id = tab2.creator;

create view SYS.SYSOPTIONS( user_name,"option",setting) 
  as select up.user_name,opt."option",opt.setting from
    SYS.SYSOPTION as opt key join SYS.SYSUSERPERM as up;

create view SYS.SYSUSEROPTIONS( user_name,
  "option",setting) 
  as select u.name,
    "option",
    isnull((select setting from
      SYS.SYSOPTIONS as s where
      s.user_name = u.name and
      s."option" = o."option"),
    setting) from
    SYS.SYSOPTIONS as o,SYS.SYSUSERAUTH as u where
    o.user_name = 'PUBLIC';

create view SYS.SYSFOREIGNKEYS( foreign_creator,
  foreign_tname,
  primary_creator,primary_tname,role,columns) 
  as select fk_up.user_name,fk_tab.table_name,pk_up.user_name,
    pk_tab.table_name,fk.role,
    (select list(string(fk_col.column_name,' IS ',
      pk_col.column_name)) from
      SYS.SYSFKCOL as fkc join SYS.SYSCOLUMN as fk_col,
      SYS.SYSCOLUMN as pk_col where
      fkc.foreign_table_id = fk.foreign_table_id and
      fkc.foreign_key_id = fk.foreign_key_id and
      pk_col.table_id = fk.primary_table_id and
      pk_col.column_id = fkc.primary_column_id) from
    SYS.SYSFOREIGNKEY as fk join
    SYS.SYSTABLE as fk_tab on fk_tab.table_id = fk.foreign_table_id join
    SYS.SYSUSERPERM as fk_up on fk_up.user_id = fk_tab.creator join
    SYS.SYSTABLE as pk_tab on pk_tab.table_id = fk.primary_table_id join
    SYS.SYSUSERPERM as pk_up on pk_up.user_id = pk_tab.creator;

create view SYS.SYSPROCPARMS( creator,
  parmname,procname,parmtype,parmmode,parmdomain,
  length,remarks) 
  as select up.user_name,pp.parm_name,p.proc_name,pp.parm_type,
    if pp.parm_mode_in = 'Y' and pp.parm_mode_out = 'N' then 'IN' else
      if pp.parm_mode_in = 'N' and pp.parm_mode_out = 'Y' then 'OUT' else 'INOUT' endif
    endif,
    dom.domain_name,pp.width,pp.remarks from
    SYS.SYSPROCPARM as pp key join
    SYS.SYSPROCEDURE as p key join
    SYS.SYSUSERPERM as up key join
    SYS.SYSDOMAIN as dom;

create view SYS.SYSTRIGGERS( owner,
  trigname,tname,event,trigtime,trigdefn) 
  as select up.user_name,trig.trigger_name,tab.table_name,
    if trig.event = 'I' then 'INSERT' else
      if trig.event = 'U' then 'UPDATE' else
        if trig.event = 'C' then 'UPDATE' else
          if trig.event = 'D' then 'DELETE' else
            if trig.event = 'A' then 'INSERT,DELETE' else
              if trig.event = 'B' then 'INSERT,UPDATE' else
                if trig.event = 'E' then 'DELETE,UPDATE' else 'INSERT,DELETE,UPDATE' endif
              endif
            endif
          endif
        endif
      endif
    endif,if trig.trigger_time = 'B' or trig.trigger_time = 'P' then 'BEFORE' else
      if trig.trigger_time = 'A' or trig.trigger_time = 'S' then 'AFTER' else
        if trig.trigger_time = 'R' then 'RESOLVE' else 'INSTEAD OF' endif
      endif
    endif,trig.trigger_defn from
    SYS.SYSTRIGGER as trig key join
    SYS.SYSTABLE as tab join
    SYS.SYSUSERPERM as up on up.user_id = tab.creator where
    trig.foreign_table_id is null;

create view SYS.SYSPROCAUTH( grantee,
  creator,procname) 
  as select up1.user_name,up2.user_name,p.proc_name from
    SYS.SYSPROCEDURE as p key join
    SYS.SYSPROCPERM as pp join
    SYS.SYSUSERPERM as up1 on up1.user_id = pp.grantee join
    SYS.SYSUSERPERM as up2 on up2.user_id = p.creator;

create view SYS.SYSREMOTETYPES
  as select type_id,type_name,publisher_address,remarks from
    SYS.SYSREMOTETYPE;

create view SYS.SYSPUBLICATIONS
  as select u.user_name as creator,p.publication_name,p.type,p.remarks from
    SYS.SYSPUBLICATION as p key join SYS.SYSUSERPERM as u;

create view SYS.SYSARTICLES
  as select p.publication_name,t.table_name,a.where_expr,
    a.subscribe_by_expr from
    SYS.SYSARTICLE as a key join
    SYS.SYSPUBLICATION as p key join
    SYS.SYSTABLE as t;

create view SYS.SYSARTICLECOLS
  as select p.publication_name,t.table_name,c.column_name from
    SYS.SYSARTICLECOL as ac join
    SYS.SYSPUBLICATION as p on p.publication_id = ac.publication_id join
    SYS.SYSTABLE as t on t.table_id = ac.table_id join
    SYS.SYSCOLUMN as c on c.table_id = ac.table_id and
    c.column_id = ac.column_id;

create view SYS.SYSREMOTEUSERS
  as select u.user_name,r.consolidate,t.type_name,r.address,r.frequency,
    r.send_time,
    (if r.frequency = 'A' then null else if r.frequency = 'P' then
        if r.time_sent is null then current timestamp
        else(select min(minutes(a.time_sent,60*hour(a.send_time)+
            minute(seconds(a.send_time,59)))) from
            SYS.SYSREMOTEUSER as a where a.frequency = 'P' and
            a.send_time = r.send_time)
        endif else if current date+r.send_time > 
        coalesce(r.time_sent,current timestamp) then
          current date+r.send_time else current date+r.send_time+1 endif
      endif endif) as next_send,
    r.log_send,r.time_sent,r.log_sent,r.confirm_sent,r.send_count,
    r.resend_count,r.time_received,r.log_received,
    r.confirm_received,r.receive_count,r.rereceive_count from
    SYS.SYSREMOTEUSER as r key join
    SYS.SYSUSERPERM as u key join
    SYS.SYSREMOTETYPE as t;

create view SYS.SYSSUBSCRIPTIONS
  as select p.publication_name,u.user_name,s.subscribe_by,s.created,
    s.started from
    SYS.SYSSUBSCRIPTION as s key join
    SYS.SYSPUBLICATION as p join
    SYS.SYSUSERPERM as u on u.user_id = s.user_id;

create view SYS.SYSCOLSTATS
  as select u.user_name,t.table_name,c.column_name,
    format_id,update_time,density,max_steps,
    actual_steps,step_values,frequencies from
    SYS.SYSCOLSTAT as s,SYS.SYSTABLE as t,SYS.SYSCOLUMN as c,SYS.SYSUSERPERM as u where
    s.table_id = c.table_id and
    s.column_id = c.column_id and
    c.table_id = t.table_id and
    t.creator = u.user_id;

create view dbo.syscolumns( id,
  number,
  colid,
  status,
  type,
  length,
  offset,
  usertype,
  cdefault,
  domain,
  name,
  printfmt,
  prec,
  scale) as
  select cast(col.table_id+100000 as unsigned bigint),
    cast(0 as smallint),
    cast(col.column_id as unsigned integer),
    cast(if nulls = 'Y' then 8 else 0 endif as smallint),
    cast(sst.ss_domain_id as smallint),
    cast(col.width as smallint),
    cast(0 as smallint),
    cast((if col.user_type is not null and col.user_type > (select max(sa_user_type) from SYS.SYSTYPEMAP where sa_user_type is not null) then col.user_type else sst.ss_user_type endif) as smallint),
    cast(0 as integer),
    cast(0 as integer),
    cast(col.column_name as varchar(128)),
    cast(null as varchar(255)),
    cast(col.width as smallint),
    cast(col.scale as smallint) from
    SYS.SYSCOLUMN as col,SYS.SYSTYPEMAP as map,SYS.SYSSQLSERVERTYPE as sst where
    (map.sa_user_type = col.user_type or
    (map.sa_user_type is null and
    (col.user_type is null or col.user_type > 113))) and
    map.sa_domain_id = col.domain_id and
    map.ss_user_type = sst.ss_user_type and
    (nullable = 'N' or nullable is null) union all
  select cast(parm.proc_id+200000 as integer),
    cast(0 as smallint),
    cast(parm.parm_id as smallint),
    cast(0 as smallint),
    cast(sst.ss_domain_id as smallint),
    cast(parm.width as smallint),
    cast(0 as smallint),
    cast(sst.ss_user_type as smallint),
    cast(0 as integer),
    cast(0 as integer),
    cast(parm.parm_name as varchar(128)),
    cast(null as varchar(255)),
    cast(parm.width as smallint),
    cast(parm.scale as smallint) from
    SYS.SYSPROCPARM as parm,SYS.SYSTYPEMAP as map,SYS.SYSSQLSERVERTYPE as sst where
    map.sa_user_type is null and
    parm.parm_type = 0 and
    map.sa_domain_id = parm.domain_id and
    map.ss_user_type = sst.ss_user_type and
    (nullable = 'N' or nullable is null);

create view dbo.syscomments( id,
  number,
  colid,
  texttype,
  language,
  text,
  colid2) as
  select cast(table_id+100000 as unsigned bigint),
    cast(0 as smallint),
    cast(row_num as unsigned integer),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(substr(view_def,(row_num-1)*80+1,80) as varchar(255)),
    cast(0 as unsigned integer) from
    SYS.SYSTABLE,dbo.RowGenerator where
    table_type = 'VIEW' and
    row_num <= length(view_def)/80+1 union all
  select cast(table_id+100000 as unsigned bigint),
    cast(0 as smallint),
    cast(row_num as smallint),
    cast(1 as smallint),
    cast(0 as smallint),
    cast(substr(remarks,(row_num-1)*80+1,80) as varchar(255)),
    cast(0 as smallint) from
    SYS.SYSTABLE,dbo.RowGenerator where
    table_type = 'VIEW' and
    row_num <= length(remarks)/80+1 union all
  select cast(proc_id+200000 as integer),
    cast(0 as smallint),
    cast(row_num as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(substr(proc_defn,(row_num-1)*80+1,80) as varchar(255)),
    cast(0 as smallint) from
    SYS.SYSPROCEDURE,dbo.RowGenerator where
    row_num <= length(proc_defn)/80+1 union all
  select cast(proc_id+200000 as integer),
    cast(0 as smallint),
    cast(row_num as smallint),
    cast(1 as smallint),
    cast(0 as smallint),
    cast(substr(remarks,(row_num-1)*80+1,80) as varchar(255)),
    cast(0 as smallint) from
    SYS.SYSPROCEDURE,dbo.RowGenerator where
    row_num <= length(remarks)/80+1 union all
  select cast(trigger_id+300000 as integer),
    cast(0 as smallint),
    cast(row_num as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(substr(trigger_defn,(row_num-1)*80+1,80) as varchar(255)),
    cast(0 as smallint) from
    SYS.SYSTRIGGER,dbo.RowGenerator where
    row_num <= length(trigger_defn)/80+1 union all
  select cast(trigger_id+300000 as integer),
    cast(0 as smallint),
    cast(row_num as smallint),
    cast(1 as smallint),
    cast(0 as smallint),
    cast(substr(remarks,(row_num-1)*80+1,80) as varchar(255)),
    cast(0 as smallint) from
    SYS.SYSTRIGGER,dbo.RowGenerator where
    row_num <= length(remarks)/80+1;

create view dbo.sysindexes( name,
  id,
  indid,
  doampg,
  ioampg,
  oampgtrips,
  status2,
  ipgtrips,
  "first",
  root,
  distribution,
  usagecnt,
  segment,
  status,
  rowpage,
  minlen,
  maxlen,
  maxirow,
  keycnt,
  keysl,
  keys2,
  soid,
  csid) as
  select cast(index_name as varchar(128)),
    cast(table_id+100000 as unsigned bigint),
    cast(index_id as unsigned integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast((if "unique" = 'U' then 2 else 0 endif) as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as smallint),
    cast(0 as smallint),
    cast((if "unique" = 'N' then 1 else 1+2 endif) as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(null as binary(255)),
    cast(null as binary(255)),
    cast(0 as smallint),
    cast(0 as smallint) from
    SYS.SYSINDEX union all
  select cast(table_name as char(128)),
    cast(table_id+100000 as unsigned bigint),
    cast(0 as smallint),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(null as binary(255)),
    cast(null as binary(255)),
    cast(0 as smallint),
    cast(0 as smallint) from
    SYS.SYSTABLE union all
  select cast(table_name as char(128)),
    cast(t.table_id+100000 as unsigned bigint),
    cast(20000+foreign_key_id as smallint),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(1 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(null as binary(255)),
    cast(null as binary(255)),
    cast(0 as smallint),
    cast(0 as smallint) from
    SYS.SYSTABLE as t,SYS.SYSFOREIGNKEY as fk where
    t.table_id = fk.foreign_table_id union all
  select cast(table_name as char(128)),
    cast(t.table_id+100000 as unsigned bigint),
    cast(10000 as smallint),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(2 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(1+2+2048 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(null as binary(255)),
    cast(null as binary(255)),
    cast(0 as smallint),
    cast(0 as smallint) from
    SYS.SYSTABLE as t where
    exists(select table_id from SYS.SYSCOLUMN where
      table_id = t.table_id and
      pkey = 'Y');

create view dbo.sysobjects( name,
  id,
  uid,
  type,
  userstat,
  sysstat,
  indexdel,
  schemacnt,
  sysstat2,
  crdate,
  expdate,
  deltrig,
  instrig,
  updtrig,
  seltrig,
  ckfirst,
  cache,
  audflags,
  objspare) as
  select cast(table_name as varchar(128)),
    cast(table_id+100000 as unsigned bigint),
    cast(creator as unsigned integer),
    cast(if table_type = 'VIEW' then 'V' else
      if creator = 0 then 'S' else 'U' endif
    endif as char(2)),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(null as date),
    cast(null as date),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as smallint),
    cast(0 as integer),
    cast(0 as integer) from
    SYS.SYSTABLE union all
  select cast(proc_name as char(128)),
    cast(proc_id+200000 as integer),
    cast(creator as unsigned integer),cast('P' as char(2)),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(null as date),
    cast(null as date),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as smallint),
    cast(0 as integer),
    cast(0 as integer) from
    SYS.SYSPROCEDURE union all
  select cast(trig.trigger_name as char(128)),
    cast(trig.trigger_id+300000 as integer),
    cast(tab.creator as unsigned integer),cast('TR' as char(2)),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(null as date),
    cast(null as date),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as smallint),
    cast(0 as integer),
    cast(0 as integer) from
    SYS.SYSTRIGGER as trig key join SYS.SYSTABLE as tab;

create view dbo.systypes( uid,
  usertype,
  "variable",
  allowsnulls,
  type,
  length,
  tdefault,
  domain,
  name,
  printfmt,
  prec,
  scale,
  ident,
  hierarchy) as
  select cast(0 as unsigned integer),
    cast(sst.ss_user_type as smallint),
    cast(0 as smallint),
    cast(1 as smallint),
    cast(ss_domain_id as smallint),
    cast(0 as smallint),
    cast(0 as integer),
    cast(0 as integer),
    cast(sst.ss_type_name as varchar(128)),
    cast(null as varchar(255)),
    cast("precision" as smallint),
    cast(null as smallint),
    cast(0 as smallint),
    cast(0 as smallint) from
    SYS.SYSDOMAIN,SYS.SYSSQLSERVERTYPE as sst where
    SYSDOMAIN.domain_id = primary_sa_domain_id and
    primary_sa_user_type is null union all
  select cast(creator as unsigned integer),
    cast(ss_user_type as smallint),
    cast(0 as smallint),
    cast((if nulls = 'Y' then 1 else 0 endif) as smallint),
    cast(ss_domain_id as smallint),
    cast(width as smallint),
    cast(0 as integer),
    cast(0 as integer),
    cast(sst.ss_type_name as varchar(128)),
    cast(null as varchar(255)),
    cast("precision" as smallint),
    cast(scale as smallint),
    cast((if isnull("default",'') = 'autoincrement' then 1 else 0 endif) as smallint),
    cast(0 as smallint) from
    SYS.SYSUSERTYPE as t,SYS.SYSDOMAIN as d,SYS.SYSSQLSERVERTYPE as sst where
    t.domain_id = d.domain_id and
    sst.primary_sa_domain_id = t.domain_id and
    sst.primary_sa_user_type = t.type_id union all
  select cast(creator as unsigned integer),
    cast(t.type_id as smallint),
    cast(0 as smallint),
    cast((if nulls = 'Y' then 1 else 0 endif) as smallint),
    cast(ss_domain_id as smallint),
    cast(width as smallint),
    cast(0 as integer),
    cast(0 as integer),
    cast(t.type_name as varchar(128)),
    cast(null as varchar(255)),
    cast("precision" as smallint),
    cast(scale as smallint),
    cast((if isnull("default",'') = 'autoincrement' then 1 else 0 endif) as smallint),
    cast(0 as smallint) from
    SYS.SYSUSERTYPE as t,SYS.SYSDOMAIN as d,SYS.SYSSQLSERVERTYPE as sst,SYS.SYSTYPEMAP as tm where
    t.domain_id = d.domain_id and
    tm.sa_domain_id = t.domain_id and
    tm.sa_user_type is null and
    t.type_id > (select max(sa_user_type) from SYS.SYSTYPEMAP where sa_user_type is not null) and
    sst.ss_user_type = tm.ss_user_type and(
    nullable = 'N' or nullable is null);

create view dbo.sysusers( suid,
  uid,
  gid,
  name,
  environ) 
  as select cast(user_id as unsigned integer),
    cast(user_id as unsigned integer),
    cast(0 as unsigned integer),
    cast(user_name as varchar(128)),
    cast(null as varchar(255)) from
    SYS.SYSUSERPERM;

create view dbo.syslogins( suid,
  status,
  accdate,
  totcpu,
  totio,
  spacelimit,
  timelimit,
  resultlimit,
  dbname,
  name,
  password,
  language,
  pwdate,
  audflags,
  fullname) 
  as select cast(user_id as unsigned integer),
    cast(0 as smallint),
    cast(null as date),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(null as varchar(128)),
    cast(user_name as varchar(128)),
    cast(null as binary(128)),
    cast(null as varchar(30)),
    cast(null as date),
    cast(0 as integer),
    cast(user_name as varchar(30)) from
    SYS.SYSUSERPERM;

create view SYS.SYSTUNINGINDEXES( icreator,
  iname,fname,creator,tname,indextype,
  colnames,interval,level_num,exists_as,optimizer_status) as
  select up.user_name,idx.index_name,file.file_name,up.user_name,
    tab.table_name,
    if idx."unique" = 'n' then 'non-unique' else
      if idx."unique" = 'u' then 'unique constraint' else 'unique' endif
    endif,
    (select list(string(column_name,
      if "order" = 'a' then ' asc' else ' desc' endif)) from
      dbo.sa_virtual_sysixcol() as ixcol,SYS.SYSCOLUMN where
      ixcol.column_id = SYS.SYSCOLUMN.column_id and
      ixcol.table_id = SYS.SYSCOLUMN.table_id and
      ixcol.index_id = idx.index_id and
      ixcol.table_id = idx.table_id),
    0,0,'virtual',
    if idx.disabled = 1 then 'disabled' else 'enabled' endif from
    SYS.SYSTABLE as tab key join
    SYS.SYSFILE as file join
    dbo.sa_virtual_sysindex() as idx on tab.table_id = idx.table_id join
    SYS.SYSUSERPERM as up on up.user_id = idx.creator union all
  select up.user_name,idx.index_name,file.file_name,up.user_name,
    tab.table_name,
    if idx."unique" = 'n' then 'non-unique' else
      if idx."unique" = 'u' then 'unique constraint' else 'unique' endif
    endif,
    (select list(string(column_name,
      if "order" = 'a' then ' asc' else ' desc' endif)) from
      SYS.SYSIXCOL join SYS.SYSCOLUMN where
      index_id = idx.index_id and
      SYSIXCOL.table_id = idx.table_id),
    0,0,'physical',
    if index_enabled(idx.table_id,idx.index_id) = 1 then 'enabled' else 'disabled' endif from
    SYS.SYSTABLE as tab key join
    SYS.SYSFILE as file key join
    SYS.SYSINDEX as idx join
    SYS.SYSUSERPERM as up on up.user_id = idx.creator;

create view dbo.ml_connection_scripts
  as select dbo.ml_script_version.name as version,
    dbo.ml_connection_script.event,
    dbo.ml_script.script_language,
    dbo.ml_script.script from
    dbo.ml_connection_script,
    dbo.ml_script_version,
    dbo.ml_script where
    dbo.ml_connection_script.version_id = dbo.ml_script_version.version_id and
    dbo.ml_connection_script.script_id = dbo.ml_script.script_id;

create view dbo.ml_table_scripts
  as select dbo.ml_script_version.name as version,
    dbo.ml_table_script.event,
    dbo.ml_table.name as table_name,
    dbo.ml_script.script_language,
    dbo.ml_script.script from
    dbo.ml_table_script,
    dbo.ml_script_version,
    dbo.ml_script,
    dbo.ml_table where
    dbo.ml_table_script.version_id = dbo.ml_script_version.version_id and
    dbo.ml_table_script.script_id = dbo.ml_script.script_id and
    dbo.ml_table_script.table_id = dbo.ml_table.table_id;

create view dbo.ml_qa_messages
  as select mr.seqno,
    mr.msgid,
    md.address,
    md.client,
    mr.originator,
    md.status,
    md.statustime,
    md.verbiage,
    mr.expires,
    mr.priority,
    mr.props,
    mr.kind,
    mr.content,
    mr.contentsize,
    md.syncstatus,
    md.receiverid,
    md.last_modified from
    dbo.ml_qa_repository as mr,dbo.ml_qa_delivery as md where
    mr.msgid = md.msgid and
    md.status = (select max(status) from
      dbo.ml_qa_delivery as mdd where
      md.msgid = mdd.msgid and(
      mdd.address = md.address));

create view SYS.SYSOPTSTRATEGIES
  as select js.joinstrategy_id,
    js.block_id,
    r.request_id,
    read_cost+write_cost+cpu_cost as total_cost,
    pruning_order,
    read_cost,
    write_cost,
    cpu_cost,
    r.user_id,
    r.conn_id,
    sql_hash from
    SYS.SYSOPTJOINSTRATEGY as js key join
    SYS.SYSOPTBLOCK key join
    SYS.SYSOPTREQUEST as r;

create view SYS.SYSOPTPLANS
  as select r.request_id,
    js.joinstrategy_id,
    time_stamp,
    user_id,
    conn_id,
    sql_original,
    sql_rewritten,
    js.block_id,
    plan_extra,
    js.plan_xml,
    pruning_order,
    num_reads,
    num_writes,
    read_cost,
    write_cost,
    cpu_cost from
    SYS.SYSOPTJOINSTRATEGY as js key join
    SYS.SYSOPTBLOCK key join
    SYS.SYSOPTREQUEST as r;

create view SYS.SYSOPTORDERS
  as select order_id,
    joinstrategy_id,
    q.quantifier_id,
    index_id,
    join_type,
    systable_id,
    column_count from
    SYS.SYSOPTORDER key left outer join
    SYS.SYSOPTQUANTIFIER as q;

create view dbo.sysiqvindex( table_id,
  index_id,
  root,
  file_id,
  "unique",
  creator,
  index_name,
  remarks,
  index_type,
  index_owner) 
  as select table_id,
    index_id,
    root,
    file_id,
    "unique",
    creator,
    index_name,
    remarks,
    index_type,
    index_owner from
    SYS.SYSINDEX where
    index_type <> 'FP';

create view dbo.sysiqobjects( name,
  id,
  pid,
  uid,
  type,
  userstat,
  sysstat,
  indexdel,
  schemacnt,
  sysstat2,
  crdate,
  expdate,
  deltrig,
  instrig,
  updtrig,
  seltrig,
  ckfirst,
  cache,
  audflags,
  objspare) as
  select cast(tbl.table_name as varchar(128)),
    cast(tbl.table_id as integer),
    cast(null as integer),
    cast(tbl.creator as unsigned integer),
    cast(if tbl.table_type = 'VIEW' then 'V' else
      if tbl.creator = 0 then 'S' else 'U' endif
    endif as char(2)),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    if server_type = 'SA' then
      cast(null as datetime)
    else
      create_time
    endif,
    cast(null as datetime),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as smallint),
    cast(0 as integer),
    cast(0 as integer) from
    SYS.SYSTABLE as tbl left outer join SYS.SYSIQTABLE as sysiq on
    tbl.table_id = sysiq.table_id union all
  select cast(proc_name as varchar(128)),
    cast(proc_id as integer),
    cast(null as integer),
    cast(creator as unsigned integer),cast('P' as char(2)),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(null as datetime),
    cast(null as datetime),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as smallint),
    cast(0 as integer),
    cast(0 as integer) from
    SYS.SYSPROCEDURE union all
  select cast(trig.trigger_name as varchar(128)),
    cast(trig.trigger_id as integer),
    cast(null as integer),
    cast(tab.creator as unsigned integer),cast('TR' as char(2)),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(null as datetime),
    cast(null as datetime),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as smallint),
    cast(0 as integer),
    cast(0 as integer) from
    SYS.SYSTRIGGER as trig key join SYS.SYSTABLE as tab union all
  select cast(event.event_name as varchar(128)),
    cast(event.event_id as integer),
    cast(null as integer),
    cast(event.creator as unsigned integer),cast('EV' as char(2)),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(null as datetime),
    cast(null as datetime),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as smallint),
    cast(0 as integer),
    cast(0 as integer) from
    SYS.SYSEVENT as event union all
  select cast(joinindex.joinindex_name as varchar(128)),
    cast(joinindex.joinindex_id as integer),
    cast(null as integer),
    cast(joinindex.creator as unsigned integer),cast('JI' as char(2)),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(null as datetime),
    cast(null as datetime),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as smallint),
    cast(0 as integer),
    cast(0 as integer) from
    SYS.SYSIQJOININDEX as joinindex union all
  select cast(cnstrnt.constraint_name as varchar(128)),
    cast(cnstrnt.constraint_id as integer),
    cast(null as integer),
    cast(null as unsigned integer),cast('CO' as char(2)),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(null as datetime),
    cast(null as datetime),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as smallint),
    cast(0 as integer),
    cast(0 as integer) from
    SYS.SYSCONSTRAINT as cnstrnt union all
  select cast(sysdomain.domain_name as varchar(128)),
    cast(sysdomain.domain_id as integer),
    cast(null as integer),
    cast(null as unsigned integer),cast('SD' as char(2)),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(null as datetime),
    cast(null as datetime),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as smallint),
    cast(0 as integer),
    cast(0 as integer) from
    SYS.SYSDOMAIN as sysdomain union all
  select cast(userdomain.type_name as varchar(128)),
    cast(userdomain.type_id as integer),
    cast(null as integer),
    cast(userdomain.creator as unsigned integer),cast('UD' as char(2)),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(null as datetime),
    cast(null as datetime),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as smallint),
    cast(0 as integer),
    cast(0 as integer) from
    SYS.SYSUSERTYPE as userdomain union all
  select cast(syscol.column_name as varchar(128)),
    cast(syscol.column_id as integer),
    cast(syscol.table_id as integer),
    cast(null as unsigned integer),cast('CL' as char(2)),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(null as datetime),
    cast(null as datetime),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as smallint),
    cast(0 as integer),
    cast(0 as integer) from
    SYS.SYSCOLUMN as syscol union all
  select cast(idx.index_name as varchar(128)),
    cast(idx.index_id as integer),
    cast(idx.table_id as integer),
    cast(null as unsigned integer),cast('IX' as char(2)),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(0 as smallint),
    cast(null as datetime),
    cast(null as datetime),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as integer),
    cast(0 as smallint),
    cast(0 as integer),
    cast(0 as integer) from
    SYS.SYSINDEX as idx;

create view dcpublic.SELECT_AGGLEVEL_TABLE
  as select FIRSTDATE_ID,
    AGGLEVEL,
    DATE_ID,
    YEAR_ID,
    MONTH_ID,
    DAY_ID,
    WEEK_ID,
    WEEKDAY_ID,
    BUSINESSDAY,
    TIMELEVEL,
    TIMELEVELBH,
    DAYS from
    dc.SELECT_AGGLEVEL_TABLE;

create view dcpublic.DIM_AGG_BHCLASS
  as select AGGLEVEL,
    BHCLASS,
    DESCRIPTION,
    LINK from
    dc.DIM_AGG_BHCLASS;

create view dcpublic.DIM_BHCLASS
  as select BHCLASS,
    DESCRIPTION,
    LINK from
    dc.DIM_BHCLASS;

create view dcpublic.DIM_DATE
  as select DATE_ID,
    YEAR_ID,
    MONTH_ID,
    DAY_ID,
    WEEK_ID,
    WEEKDAY_ID,
    BUSINESSDAY from
    dc.DIM_DATE;

create view dcpublic.DIM_TIME
  as select HOUR_ID,
    MIN_ID,
    OFFICE_TIME,
    DAY_TIME,
    NIGHT_TIME from
    dc.DIM_TIME;

create view dcpublic.DIM_TIMELEVEL
  as select TIMELEVEL_ID,
    TIMELEVEL,
    TABLELEVEL,
    DURATIONMIN,
    SCOPE,
    DESCRIPTION,
    MAXDELAY from
    dc.DIM_TIMELEVEL;

create view dcpublic.DIM_WEEKDAY
  as select WEEKDAY_ID,
    LANGUAGE,
    NAME from
    dc.DIM_WEEKDAY;

create view dcpublic.DIM_ROWSTATUS
  as select ROWSTATUS,
    DESCRIPTION from
    dc.DIM_ROWSTATUS;

create view dc.DIM_HOUR( HOUR_ID,OFFICE_TIME,DAY_TIME,NIGHT_TIME) as select HOUR_ID,max(OFFICE_TIME),max(DAY_TIME),max(NIGHT_TIME) from dc.DIM_TIME group by HOUR_ID;

create view dc.NEW_DATES( DATE_ID,YEAR_ID,MONTH_ID,DAY_ID,WEEK_ID,WEEKDAY_ID,BUSINESSDAY) as select DATE_ID+7,YEARS(DATE_ID+7),DATEPART(mm,DATE_ID+7),DAY(DATE_ID+7),DATEPART(cwk,DATE_ID+7),DOW(DATE_ID+7),case(DOW(DATE_ID+7)) when 6 then 0 when 7 then 0 else 1 end from dc.dim_date where date_id > (select max(date_id)-7 from dc.dim_date);

create view
  dc.SELECT_DWH_BASE_AGGLEVEL( FIRSTDATE_ID,AGGLEVEL,DATE_ID,YEAR_ID,MONTH_ID,DAY_ID,WEEK_ID,WEEKDAY_ID,BUSINESSDAY,
  TIMELEVEL,TIMELEVELBH,DAYS) 
  as select FIRSTDATE_ID,AGGLEVEL,DATE_ID,YEAR_ID,MONTH_ID,DAY_ID,WEEK_ID,WEEKDAY_ID,BUSINESSDAY,
    TIMELEVEL,TIMELEVELBH,DAYS from dc.SELECT_AGGLEVEL_TABLE where
    TIMELEVELBH = 'DAYBH';

create view dcpublic.LOG_MonitoredTypes
  as select TYPENAME,
    TIMELEVEL,
    STATUS,
    MODIFIED,
    ACTIVATIONDAY,
    TECHPACK_NAME from
    dc.LOG_MonitoredTypes;

create view dcpublic.LOG_tablesizes
  as select OWNERNAME,
    TABLENAME,
    COLUMNS,
    KBYTES,
    PAGES,
    COMPRESSEDPAGES,
    NBLOCKS,
    ROWS,
    MINDATE,
    MAXDATE,
    UPDATED from
    dc.LOG_tablesizes;

create view dcpublic.LOG_Aggregations
  as select AGGREGATION,
    VERSIONID,
    AGGREGATIONSET,
    AGGREGATIONGROUP,
    REAGGREGATIONSET,
    REAGGREGATIONGROUP,
    GROUPORDER,
    AGGREGATIONORDER,
    AGGREGATIONTYPE,
    AGGREGATIONSCOPE,
    STATUS,
    MODIFIED from
    dc.LOG_Aggregations;

create view dcpublic.LOG_AggregationRules
  as select AGGREGATION,
    RULEID,
    TARGET_TYPE,
    TARGET_LEVEL,
    TARGET_TABLE,
    SOURCE_TYPE,
    SOURCE_LEVEL,
    SOURCE_TABLE,
    RULETYPE,
    AGGREGATIONSCOPE,
    STATUS,
    MODIFIED,
    BHTYPE from
    dc.LOG_AggregationRules;

create view dcpublic.LOG_MonitoringRules
  as select TYPENAME,
    TIMELEVEL,
    RULENAME,
    THRESHOLD,
    STATUS,
    MODIFIED,
    TECHPACK_NAME from
    dc.LOG_MonitoringRules;

create view dc.LOG_SESSION_ADAPTER as
  select * from dc.LOG_SESSION_ADAPTER_01 union all
  select * from dc.LOG_SESSION_ADAPTER_02 union all
  select * from dc.LOG_SESSION_ADAPTER_03 union all
  select * from dc.LOG_SESSION_ADAPTER_04 union all
  select * from dc.LOG_SESSION_ADAPTER_05 union all
  select * from dc.LOG_SESSION_ADAPTER_06 union all
  select * from dc.LOG_SESSION_ADAPTER_07;

create view dcpublic.LOG_SESSION_ADAPTER
  as select SESSION_ID,
    BATCH_ID,
    DATE_ID,
    ROWSTATUS,
    FILENAME,
    SESSIONENDTIME,
    SESSIONSTARTTIME,
    SOURCE,
    STATUS,
    SOURCEFILE_MODTIME,
    FLAG from
    dc.LOG_SESSION_ADAPTER where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.LOG_SESSION_LOADER as
  select * from dc.LOG_SESSION_LOADER_01 union all
  select * from dc.LOG_SESSION_LOADER_02 union all
  select * from dc.LOG_SESSION_LOADER_03 union all
  select * from dc.LOG_SESSION_LOADER_04 union all
  select * from dc.LOG_SESSION_LOADER_05 union all
  select * from dc.LOG_SESSION_LOADER_06 union all
  select * from dc.LOG_SESSION_LOADER_07;

create view dcpublic.LOG_SESSION_LOADER
  as select LOADERSET_ID,
    SESSION_ID,
    BATCH_ID,
    DATE_ID,
    ROWSTATUS,
    TIMELEVEL,
    DATADATE,
    DATATIME,
    ROWCOUNT,
    SESSIONENDTIME,
    SESSIONSTARTTIME,
    SOURCE,
    STATUS,
    TYPENAME,
    FLAG from
    dc.LOG_SESSION_LOADER where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.LOG_SESSION_AGGREGATOR as
  select * from dc.LOG_SESSION_AGGREGATOR_01 union all
  select * from dc.LOG_SESSION_AGGREGATOR_02 union all
  select * from dc.LOG_SESSION_AGGREGATOR_03 union all
  select * from dc.LOG_SESSION_AGGREGATOR_04 union all
  select * from dc.LOG_SESSION_AGGREGATOR_05 union all
  select * from dc.LOG_SESSION_AGGREGATOR_06 union all
  select * from dc.LOG_SESSION_AGGREGATOR_07;

create view dcpublic.LOG_SESSION_AGGREGATOR
  as select AGGREGATORSET_ID,
    SESSION_ID,
    BATCH_ID,
    DATE_ID,
    ROWSTATUS,
    TIMELEVEL,
    DATADATE,
    DATATIME,
    ROWCOUNT,
    SESSIONENDTIME,
    SESSIONSTARTTIME,
    SOURCE,
    STATUS,
    TYPENAME,
    FLAG from
    dc.LOG_SESSION_AGGREGATOR where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.LOG_LoadStatus as
  select * from dc.LOG_LoadStatus_01 union all
  select * from dc.LOG_LoadStatus_02 union all
  select * from dc.LOG_LoadStatus_03 union all
  select * from dc.LOG_LoadStatus_04 union all
  select * from dc.LOG_LoadStatus_05 union all
  select * from dc.LOG_LoadStatus_06 union all
  select * from dc.LOG_LoadStatus_07;

create view dc.DC_E_IMS_PTT_PTTPOCXDMS_DAY as
  select * from dc.DC_E_IMS_PTT_PTTPOCXDMS_DAY_01 union all
  select * from dc.DC_E_IMS_PTT_PTTPOCXDMS_DAY_03 union all
  select * from dc.DC_E_IMS_PTT_PTTPOCXDMS_DAY_05 union all
  select * from dc.DC_E_IMS_PTT_PTTPOCXDMS_DAY_02 union all
  select * from dc.DC_E_IMS_PTT_PTTPOCXDMS_DAY_04 union all
  select * from dc.DC_E_IMS_PTT_PTTPOCXDMS_DAY_06;

create view dcpublic.LOG_LoadStatus
  as select TYPENAME,
    TIMELEVEL,
    DATADATE,
    DATATIME,
    DATE_ID,
    ROWSTATUS,
    ROWCOUNT,
    SOURCECOUNT,
    STATUS,
    DESCRIPTION,
    MODIFIED from
    dc.LOG_LoadStatus where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.LOG_AggregationStatus as
  select * from dc.LOG_AggregationStatus_01 union all
  select * from dc.LOG_AggregationStatus_02 union all
  select * from dc.LOG_AggregationStatus_03 union all
  select * from dc.LOG_AggregationStatus_04 union all
  select * from dc.LOG_AggregationStatus_05 union all
  select * from dc.LOG_AggregationStatus_06 union all
  select * from dc.LOG_AggregationStatus_07;

create view dcpublic.LOG_AggregationStatus
  as select AGGREGATION,
    TYPENAME,
    TIMELEVEL,
    DATADATE,
    DATE_ID,
    INITIAL_AGGREGATION,
    STATUS,
    DESCRIPTION,
    ROWCOUNT,
    AGGREGATIONSCOPE,
    LAST_AGGREGATION,
    LOOPCOUNT from
    dc.LOG_AggregationStatus where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dcpublic.DIM_E_RAN_SITE
  as select OSS_ID,
    SITE_FDN,
    SITE_ID,
    SITE_NAME,
    TIMEZONE,
    TIMEZONE_VALUE,
    DST,
    VENDOR,
    STATUS,
    CREATED,
    MODIFIED,
    MODIFIER from
    dc.DIM_E_RAN_SITE;

create view dcpublic.DIM_E_RAN_SITEDST
  as select OSS_ID,
    SITE_FDN,
    SITE_ID,
    SITE_NAME,
    DST,
    VENDOR,
    STATUS,
    CREATED,
    MODIFIED,
    MODIFIER from
    dc.DIM_E_RAN_SITEDST;

create view dcpublic.DIM_E_RAN_RNC
  as select OSS_ID,
    SUBNETWORK,
    RNC_FDN,
    RNC_NAME,
    RNC_ID,
    SITE_FDN,
    SITE,
    MECONTEXTID,
    NEMIMVERSION,
    RNC_VERSION,
    MSC,
    MCC,
    MNC,
    VENDOR,
    STATUS,
    CREATED,
    MODIFIED,
    MODIFIER from
    dc.DIM_E_RAN_RNC;

create view dcpublic.DIM_E_RAN_RBS
  as select OSS_ID,
    RBS_FDN,
    RBS_ID,
    RBS_NAME,
    MECONTEXTID,
    NEMIMVERSION,
    RBS_VERSION,
    RBSIUBID,
    RNC_ID,
    SITE_FDN,
    SITE,
    MCC,
    MNC,
    VENDOR,
    STATUS,
    CREATED,
    MODIFIED,
    MODIFIER from
    dc.DIM_E_RAN_RBS;

create view dcpublic.DIM_E_RAN_RXI
  as select OSS_ID,
    RXI_FDN,
    RXI_ID,
    RXI_NAME,
    MECONTEXTID,
    NEMIMVERSION,
    RXI_VERSION,
    SITE_FDN,
    SITE,
    VENDOR,
    STATUS,
    CREATED,
    MODIFIED,
    MODIFIER from
    dc.DIM_E_RAN_RXI;

create view dcpublic.DIM_E_RAN_RBSLOCALCELL
  as select OSS_ID,
    RBSLOCALCELL_FDN,
    RBSLOCALCELL_ID,
    RBSLOCALCELL_NAME,
    RBSLOCALCELL_CARRIER,
    Sector,
    Carrier,
    RBS_ID,
    RNC_ID,
    VENDOR,
    STATUS,
    CREATED,
    MODIFIED,
    MODIFIER from
    dc.DIM_E_RAN_RBSLOCALCELL;

create view dcpublic.DIM_E_RAN_UCELL
  as select OSS_ID,
    UCELL_FDN,
    UCELL_ID,
    UCELL_NAME,
    LOCALCellID,
    RBS_ID,
    NODEB,
    SITE,
    RNC_ID,
    MSC,
    LAC,
    RAC,
    SAC,
    BCF_TYPE,
    MCC,
    MNC,
    UARFCNDL,
    UARFCNUL,
    SCRAMBLINGCODE,
    VENDOR,
    STATUS,
    CREATED,
    MODIFIED,
    MODIFIER from
    dc.DIM_E_RAN_UCELL;

create view dcpublic.DIM_E_RAN_UREL
  as select OSS_ID,
    UCELL_ID,
    UTRANRELATION_FDN,
    UTRANRELATION,
    ADJACENTCELL_FDN,
    ADJACENTCELL_SUB,
    ADJACENTCELL_ID,
    ADJACENT_RNC_ID,
    RNC_ID,
    VENDOR,
    STATUS,
    CREATED,
    MODIFIED,
    MODIFIER from
    dc.DIM_E_RAN_UREL;

create view dc.DIM_E_RAN_SITE_CURRENT as select * from dc.DIM_E_RAN_SITE_CURRENT_DC;

create view dc.DIM_E_RAN_SITEDST_CURRENT as select * from dc.DIM_E_RAN_SITEDST_CURRENT_DC;

create view dc.DIM_E_RAN_RNC_CURRENT as select * from dc.DIM_E_RAN_RNC_CURRENT_DC;

create view dc.DIM_E_RAN_RBS_CURRENT as select * from dc.DIM_E_RAN_RBS_CURRENT_DC;

create view dc.DIM_E_RAN_RXI_CURRENT as select * from dc.DIM_E_RAN_RXI_CURRENT_DC;

create view dc.DIM_E_RAN_RBSLOCALCELL_CURRENT as select * from dc.DIM_E_RAN_RBSLOCALCELL_CURRENT_DC;

create view dc.DIM_E_RAN_UCELL_CURRENT as select * from dc.DIM_E_RAN_UCELL_CURRENT_DC;

create view dc.DIM_E_RAN_UREL_CURRENT as select * from dc.DIM_E_RAN_UREL_CURRENT_DC;

create view
  dc.SELECT_DIM_E_UTRAN_AGGLEVEL( FIRSTDATE_ID,AGGLEVEL,DATE_ID,YEAR_ID,MONTH_ID,DAY_ID,WEEK_ID,WEEKDAY_ID,BUSINESSDAY,
  TIMELEVEL,TIMELEVELBH,DAYS) 
  as select FIRSTDATE_ID,AGGLEVEL,DATE_ID,YEAR_ID,MONTH_ID,DAY_ID,WEEK_ID,WEEKDAY_ID,BUSINESSDAY,
    TIMELEVEL,TIMELEVELBH,DAYS from dc.SELECT_AGGLEVEL_TABLE where
    TIMELEVELBH = 'DAYBH';

create view dcpublic.DIM_E_RBS_ELEMBH_BHTYPE
  as select BHTYPE,
    DESCRIPTION from
    dc.DIM_E_RBS_ELEMBH_BHTYPE;

create view dcpublic.DIM_E_RBS_CARRIERBH_BHTYPE
  as select BHTYPE,
    DESCRIPTION from
    dc.DIM_E_RBS_CARRIERBH_BHTYPE;

create view dc.DC_E_RBS_AICH_RAW as
  select * from dc.DC_E_RBS_AICH_RAW_01 union all
  select * from dc.DC_E_RBS_AICH_RAW_02 union all
  select * from dc.DC_E_RBS_AICH_RAW_03 union all
  select * from dc.DC_E_RBS_AICH_RAW_04 union all
  select * from dc.DC_E_RBS_AICH_RAW_05 union all
  select * from dc.DC_E_RBS_AICH_RAW_06;

create view dcpublic.DC_E_RBS_AICH_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    Aich,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmPositiveMessages,
    pmNegativeMessages from
    dc.DC_E_RBS_AICH_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_AICH_DAYBH as
  select * from dc.DC_E_RBS_AICH_DAYBH_01 union all
  select * from dc.DC_E_RBS_AICH_DAYBH_02 union all
  select * from dc.DC_E_RBS_AICH_DAYBH_03 union all
  select * from dc.DC_E_RBS_AICH_DAYBH_04 union all
  select * from dc.DC_E_RBS_AICH_DAYBH_05 union all
  select * from dc.DC_E_RBS_AICH_DAYBH_06;

create view dcpublic.DC_E_RBS_AICH_DAYBH
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    Aich,
    DATE_ID,
    MIN_ID,
    BHTYPE,
    BUSYHOUR,
    BHCLASS,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmPositiveMessages,
    pmNegativeMessages from
    dc.DC_E_RBS_AICH_DAYBH where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_AICH_DAY as
  select * from dc.DC_E_RBS_AICH_DAY_01 union all
  select * from dc.DC_E_RBS_AICH_DAY_02 union all
  select * from dc.DC_E_RBS_AICH_DAY_03 union all
  select * from dc.DC_E_RBS_AICH_DAY_04 union all
  select * from dc.DC_E_RBS_AICH_DAY_05 union all
  select * from dc.DC_E_RBS_AICH_DAY_06;

create view dcpublic.DC_E_RBS_AICH_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    Aich,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmPositiveMessages,
    pmNegativeMessages from
    dc.DC_E_RBS_AICH_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_ANTENNABRANCH_RAW as
  select * from dc.DC_E_RBS_ANTENNABRANCH_RAW_01 union all
  select * from dc.DC_E_RBS_ANTENNABRANCH_RAW_02 union all
  select * from dc.DC_E_RBS_ANTENNABRANCH_RAW_03 union all
  select * from dc.DC_E_RBS_ANTENNABRANCH_RAW_04 union all
  select * from dc.DC_E_RBS_ANTENNABRANCH_RAW_05 union all
  select * from dc.DC_E_RBS_ANTENNABRANCH_RAW_06;

create view dcpublic.DC_E_RBS_ANTENNABRANCH_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    Equipment,
    SectorAntenna,
    AntennaBranch,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfPowLimSlots from
    dc.DC_E_RBS_ANTENNABRANCH_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_ANTENNABRANCH_DAY as
  select * from dc.DC_E_RBS_ANTENNABRANCH_DAY_01 union all
  select * from dc.DC_E_RBS_ANTENNABRANCH_DAY_02 union all
  select * from dc.DC_E_RBS_ANTENNABRANCH_DAY_03 union all
  select * from dc.DC_E_RBS_ANTENNABRANCH_DAY_04 union all
  select * from dc.DC_E_RBS_ANTENNABRANCH_DAY_05 union all
  select * from dc.DC_E_RBS_ANTENNABRANCH_DAY_06;

create view dcpublic.DC_E_RBS_ANTENNABRANCH_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    Equipment,
    SectorAntenna,
    AntennaBranch,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmNoOfPowLimSlots from
    dc.DC_E_RBS_ANTENNABRANCH_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_DLBASEBANDPOOL_RAW as
  select * from dc.DC_E_RBS_DLBASEBANDPOOL_RAW_01 union all
  select * from dc.DC_E_RBS_DLBASEBANDPOOL_RAW_02 union all
  select * from dc.DC_E_RBS_DLBASEBANDPOOL_RAW_03;

create view dcpublic.DC_E_RBS_DLBASEBANDPOOL_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    Equipment,
    Subrack,
    DownlinkBaseBandPool,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmApomcOfMdlr,
    pmApomcOfMdsr,
    pmApomcOfSpreadersUsed,
    pmNoOfRlAddittionFailuresSf128,
    pmNoOfRlAddittionFailuresSf16,
    pmNoOfRlAddittionFailuresSf256,
    pmNoOfRlAddittionFailuresSf32,
    pmNoOfRlAddittionFailuresSf4,
    pmNoOfRlAddittionFailuresSf64,
    pmNoOfRlAddittionFailuresSf8,
    pmSetupAttemptsSf128,
    pmSetupAttemptsSf16,
    pmSetupAttemptsSf256,
    pmSetupAttemptsSf32,
    pmSetupAttemptsSf4,
    pmSetupAttemptsSf64,
    pmSetupAttemptsSf8,
    pmSetupFailuresSf128,
    pmSetupFailuresSf16,
    pmSetupFailuresSf256,
    pmSetupFailuresSf32,
    pmSetupFailuresSf4,
    pmSetupFailuresSf64,
    pmSetupFailuresSf8,
    pmCapacityAllocAttDlCe,
    pmCapacityAllocRejDlCe,
    pmSumCapacityDlCe,
    pmSamplesCapacityDlCe,
    pmSumSqrCapacityDlCe,
    pmAllocRejADch,
    pmUsedADch from
    dc.DC_E_RBS_DLBASEBANDPOOL_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_DLBASEBANDPOOL_DAY as
  select * from dc.DC_E_RBS_DLBASEBANDPOOL_DAY_01 union all
  select * from dc.DC_E_RBS_DLBASEBANDPOOL_DAY_02 union all
  select * from dc.DC_E_RBS_DLBASEBANDPOOL_DAY_03;

create view dcpublic.DC_E_RBS_DLBASEBANDPOOL_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    Equipment,
    Subrack,
    DownlinkBaseBandPool,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmApomcOfMdlr,
    pmApomcOfMdsr,
    pmApomcOfSpreadersUsed,
    pmNoOfRlAddittionFailuresSf128,
    pmNoOfRlAddittionFailuresSf16,
    pmNoOfRlAddittionFailuresSf256,
    pmNoOfRlAddittionFailuresSf32,
    pmNoOfRlAddittionFailuresSf4,
    pmNoOfRlAddittionFailuresSf64,
    pmNoOfRlAddittionFailuresSf8,
    pmSetupAttemptsSf128,
    pmSetupAttemptsSf16,
    pmSetupAttemptsSf256,
    pmSetupAttemptsSf32,
    pmSetupAttemptsSf4,
    pmSetupAttemptsSf64,
    pmSetupAttemptsSf8,
    pmSetupFailuresSf128,
    pmSetupFailuresSf16,
    pmSetupFailuresSf256,
    pmSetupFailuresSf32,
    pmSetupFailuresSf4,
    pmSetupFailuresSf64,
    pmSetupFailuresSf8,
    pmCapacityAllocAttDlCe,
    pmCapacityAllocRejDlCe,
    pmSumCapacityDlCe,
    pmSamplesCapacityDlCe,
    pmSumSqrCapacityDlCe,
    pmAllocRejADch,
    pmUsedADch from
    dc.DC_E_RBS_DLBASEBANDPOOL_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_EDCHRESOURCES_RAW as
  select * from dc.DC_E_RBS_EDCHRESOURCES_RAW_01 union all
  select * from dc.DC_E_RBS_EDCHRESOURCES_RAW_02 union all
  select * from dc.DC_E_RBS_EDCHRESOURCES_RAW_03 union all
  select * from dc.DC_E_RBS_EDCHRESOURCES_RAW_04 union all
  select * from dc.DC_E_RBS_EDCHRESOURCES_RAW_05 union all
  select * from dc.DC_E_RBS_EDCHRESOURCES_RAW_06;

create view dcpublic.DC_E_RBS_EDCHRESOURCES_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    EDchResources,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmSumNackedBitsCellEul,
    pmSumAckedBitsCellEul,
    pmNoActive10msFramesEul,
    pmNoUlUuLoadLimitEul,
    pmNoAllowedEul,
    pmNoActive2msFramesEul,
    pmCapacityAllocAttServEDchUsers,
    pmCapacityAllocRejServEDchUsers,
    pmNoActive10msIntervalsEulTti10,
    pmNoActive2msIntervalsEul,
    pmNoActive2msIntervalsEulTti2,
    pmSamplesCapacityServEDchUsers,
    pmSumAckedBitsCellEulTti10,
    pmSumAckedBitsCellEulTti2,
    pmSumCapacityServEDchUsers,
    pmSumNackedBitsCellEulTti10,
    pmSumNackedBitsCellEulTti2,
    pmSumSqrCapacityServEDchUsers,
    pmCapacityServEDchUsers from
    dc.DC_E_RBS_EDCHRESOURCES_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_EDCHRESOURCES_DAY as
  select * from dc.DC_E_RBS_EDCHRESOURCES_DAY_01 union all
  select * from dc.DC_E_RBS_EDCHRESOURCES_DAY_02 union all
  select * from dc.DC_E_RBS_EDCHRESOURCES_DAY_03 union all
  select * from dc.DC_E_RBS_EDCHRESOURCES_DAY_04 union all
  select * from dc.DC_E_RBS_EDCHRESOURCES_DAY_05 union all
  select * from dc.DC_E_RBS_EDCHRESOURCES_DAY_06;

create view dcpublic.DC_E_RBS_EDCHRESOURCES_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    EDchResources,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmSumNackedBitsCellEul,
    pmSumAckedBitsCellEul,
    pmNoActive10msFramesEul,
    pmNoUlUuLoadLimitEul,
    pmNoAllowedEul,
    pmNoActive2msFramesEul,
    pmCapacityAllocAttServEDchUsers,
    pmCapacityAllocRejServEDchUsers,
    pmNoActive10msIntervalsEulTti10,
    pmNoActive2msIntervalsEul,
    pmNoActive2msIntervalsEulTti2,
    pmSamplesCapacityServEDchUsers,
    pmSumAckedBitsCellEulTti10,
    pmSumAckedBitsCellEulTti2,
    pmSumCapacityServEDchUsers,
    pmSumNackedBitsCellEulTti10,
    pmSumNackedBitsCellEulTti2,
    pmSumSqrCapacityServEDchUsers,
    pmCapacityServEDchUsers from
    dc.DC_E_RBS_EDCHRESOURCES_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_EDCHRESOURCES_DAYBH as
  select * from dc.DC_E_RBS_EDCHRESOURCES_DAYBH_01 union all
  select * from dc.DC_E_RBS_EDCHRESOURCES_DAYBH_02 union all
  select * from dc.DC_E_RBS_EDCHRESOURCES_DAYBH_03 union all
  select * from dc.DC_E_RBS_EDCHRESOURCES_DAYBH_04 union all
  select * from dc.DC_E_RBS_EDCHRESOURCES_DAYBH_05 union all
  select * from dc.DC_E_RBS_EDCHRESOURCES_DAYBH_06;

create view dcpublic.DC_E_RBS_EDCHRESOURCES_DAYBH
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    EDchResources,
    DATE_ID,
    MIN_ID,
    BHTYPE,
    BUSYHOUR,
    BHCLASS,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmSumNackedBitsCellEul,
    pmSumAckedBitsCellEul,
    pmNoActive10msFramesEul,
    pmNoUlUuLoadLimitEul,
    pmNoAllowedEul,
    pmNoActive2msFramesEul,
    pmCapacityAllocAttServEDchUsers,
    pmCapacityAllocRejServEDchUsers,
    pmNoActive10msIntervalsEulTti10,
    pmNoActive2msIntervalsEul,
    pmNoActive2msIntervalsEulTti2,
    pmSamplesCapacityServEDchUsers,
    pmSumAckedBitsCellEulTti10,
    pmSumAckedBitsCellEulTti2,
    pmSumCapacityServEDchUsers,
    pmSumNackedBitsCellEulTti10,
    pmSumNackedBitsCellEulTti2,
    pmSumSqrCapacityServEDchUsers,
    pmCapacityServEDchUsers from
    dc.DC_E_RBS_EDCHRESOURCES_DAYBH where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_HSDSCHRES_RAW as
  select * from dc.DC_E_RBS_HSDSCHRES_RAW_01 union all
  select * from dc.DC_E_RBS_HSDSCHRES_RAW_02 union all
  select * from dc.DC_E_RBS_HSDSCHRES_RAW_03 union all
  select * from dc.DC_E_RBS_HSDSCHRES_RAW_04 union all
  select * from dc.DC_E_RBS_HSDSCHRES_RAW_05 union all
  select * from dc.DC_E_RBS_HSDSCHRES_RAW_06;

create view dcpublic.DC_E_RBS_HSDSCHRES_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    HsDschResources,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmAckReceived,
    pmIubMacdPduCellReceivedBits,
    pmNackReceived,
    pmNoActiveSubFrames,
    pmNoInactiveRequiredSubFrames,
    pmSumAckedBits,
    pmSumNonEmptyUserBuffers,
    pmSumTransmittedBits,
    pmSumNumHsPdschCodesAdded,
    pmSampleNumHsPdschCodesAdded,
    pmSumNoOfUsersSpi00,
    pmSumNoOfUsersSpi01,
    pmSumNoOfUsersSpi02,
    pmSumNoOfUsersSpi03,
    pmSumNoOfUsersSpi04,
    pmSumNoOfUsersSpi05,
    pmSumNoOfUsersSpi06,
    pmSumNoOfUsersSpi07,
    pmSumNoOfUsersSpi08,
    pmSumNoOfUsersSpi09,
    pmSumNoOfUsersSpi10,
    pmSumNoOfUsersSpi11,
    pmSumNoOfUsersSpi12,
    pmSumNoOfUsersSpi13,
    pmSumNoOfUsersSpi14,
    pmSumNoOfUsersSpi15,
    pmSumDelaySpi00,
    pmSumDelaySpi01,
    pmSumDelaySpi02,
    pmSumDelaySpi03,
    pmSumDelaySpi04,
    pmSumDelaySpi05,
    pmSumDelaySpi06,
    pmSumDelaySpi07,
    pmSumDelaySpi08,
    pmSumDelaySpi09,
    pmSumDelaySpi10,
    pmSumDelaySpi11,
    pmSumDelaySpi12,
    pmSumDelaySpi13,
    pmSumDelaySpi14,
    pmSumDelaySpi15,
    pmSumJitterSpi00,
    pmSumJitterSpi01,
    pmSumJitterSpi02,
    pmSumJitterSpi03,
    pmSumJitterSpi04,
    pmSumJitterSpi05,
    pmSumJitterSpi06,
    pmSumJitterSpi07,
    pmSumJitterSpi08,
    pmSumJitterSpi09,
    pmSumJitterSpi10,
    pmSumJitterSpi11,
    pmSumJitterSpi12,
    pmSumJitterSpi13,
    pmSumJitterSpi14,
    pmSumJitterSpi15,
    pmSumTransmittedBitsSpi00,
    pmSumTransmittedBitsSpi01,
    pmSumTransmittedBitsSpi02,
    pmSumTransmittedBitsSpi03,
    pmSumTransmittedBitsSpi04,
    pmSumTransmittedBitsSpi05,
    pmSumTransmittedBitsSpi06,
    pmSumTransmittedBitsSpi07,
    pmSumTransmittedBitsSpi08,
    pmSumTransmittedBitsSpi09,
    pmSumTransmittedBitsSpi10,
    pmSumTransmittedBitsSpi11,
    pmSumTransmittedBitsSpi12,
    pmSumTransmittedBitsSpi13,
    pmSumTransmittedBitsSpi14,
    pmSumTransmittedBitsSpi15,
    pmSumAckedBitsSpi00,
    pmSumAckedBitsSpi01,
    pmSumAckedBitsSpi02,
    pmSumAckedBitsSpi03,
    pmSumAckedBitsSpi04,
    pmSumAckedBitsSpi05,
    pmSumAckedBitsSpi06,
    pmSumAckedBitsSpi07,
    pmSumAckedBitsSpi08,
    pmSumAckedBitsSpi09,
    pmSumAckedBitsSpi10,
    pmSumAckedBitsSpi11,
    pmSumAckedBitsSpi12,
    pmSumAckedBitsSpi13,
    pmSumAckedBitsSpi14,
    pmSumAckedBitsSpi15,
    pmNoActiveSubFramesSpi00,
    pmNoActiveSubFramesSpi01,
    pmNoActiveSubFramesSpi02,
    pmNoActiveSubFramesSpi03,
    pmNoActiveSubFramesSpi04,
    pmNoActiveSubFramesSpi05,
    pmNoActiveSubFramesSpi06,
    pmNoActiveSubFramesSpi07,
    pmNoActiveSubFramesSpi08,
    pmNoActiveSubFramesSpi09,
    pmNoActiveSubFramesSpi10,
    pmNoActiveSubFramesSpi11,
    pmNoActiveSubFramesSpi12,
    pmNoActiveSubFramesSpi13,
    pmNoActiveSubFramesSpi14,
    pmNoActiveSubFramesSpi15,
    pmNoInactiveRequiredSubFramesSpi00,
    pmNoInactiveRequiredSubFramesSpi01,
    pmNoInactiveRequiredSubFramesSpi02,
    pmNoInactiveRequiredSubFramesSpi03,
    pmNoInactiveRequiredSubFramesSpi04,
    pmNoInactiveRequiredSubFramesSpi05,
    pmNoInactiveRequiredSubFramesSpi06,
    pmNoInactiveRequiredSubFramesSpi07,
    pmNoInactiveRequiredSubFramesSpi08,
    pmNoInactiveRequiredSubFramesSpi09,
    pmNoInactiveRequiredSubFramesSpi10,
    pmNoInactiveRequiredSubFramesSpi11,
    pmNoInactiveRequiredSubFramesSpi12,
    pmNoInactiveRequiredSubFramesSpi13,
    pmNoInactiveRequiredSubFramesSpi14,
    pmNoInactiveRequiredSubFramesSpi15,
    pmSumNonEmptyUserBuffersSpi00,
    pmSumNonEmptyUserBuffersSpi01,
    pmSumNonEmptyUserBuffersSpi02,
    pmSumNonEmptyUserBuffersSpi03,
    pmSumNonEmptyUserBuffersSpi04,
    pmSumNonEmptyUserBuffersSpi05,
    pmSumNonEmptyUserBuffersSpi06,
    pmSumNonEmptyUserBuffersSpi07,
    pmSumNonEmptyUserBuffersSpi08,
    pmSumNonEmptyUserBuffersSpi09,
    pmSumNonEmptyUserBuffersSpi10,
    pmSumNonEmptyUserBuffersSpi11,
    pmSumNonEmptyUserBuffersSpi12,
    pmSumNonEmptyUserBuffersSpi13,
    pmSumNonEmptyUserBuffersSpi14,
    pmSumNonEmptyUserBuffersSpi15,
    pmCapacityAllocAttHsDschUsers,
    pmCapacityAllocAttHsPdschCodes,
    pmCapacityAllocRejHsDschUsers,
    pmCapacityAllocRejHsPdschCodes,
    pmSamplesCapacityHsDschUsers,
    pmSamplesCapacityHsPdschCodes,
    pmSumCapacityHsDschUsers,
    pmSumCapacityHsPdschCodes,
    pmSumSqrCapacityHsDschUsers,
    pmSumSqrCapacityHsPdschCodes,
    pmAllocRejHwHsDschUsers,
    pmCapacityHsDschUsers,
    pmCapacityHsPdschCodes from
    dc.DC_E_RBS_HSDSCHRES_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_HSDSCHRES_DAY as
  select * from dc.DC_E_RBS_HSDSCHRES_DAY_01 union all
  select * from dc.DC_E_RBS_HSDSCHRES_DAY_02 union all
  select * from dc.DC_E_RBS_HSDSCHRES_DAY_03 union all
  select * from dc.DC_E_RBS_HSDSCHRES_DAY_04 union all
  select * from dc.DC_E_RBS_HSDSCHRES_DAY_05 union all
  select * from dc.DC_E_RBS_HSDSCHRES_DAY_06;

create view dcpublic.DC_E_RBS_HSDSCHRES_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    HsDschResources,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmAckReceived,
    pmIubMacdPduCellReceivedBits,
    pmNackReceived,
    pmNoActiveSubFrames,
    pmNoInactiveRequiredSubFrames,
    pmSumAckedBits,
    pmSumNonEmptyUserBuffers,
    pmSumTransmittedBits,
    pmSumNumHsPdschCodesAdded,
    pmSampleNumHsPdschCodesAdded,
    pmSumNoOfUsersSpi00,
    pmSumNoOfUsersSpi01,
    pmSumNoOfUsersSpi02,
    pmSumNoOfUsersSpi03,
    pmSumNoOfUsersSpi04,
    pmSumNoOfUsersSpi05,
    pmSumNoOfUsersSpi06,
    pmSumNoOfUsersSpi07,
    pmSumNoOfUsersSpi08,
    pmSumNoOfUsersSpi09,
    pmSumNoOfUsersSpi10,
    pmSumNoOfUsersSpi11,
    pmSumNoOfUsersSpi12,
    pmSumNoOfUsersSpi13,
    pmSumNoOfUsersSpi14,
    pmSumNoOfUsersSpi15,
    pmSumDelaySpi00,
    pmSumDelaySpi01,
    pmSumDelaySpi02,
    pmSumDelaySpi03,
    pmSumDelaySpi04,
    pmSumDelaySpi05,
    pmSumDelaySpi06,
    pmSumDelaySpi07,
    pmSumDelaySpi08,
    pmSumDelaySpi09,
    pmSumDelaySpi10,
    pmSumDelaySpi11,
    pmSumDelaySpi12,
    pmSumDelaySpi13,
    pmSumDelaySpi14,
    pmSumDelaySpi15,
    pmSumJitterSpi00,
    pmSumJitterSpi01,
    pmSumJitterSpi02,
    pmSumJitterSpi03,
    pmSumJitterSpi04,
    pmSumJitterSpi05,
    pmSumJitterSpi06,
    pmSumJitterSpi07,
    pmSumJitterSpi08,
    pmSumJitterSpi09,
    pmSumJitterSpi10,
    pmSumJitterSpi11,
    pmSumJitterSpi12,
    pmSumJitterSpi13,
    pmSumJitterSpi14,
    pmSumJitterSpi15,
    pmSumTransmittedBitsSpi00,
    pmSumTransmittedBitsSpi01,
    pmSumTransmittedBitsSpi02,
    pmSumTransmittedBitsSpi03,
    pmSumTransmittedBitsSpi04,
    pmSumTransmittedBitsSpi05,
    pmSumTransmittedBitsSpi06,
    pmSumTransmittedBitsSpi07,
    pmSumTransmittedBitsSpi08,
    pmSumTransmittedBitsSpi09,
    pmSumTransmittedBitsSpi10,
    pmSumTransmittedBitsSpi11,
    pmSumTransmittedBitsSpi12,
    pmSumTransmittedBitsSpi13,
    pmSumTransmittedBitsSpi14,
    pmSumTransmittedBitsSpi15,
    pmSumAckedBitsSpi00,
    pmSumAckedBitsSpi01,
    pmSumAckedBitsSpi02,
    pmSumAckedBitsSpi03,
    pmSumAckedBitsSpi04,
    pmSumAckedBitsSpi05,
    pmSumAckedBitsSpi06,
    pmSumAckedBitsSpi07,
    pmSumAckedBitsSpi08,
    pmSumAckedBitsSpi09,
    pmSumAckedBitsSpi10,
    pmSumAckedBitsSpi11,
    pmSumAckedBitsSpi12,
    pmSumAckedBitsSpi13,
    pmSumAckedBitsSpi14,
    pmSumAckedBitsSpi15,
    pmNoActiveSubFramesSpi00,
    pmNoActiveSubFramesSpi01,
    pmNoActiveSubFramesSpi02,
    pmNoActiveSubFramesSpi03,
    pmNoActiveSubFramesSpi04,
    pmNoActiveSubFramesSpi05,
    pmNoActiveSubFramesSpi06,
    pmNoActiveSubFramesSpi07,
    pmNoActiveSubFramesSpi08,
    pmNoActiveSubFramesSpi09,
    pmNoActiveSubFramesSpi10,
    pmNoActiveSubFramesSpi11,
    pmNoActiveSubFramesSpi12,
    pmNoActiveSubFramesSpi13,
    pmNoActiveSubFramesSpi14,
    pmNoActiveSubFramesSpi15,
    pmNoInactiveRequiredSubFramesSpi00,
    pmNoInactiveRequiredSubFramesSpi01,
    pmNoInactiveRequiredSubFramesSpi02,
    pmNoInactiveRequiredSubFramesSpi03,
    pmNoInactiveRequiredSubFramesSpi04,
    pmNoInactiveRequiredSubFramesSpi05,
    pmNoInactiveRequiredSubFramesSpi06,
    pmNoInactiveRequiredSubFramesSpi07,
    pmNoInactiveRequiredSubFramesSpi08,
    pmNoInactiveRequiredSubFramesSpi09,
    pmNoInactiveRequiredSubFramesSpi10,
    pmNoInactiveRequiredSubFramesSpi11,
    pmNoInactiveRequiredSubFramesSpi12,
    pmNoInactiveRequiredSubFramesSpi13,
    pmNoInactiveRequiredSubFramesSpi14,
    pmNoInactiveRequiredSubFramesSpi15,
    pmSumNonEmptyUserBuffersSpi00,
    pmSumNonEmptyUserBuffersSpi01,
    pmSumNonEmptyUserBuffersSpi02,
    pmSumNonEmptyUserBuffersSpi03,
    pmSumNonEmptyUserBuffersSpi04,
    pmSumNonEmptyUserBuffersSpi05,
    pmSumNonEmptyUserBuffersSpi06,
    pmSumNonEmptyUserBuffersSpi07,
    pmSumNonEmptyUserBuffersSpi08,
    pmSumNonEmptyUserBuffersSpi09,
    pmSumNonEmptyUserBuffersSpi10,
    pmSumNonEmptyUserBuffersSpi11,
    pmSumNonEmptyUserBuffersSpi12,
    pmSumNonEmptyUserBuffersSpi13,
    pmSumNonEmptyUserBuffersSpi14,
    pmSumNonEmptyUserBuffersSpi15,
    pmCapacityAllocAttHsDschUsers,
    pmCapacityAllocAttHsPdschCodes,
    pmCapacityAllocRejHsDschUsers,
    pmCapacityAllocRejHsPdschCodes,
    pmSamplesCapacityHsDschUsers,
    pmSamplesCapacityHsPdschCodes,
    pmSumCapacityHsDschUsers,
    pmSumCapacityHsPdschCodes,
    pmSumSqrCapacityHsDschUsers,
    pmSumSqrCapacityHsPdschCodes,
    pmAllocRejHwHsDschUsers,
    pmCapacityHsDschUsers,
    pmCapacityHsPdschCodes from
    dc.DC_E_RBS_HSDSCHRES_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_HSDSCHRES_DAYBH as
  select * from dc.DC_E_RBS_HSDSCHRES_DAYBH_01 union all
  select * from dc.DC_E_RBS_HSDSCHRES_DAYBH_02 union all
  select * from dc.DC_E_RBS_HSDSCHRES_DAYBH_03 union all
  select * from dc.DC_E_RBS_HSDSCHRES_DAYBH_04 union all
  select * from dc.DC_E_RBS_HSDSCHRES_DAYBH_05 union all
  select * from dc.DC_E_RBS_HSDSCHRES_DAYBH_06;

create view dcpublic.DC_E_RBS_HSDSCHRES_DAYBH
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    HsDschResources,
    DATE_ID,
    MIN_ID,
    BHTYPE,
    BUSYHOUR,
    BHCLASS,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmAckReceived,
    pmIubMacdPduCellReceivedBits,
    pmNackReceived,
    pmNoActiveSubFrames,
    pmNoInactiveRequiredSubFrames,
    pmSumAckedBits,
    pmSumNonEmptyUserBuffers,
    pmSumTransmittedBits,
    pmSumNumHsPdschCodesAdded,
    pmSampleNumHsPdschCodesAdded,
    pmSumNoOfUsersSpi00,
    pmSumNoOfUsersSpi01,
    pmSumNoOfUsersSpi02,
    pmSumNoOfUsersSpi03,
    pmSumNoOfUsersSpi04,
    pmSumNoOfUsersSpi05,
    pmSumNoOfUsersSpi06,
    pmSumNoOfUsersSpi07,
    pmSumNoOfUsersSpi08,
    pmSumNoOfUsersSpi09,
    pmSumNoOfUsersSpi10,
    pmSumNoOfUsersSpi11,
    pmSumNoOfUsersSpi12,
    pmSumNoOfUsersSpi13,
    pmSumNoOfUsersSpi14,
    pmSumNoOfUsersSpi15,
    pmSumDelaySpi00,
    pmSumDelaySpi01,
    pmSumDelaySpi02,
    pmSumDelaySpi03,
    pmSumDelaySpi04,
    pmSumDelaySpi05,
    pmSumDelaySpi06,
    pmSumDelaySpi07,
    pmSumDelaySpi08,
    pmSumDelaySpi09,
    pmSumDelaySpi10,
    pmSumDelaySpi11,
    pmSumDelaySpi12,
    pmSumDelaySpi13,
    pmSumDelaySpi14,
    pmSumDelaySpi15,
    pmSumJitterSpi00,
    pmSumJitterSpi01,
    pmSumJitterSpi02,
    pmSumJitterSpi03,
    pmSumJitterSpi04,
    pmSumJitterSpi05,
    pmSumJitterSpi06,
    pmSumJitterSpi07,
    pmSumJitterSpi08,
    pmSumJitterSpi09,
    pmSumJitterSpi10,
    pmSumJitterSpi11,
    pmSumJitterSpi12,
    pmSumJitterSpi13,
    pmSumJitterSpi14,
    pmSumJitterSpi15,
    pmSumTransmittedBitsSpi00,
    pmSumTransmittedBitsSpi01,
    pmSumTransmittedBitsSpi02,
    pmSumTransmittedBitsSpi03,
    pmSumTransmittedBitsSpi04,
    pmSumTransmittedBitsSpi05,
    pmSumTransmittedBitsSpi06,
    pmSumTransmittedBitsSpi07,
    pmSumTransmittedBitsSpi08,
    pmSumTransmittedBitsSpi09,
    pmSumTransmittedBitsSpi10,
    pmSumTransmittedBitsSpi11,
    pmSumTransmittedBitsSpi12,
    pmSumTransmittedBitsSpi13,
    pmSumTransmittedBitsSpi14,
    pmSumTransmittedBitsSpi15,
    pmSumAckedBitsSpi00,
    pmSumAckedBitsSpi01,
    pmSumAckedBitsSpi02,
    pmSumAckedBitsSpi03,
    pmSumAckedBitsSpi04,
    pmSumAckedBitsSpi05,
    pmSumAckedBitsSpi06,
    pmSumAckedBitsSpi07,
    pmSumAckedBitsSpi08,
    pmSumAckedBitsSpi09,
    pmSumAckedBitsSpi10,
    pmSumAckedBitsSpi11,
    pmSumAckedBitsSpi12,
    pmSumAckedBitsSpi13,
    pmSumAckedBitsSpi14,
    pmSumAckedBitsSpi15,
    pmNoActiveSubFramesSpi00,
    pmNoActiveSubFramesSpi01,
    pmNoActiveSubFramesSpi02,
    pmNoActiveSubFramesSpi03,
    pmNoActiveSubFramesSpi04,
    pmNoActiveSubFramesSpi05,
    pmNoActiveSubFramesSpi06,
    pmNoActiveSubFramesSpi07,
    pmNoActiveSubFramesSpi08,
    pmNoActiveSubFramesSpi09,
    pmNoActiveSubFramesSpi10,
    pmNoActiveSubFramesSpi11,
    pmNoActiveSubFramesSpi12,
    pmNoActiveSubFramesSpi13,
    pmNoActiveSubFramesSpi14,
    pmNoActiveSubFramesSpi15,
    pmNoInactiveRequiredSubFramesSpi00,
    pmNoInactiveRequiredSubFramesSpi01,
    pmNoInactiveRequiredSubFramesSpi02,
    pmNoInactiveRequiredSubFramesSpi03,
    pmNoInactiveRequiredSubFramesSpi04,
    pmNoInactiveRequiredSubFramesSpi05,
    pmNoInactiveRequiredSubFramesSpi06,
    pmNoInactiveRequiredSubFramesSpi07,
    pmNoInactiveRequiredSubFramesSpi08,
    pmNoInactiveRequiredSubFramesSpi09,
    pmNoInactiveRequiredSubFramesSpi10,
    pmNoInactiveRequiredSubFramesSpi11,
    pmNoInactiveRequiredSubFramesSpi12,
    pmNoInactiveRequiredSubFramesSpi13,
    pmNoInactiveRequiredSubFramesSpi14,
    pmNoInactiveRequiredSubFramesSpi15,
    pmSumNonEmptyUserBuffersSpi00,
    pmSumNonEmptyUserBuffersSpi01,
    pmSumNonEmptyUserBuffersSpi02,
    pmSumNonEmptyUserBuffersSpi03,
    pmSumNonEmptyUserBuffersSpi04,
    pmSumNonEmptyUserBuffersSpi05,
    pmSumNonEmptyUserBuffersSpi06,
    pmSumNonEmptyUserBuffersSpi07,
    pmSumNonEmptyUserBuffersSpi08,
    pmSumNonEmptyUserBuffersSpi09,
    pmSumNonEmptyUserBuffersSpi10,
    pmSumNonEmptyUserBuffersSpi11,
    pmSumNonEmptyUserBuffersSpi12,
    pmSumNonEmptyUserBuffersSpi13,
    pmSumNonEmptyUserBuffersSpi14,
    pmSumNonEmptyUserBuffersSpi15,
    pmCapacityAllocAttHsDschUsers,
    pmCapacityAllocAttHsPdschCodes,
    pmCapacityAllocRejHsDschUsers,
    pmCapacityAllocRejHsPdschCodes,
    pmSamplesCapacityHsDschUsers,
    pmSamplesCapacityHsPdschCodes,
    pmSumCapacityHsDschUsers,
    pmSumCapacityHsPdschCodes,
    pmSumSqrCapacityHsDschUsers,
    pmSumSqrCapacityHsPdschCodes,
    pmAllocRejHwHsDschUsers,
    pmCapacityHsDschUsers,
    pmCapacityHsPdschCodes from
    dc.DC_E_RBS_HSDSCHRES_DAYBH where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_IUB_RAW as
  select * from dc.DC_E_RBS_IUB_RAW_01 union all
  select * from dc.DC_E_RBS_IUB_RAW_02 union all
  select * from dc.DC_E_RBS_IUB_RAW_03;

create view dcpublic.DC_E_RBS_IUB_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    Iub,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmTotalTimeIubLinkCongestedUl,
    pmNoOfDiscardedMsg from
    dc.DC_E_RBS_IUB_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_IUB_DAY as
  select * from dc.DC_E_RBS_IUB_DAY_01 union all
  select * from dc.DC_E_RBS_IUB_DAY_02 union all
  select * from dc.DC_E_RBS_IUB_DAY_03;

create view dcpublic.DC_E_RBS_IUB_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    Iub,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmTotalTimeIubLinkCongestedUl,
    pmNoOfDiscardedMsg from
    dc.DC_E_RBS_IUB_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_IUBDATASTREAMS_RAW as
  select * from dc.DC_E_RBS_IUBDATASTREAMS_RAW_01 union all
  select * from dc.DC_E_RBS_IUBDATASTREAMS_RAW_02 union all
  select * from dc.DC_E_RBS_IUBDATASTREAMS_RAW_03;

create view dcpublic.DC_E_RBS_IUBDATASTREAMS_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    Iub,
    IubDataStreams,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmCapAllocIubHsLimitingRatio,
    pmHsDataFramesLost,
    pmHsDataFramesReceived,
    pmDchFramesOutOfSequenceDl,
    pmRbsHsPdschCodePrio,
    pmDchFramesCrcMismatch,
    pmDchFramesLate,
    pmDchFramesTooLate,
    pmDchFramesReceived,
    pmNoUlIubLimitEul,
    pmEdchIubLimitingRatio,
    pmCapAllocIubHsLimitingRatioSpi00,
    pmCapAllocIubHsLimitingRatioSpi01,
    pmCapAllocIubHsLimitingRatioSpi02,
    pmCapAllocIubHsLimitingRatioSpi03,
    pmCapAllocIubHsLimitingRatioSpi04,
    pmCapAllocIubHsLimitingRatioSpi05,
    pmCapAllocIubHsLimitingRatioSpi06,
    pmCapAllocIubHsLimitingRatioSpi07,
    pmCapAllocIubHsLimitingRatioSpi08,
    pmCapAllocIubHsLimitingRatioSpi09,
    pmCapAllocIubHsLimitingRatioSpi10,
    pmCapAllocIubHsLimitingRatioSpi11,
    pmCapAllocIubHsLimitingRatioSpi12,
    pmCapAllocIubHsLimitingRatioSpi13,
    pmCapAllocIubHsLimitingRatioSpi14,
    pmCapAllocIubHsLimitingRatioSpi15,
    pmHsDataFramesLostSpi00,
    pmHsDataFramesLostSpi01,
    pmHsDataFramesLostSpi02,
    pmHsDataFramesLostSpi03,
    pmHsDataFramesLostSpi04,
    pmHsDataFramesLostSpi05,
    pmHsDataFramesLostSpi06,
    pmHsDataFramesLostSpi07,
    pmHsDataFramesLostSpi08,
    pmHsDataFramesLostSpi09,
    pmHsDataFramesLostSpi10,
    pmHsDataFramesLostSpi11,
    pmHsDataFramesLostSpi12,
    pmHsDataFramesLostSpi13,
    pmHsDataFramesLostSpi14,
    pmHsDataFramesLostSpi15,
    pmHsDataFramesReceivedSpi00,
    pmHsDataFramesReceivedSpi01,
    pmHsDataFramesReceivedSpi02,
    pmHsDataFramesReceivedSpi03,
    pmHsDataFramesReceivedSpi04,
    pmHsDataFramesReceivedSpi05,
    pmHsDataFramesReceivedSpi06,
    pmHsDataFramesReceivedSpi07,
    pmHsDataFramesReceivedSpi08,
    pmHsDataFramesReceivedSpi09,
    pmHsDataFramesReceivedSpi10,
    pmHsDataFramesReceivedSpi11,
    pmHsDataFramesReceivedSpi12,
    pmHsDataFramesReceivedSpi13,
    pmHsDataFramesReceivedSpi14,
    pmHsDataFramesReceivedSpi15 from
    dc.DC_E_RBS_IUBDATASTREAMS_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_IUBDATASTREAMS_DAY as
  select * from dc.DC_E_RBS_IUBDATASTREAMS_DAY_01 union all
  select * from dc.DC_E_RBS_IUBDATASTREAMS_DAY_02 union all
  select * from dc.DC_E_RBS_IUBDATASTREAMS_DAY_03;

create view dcpublic.DC_E_RBS_IUBDATASTREAMS_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    Iub,
    IubDataStreams,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmCapAllocIubHsLimitingRatio,
    pmHsDataFramesLost,
    pmHsDataFramesReceived,
    pmDchFramesOutOfSequenceDl,
    pmRbsHsPdschCodePrio,
    pmDchFramesCrcMismatch,
    pmDchFramesLate,
    pmDchFramesTooLate,
    pmDchFramesReceived,
    pmNoUlIubLimitEul,
    pmEdchIubLimitingRatio,
    pmCapAllocIubHsLimitingRatioSpi00,
    pmCapAllocIubHsLimitingRatioSpi01,
    pmCapAllocIubHsLimitingRatioSpi02,
    pmCapAllocIubHsLimitingRatioSpi03,
    pmCapAllocIubHsLimitingRatioSpi04,
    pmCapAllocIubHsLimitingRatioSpi05,
    pmCapAllocIubHsLimitingRatioSpi06,
    pmCapAllocIubHsLimitingRatioSpi07,
    pmCapAllocIubHsLimitingRatioSpi08,
    pmCapAllocIubHsLimitingRatioSpi09,
    pmCapAllocIubHsLimitingRatioSpi10,
    pmCapAllocIubHsLimitingRatioSpi11,
    pmCapAllocIubHsLimitingRatioSpi12,
    pmCapAllocIubHsLimitingRatioSpi13,
    pmCapAllocIubHsLimitingRatioSpi14,
    pmCapAllocIubHsLimitingRatioSpi15,
    pmHsDataFramesLostSpi00,
    pmHsDataFramesLostSpi01,
    pmHsDataFramesLostSpi02,
    pmHsDataFramesLostSpi03,
    pmHsDataFramesLostSpi04,
    pmHsDataFramesLostSpi05,
    pmHsDataFramesLostSpi06,
    pmHsDataFramesLostSpi07,
    pmHsDataFramesLostSpi08,
    pmHsDataFramesLostSpi09,
    pmHsDataFramesLostSpi10,
    pmHsDataFramesLostSpi11,
    pmHsDataFramesLostSpi12,
    pmHsDataFramesLostSpi13,
    pmHsDataFramesLostSpi14,
    pmHsDataFramesLostSpi15,
    pmHsDataFramesReceivedSpi00,
    pmHsDataFramesReceivedSpi01,
    pmHsDataFramesReceivedSpi02,
    pmHsDataFramesReceivedSpi03,
    pmHsDataFramesReceivedSpi04,
    pmHsDataFramesReceivedSpi05,
    pmHsDataFramesReceivedSpi06,
    pmHsDataFramesReceivedSpi07,
    pmHsDataFramesReceivedSpi08,
    pmHsDataFramesReceivedSpi09,
    pmHsDataFramesReceivedSpi10,
    pmHsDataFramesReceivedSpi11,
    pmHsDataFramesReceivedSpi12,
    pmHsDataFramesReceivedSpi13,
    pmHsDataFramesReceivedSpi14,
    pmHsDataFramesReceivedSpi15 from
    dc.DC_E_RBS_IUBDATASTREAMS_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_NBAPCOMMON_RAW as
  select * from dc.DC_E_RBS_NBAPCOMMON_RAW_01 union all
  select * from dc.DC_E_RBS_NBAPCOMMON_RAW_02 union all
  select * from dc.DC_E_RBS_NBAPCOMMON_RAW_03;

create view dcpublic.DC_E_RBS_NBAPCOMMON_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    Iub,
    NbapCommon,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfDiscardedMsg from
    dc.DC_E_RBS_NBAPCOMMON_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_NBAPCOMMON_DAY as
  select * from dc.DC_E_RBS_NBAPCOMMON_DAY_01 union all
  select * from dc.DC_E_RBS_NBAPCOMMON_DAY_02 union all
  select * from dc.DC_E_RBS_NBAPCOMMON_DAY_03;

create view dcpublic.DC_E_RBS_NBAPCOMMON_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    Iub,
    NbapCommon,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmNoOfDiscardedMsg from
    dc.DC_E_RBS_NBAPCOMMON_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_PRACH_RAW as
  select * from dc.DC_E_RBS_PRACH_RAW_01 union all
  select * from dc.DC_E_RBS_PRACH_RAW_02 union all
  select * from dc.DC_E_RBS_PRACH_RAW_03 union all
  select * from dc.DC_E_RBS_PRACH_RAW_04 union all
  select * from dc.DC_E_RBS_PRACH_RAW_05 union all
  select * from dc.DC_E_RBS_PRACH_RAW_06;

create view dcpublic.DC_E_RBS_PRACH_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    Prach,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoPreambleFalseDetection,
    pmSuccReceivedBlocks,
    pmUnsuccReceivedBlocks from
    dc.DC_E_RBS_PRACH_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_PRACH_DAY as
  select * from dc.DC_E_RBS_PRACH_DAY_01 union all
  select * from dc.DC_E_RBS_PRACH_DAY_02 union all
  select * from dc.DC_E_RBS_PRACH_DAY_03 union all
  select * from dc.DC_E_RBS_PRACH_DAY_04 union all
  select * from dc.DC_E_RBS_PRACH_DAY_05 union all
  select * from dc.DC_E_RBS_PRACH_DAY_06;

create view dcpublic.DC_E_RBS_PRACH_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    Prach,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmNoPreambleFalseDetection,
    pmSuccReceivedBlocks,
    pmUnsuccReceivedBlocks from
    dc.DC_E_RBS_PRACH_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_PRACH_DAYBH as
  select * from dc.DC_E_RBS_PRACH_DAYBH_01 union all
  select * from dc.DC_E_RBS_PRACH_DAYBH_02 union all
  select * from dc.DC_E_RBS_PRACH_DAYBH_03 union all
  select * from dc.DC_E_RBS_PRACH_DAYBH_04 union all
  select * from dc.DC_E_RBS_PRACH_DAYBH_05 union all
  select * from dc.DC_E_RBS_PRACH_DAYBH_06;

create view dcpublic.DC_E_RBS_PRACH_DAYBH
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    Prach,
    DATE_ID,
    MIN_ID,
    BHTYPE,
    BUSYHOUR,
    BHCLASS,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmNoPreambleFalseDetection,
    pmSuccReceivedBlocks,
    pmUnsuccReceivedBlocks from
    dc.DC_E_RBS_PRACH_DAYBH where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_RADIOLINKS_RAW as
  select * from dc.DC_E_RBS_RADIOLINKS_RAW_01 union all
  select * from dc.DC_E_RBS_RADIOLINKS_RAW_02 union all
  select * from dc.DC_E_RBS_RADIOLINKS_RAW_03 union all
  select * from dc.DC_E_RBS_RADIOLINKS_RAW_04 union all
  select * from dc.DC_E_RBS_RADIOLINKS_RAW_05 union all
  select * from dc.DC_E_RBS_RADIOLINKS_RAW_06;

create view dcpublic.DC_E_RBS_RADIOLINKS_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    RadioLinks,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmRLSSupSynchToUnsynch,
    pmRLSSupWaitToOutOfSynch from
    dc.DC_E_RBS_RADIOLINKS_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_RADIOLINKS_DAY as
  select * from dc.DC_E_RBS_RADIOLINKS_DAY_01 union all
  select * from dc.DC_E_RBS_RADIOLINKS_DAY_02 union all
  select * from dc.DC_E_RBS_RADIOLINKS_DAY_03 union all
  select * from dc.DC_E_RBS_RADIOLINKS_DAY_04 union all
  select * from dc.DC_E_RBS_RADIOLINKS_DAY_05 union all
  select * from dc.DC_E_RBS_RADIOLINKS_DAY_06;

create view dcpublic.DC_E_RBS_RADIOLINKS_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    RadioLinks,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmRLSSupSynchToUnsynch,
    pmRLSSupWaitToOutOfSynch from
    dc.DC_E_RBS_RADIOLINKS_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_RADIOLINKS_DAYBH as
  select * from dc.DC_E_RBS_RADIOLINKS_DAYBH_01 union all
  select * from dc.DC_E_RBS_RADIOLINKS_DAYBH_02 union all
  select * from dc.DC_E_RBS_RADIOLINKS_DAYBH_03 union all
  select * from dc.DC_E_RBS_RADIOLINKS_DAYBH_04 union all
  select * from dc.DC_E_RBS_RADIOLINKS_DAYBH_05 union all
  select * from dc.DC_E_RBS_RADIOLINKS_DAYBH_06;

create view dcpublic.DC_E_RBS_RADIOLINKS_DAYBH
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    RadioLinks,
    DATE_ID,
    MIN_ID,
    BHTYPE,
    BUSYHOUR,
    BHCLASS,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmRLSSupSynchToUnsynch,
    pmRLSSupWaitToOutOfSynch from
    dc.DC_E_RBS_RADIOLINKS_DAYBH where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_RADIOLINKS_V_RAW as
  select * from dc.DC_E_RBS_RADIOLINKS_V_RAW_01 union all
  select * from dc.DC_E_RBS_RADIOLINKS_V_RAW_02 union all
  select * from dc.DC_E_RBS_RADIOLINKS_V_RAW_03 union all
  select * from dc.DC_E_RBS_RADIOLINKS_V_RAW_04 union all
  select * from dc.DC_E_RBS_RADIOLINKS_V_RAW_05 union all
  select * from dc.DC_E_RBS_RADIOLINKS_V_RAW_06 union all
  select * from dc.DC_E_RBS_RADIOLINKS_V_RAW_07 union all
  select * from dc.DC_E_RBS_RADIOLINKS_V_RAW_08 union all
  select * from dc.DC_E_RBS_RADIOLINKS_V_RAW_09;

create view dcpublic.DC_E_RBS_RADIOLINKS_V_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    RadioLinks,
    DCVECTOR_INDEX,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmAverageSir,
    pmAverageSirError,
    pmDpcchBer,
    pmDpchCodePowerSf128,
    pmDpchCodePowerSf16,
    pmDpchCodePowerSf256,
    pmDpchCodePowerSf32,
    pmDpchCodePowerSf4,
    pmDpchCodePowerSf64,
    pmDpchCodePowerSf8,
    pmDpdchBer,
    pmOutOfSynch,
    pmUlSynchTime,
    pmUlSynchTimeSHO,
    pmBranchDeltaSir from
    dc.DC_E_RBS_RADIOLINKS_V_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_RADIOLINKS_V_DAY as
  select * from dc.DC_E_RBS_RADIOLINKS_V_DAY_01 union all
  select * from dc.DC_E_RBS_RADIOLINKS_V_DAY_02 union all
  select * from dc.DC_E_RBS_RADIOLINKS_V_DAY_03 union all
  select * from dc.DC_E_RBS_RADIOLINKS_V_DAY_04;

create view dcpublic.DC_E_RBS_RADIOLINKS_V_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    RadioLinks,
    DCVECTOR_INDEX,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmAverageSir,
    pmAverageSirError,
    pmDpcchBer,
    pmDpchCodePowerSf128,
    pmDpchCodePowerSf16,
    pmDpchCodePowerSf256,
    pmDpchCodePowerSf32,
    pmDpchCodePowerSf4,
    pmDpchCodePowerSf64,
    pmDpchCodePowerSf8,
    pmDpdchBer,
    pmOutOfSynch,
    pmUlSynchTime,
    pmUlSynchTimeSHO,
    pmBranchDeltaSir from
    dc.DC_E_RBS_RADIOLINKS_V_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_RADIOLINKS_V_DAYBH as
  select * from dc.DC_E_RBS_RADIOLINKS_V_DAYBH_01 union all
  select * from dc.DC_E_RBS_RADIOLINKS_V_DAYBH_02 union all
  select * from dc.DC_E_RBS_RADIOLINKS_V_DAYBH_03 union all
  select * from dc.DC_E_RBS_RADIOLINKS_V_DAYBH_04;

create view dcpublic.DC_E_IMS_PTT_PTTCSS_DAY
  as select OSS_ID,
    SN,
    IMS_DC,
    g3SubNetwork,
    g3ManagedElement,
    MOID,
    PTT_CSS,
    Source,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pocusAutoAvailable,
    pocusManualAvailable,
    cssUsers,
    csServiceAdhoc,
    csServiceChatGroup,
    csServicePrearranged,
    ipaMessages,
    pocusPublish,
    csServiceSubscribeToParticipantInfo,
    csServiceUndetermined,
    pocusISBUnavailable,
    pttUsers,
    cssAddUserToSessionAttempts,
    cssAdhocAttempts,
    cssChatAttempts,
    cssRejoinAttempts,
    cssMMInviteAttempts,
    cssOneToOneAttempts,
    cssPrearrangedAttempts,
    pocusInitialPublishAttempts,
    gaMessageAttempts,
    ipaMessagesAttempts,
    pocusPublishRefreshAttempts,
    pocusPublishAttempts,
    cssSubscribeToParticipantInfoAttempts,
    cssUndeterminedAttempts from
    dc.DC_E_IMS_PTT_PTTCSS_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dcpublic.DC_E_IMS_PTT_PTTPOCXDMS_DAY
  as select OSS_ID,
    SN,
    IMS_DC,
    g3SubNetwork,
    g3ManagedElement,
    MOID,
    PTT_POCXDMS,
    Source,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pocxdmsGroups,
    pocxdmsXcapRequestsDelete,
    pocxdmsXcapRequestsGet,
    pocxdmsXcapRequestsPut,
    pocxdmsXcapRequestsDeleteAttempts,
    pocxdmsXcapRequestsGetAttempts,
    pocxdmsXcapRequestsPutAttempts from
    dc.DC_E_IMS_PTT_PTTPOCXDMS_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dcpublic.DC_E_RBS_RADIOLINKS_V_DAYBH
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    RadioLinks,
    DCVECTOR_INDEX,
    DATE_ID,
    MIN_ID,
    BHTYPE,
    BUSYHOUR,
    BHCLASS,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmAverageSir,
    pmAverageSirError,
    pmDpcchBer,
    pmDpchCodePowerSf128,
    pmDpchCodePowerSf16,
    pmDpchCodePowerSf256,
    pmDpchCodePowerSf32,
    pmDpchCodePowerSf4,
    pmDpchCodePowerSf64,
    pmDpchCodePowerSf8,
    pmDpdchBer,
    pmOutOfSynch,
    pmUlSynchTime,
    pmUlSynchTimeSHO,
    pmBranchDeltaSir from
    dc.DC_E_RBS_RADIOLINKS_V_DAYBH where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_SCCPCH_RAW as
  select * from dc.DC_E_RBS_SCCPCH_RAW_01 union all
  select * from dc.DC_E_RBS_SCCPCH_RAW_02 union all
  select * from dc.DC_E_RBS_SCCPCH_RAW_03 union all
  select * from dc.DC_E_RBS_SCCPCH_RAW_04 union all
  select * from dc.DC_E_RBS_SCCPCH_RAW_05 union all
  select * from dc.DC_E_RBS_SCCPCH_RAW_06;

create view dcpublic.DC_E_RBS_SCCPCH_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    Sccpch,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfTfc1OnFach1,
    pmNoOfTfc2OnFach1,
    pmNoOfTfc3OnFach2,
    pmMbmsSccpchTransmittedTfc from
    dc.DC_E_RBS_SCCPCH_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_SCCPCH_DAY as
  select * from dc.DC_E_RBS_SCCPCH_DAY_01 union all
  select * from dc.DC_E_RBS_SCCPCH_DAY_02 union all
  select * from dc.DC_E_RBS_SCCPCH_DAY_03 union all
  select * from dc.DC_E_RBS_SCCPCH_DAY_04 union all
  select * from dc.DC_E_RBS_SCCPCH_DAY_05 union all
  select * from dc.DC_E_RBS_SCCPCH_DAY_06;

create view dcpublic.DC_E_RBS_SCCPCH_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    Sccpch,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmNoOfTfc1OnFach1,
    pmNoOfTfc2OnFach1,
    pmNoOfTfc3OnFach2,
    pmMbmsSccpchTransmittedTfc from
    dc.DC_E_RBS_SCCPCH_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_SCCPCH_DAYBH as
  select * from dc.DC_E_RBS_SCCPCH_DAYBH_01 union all
  select * from dc.DC_E_RBS_SCCPCH_DAYBH_02 union all
  select * from dc.DC_E_RBS_SCCPCH_DAYBH_03 union all
  select * from dc.DC_E_RBS_SCCPCH_DAYBH_04 union all
  select * from dc.DC_E_RBS_SCCPCH_DAYBH_05 union all
  select * from dc.DC_E_RBS_SCCPCH_DAYBH_06;

create view dcpublic.DC_E_RBS_SCCPCH_DAYBH
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    Sccpch,
    DATE_ID,
    MIN_ID,
    BHTYPE,
    BUSYHOUR,
    BHCLASS,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmNoOfTfc1OnFach1,
    pmNoOfTfc2OnFach1,
    pmNoOfTfc3OnFach2,
    pmMbmsSccpchTransmittedTfc from
    dc.DC_E_RBS_SCCPCH_DAYBH where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_ULBASEBANDPOOL_RAW as
  select * from dc.DC_E_RBS_ULBASEBANDPOOL_RAW_01 union all
  select * from dc.DC_E_RBS_ULBASEBANDPOOL_RAW_02 union all
  select * from dc.DC_E_RBS_ULBASEBANDPOOL_RAW_03;

create view dcpublic.DC_E_RBS_ULBASEBANDPOOL_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    Equipment,
    Subrack,
    UplinkBaseBandPool,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmApomcOfRakeRecUsed,
    pmApomcOfUlLinkCap,
    pmApomcOfRachCap,
    pmNoOfIbho,
    pmNoUlHwLimitEul,
    pmSetupAttemptsSf128,
    pmSetupAttemptsSf16,
    pmSetupAttemptsSf256,
    pmSetupAttemptsSf32,
    pmSetupAttemptsSf4,
    pmSetupAttemptsSf64,
    pmSetupAttemptsSf8,
    pmSetupFailuresSf128,
    pmSetupFailuresSf16,
    pmSetupFailuresSf256,
    pmSetupFailuresSf32,
    pmSetupFailuresSf4,
    pmSetupFailuresSf64,
    pmSetupFailuresSf8,
    pmCapacityAllocAttUlCe,
    pmCapacityAllocRejUlCe,
    pmSamplesCapacityUlCe,
    pmSumCapacityUlCe,
    pmSumSqrCapacityUlCe,
    pmCapacityUlCe from
    dc.DC_E_RBS_ULBASEBANDPOOL_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_ULBASEBANDPOOL_DAY as
  select * from dc.DC_E_RBS_ULBASEBANDPOOL_DAY_01 union all
  select * from dc.DC_E_RBS_ULBASEBANDPOOL_DAY_02 union all
  select * from dc.DC_E_RBS_ULBASEBANDPOOL_DAY_03;

create view dcpublic.DC_E_RBS_ULBASEBANDPOOL_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    Equipment,
    Subrack,
    UplinkBaseBandPool,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmApomcOfRakeRecUsed,
    pmApomcOfUlLinkCap,
    pmApomcOfRachCap,
    pmNoOfIbho,
    pmNoUlHwLimitEul,
    pmSetupAttemptsSf128,
    pmSetupAttemptsSf16,
    pmSetupAttemptsSf256,
    pmSetupAttemptsSf32,
    pmSetupAttemptsSf4,
    pmSetupAttemptsSf64,
    pmSetupAttemptsSf8,
    pmSetupFailuresSf128,
    pmSetupFailuresSf16,
    pmSetupFailuresSf256,
    pmSetupFailuresSf32,
    pmSetupFailuresSf4,
    pmSetupFailuresSf64,
    pmSetupFailuresSf8,
    pmCapacityAllocAttUlCe,
    pmCapacityAllocRejUlCe,
    pmSamplesCapacityUlCe,
    pmSumCapacityUlCe,
    pmSumSqrCapacityUlCe,
    pmCapacityUlCe from
    dc.DC_E_RBS_ULBASEBANDPOOL_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_CARRIER_V_RAW as
  select * from dc.DC_E_RBS_CARRIER_V_RAW_01 union all
  select * from dc.DC_E_RBS_CARRIER_V_RAW_02 union all
  select * from dc.DC_E_RBS_CARRIER_V_RAW_03 union all
  select * from dc.DC_E_RBS_CARRIER_V_RAW_04 union all
  select * from dc.DC_E_RBS_CARRIER_V_RAW_05 union all
  select * from dc.DC_E_RBS_CARRIER_V_RAW_06 union all
  select * from dc.DC_E_RBS_CARRIER_V_RAW_07 union all
  select * from dc.DC_E_RBS_CARRIER_V_RAW_08 union all
  select * from dc.DC_E_RBS_CARRIER_V_RAW_09;

create view dcpublic.DC_E_RBS_CARRIER_V_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    DCVECTOR_INDEX,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmAverageRssi,
    pmTransmittedCarrierPower from
    dc.DC_E_RBS_CARRIER_V_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_CARRIER_V_DAY as
  select * from dc.DC_E_RBS_CARRIER_V_DAY_01 union all
  select * from dc.DC_E_RBS_CARRIER_V_DAY_02 union all
  select * from dc.DC_E_RBS_CARRIER_V_DAY_03 union all
  select * from dc.DC_E_RBS_CARRIER_V_DAY_04;

create view dcpublic.DC_E_RBS_CARRIER_V_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    DCVECTOR_INDEX,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmAverageRssi,
    pmTransmittedCarrierPower from
    dc.DC_E_RBS_CARRIER_V_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_CARRIER_V_DAYBH as
  select * from dc.DC_E_RBS_CARRIER_V_DAYBH_01 union all
  select * from dc.DC_E_RBS_CARRIER_V_DAYBH_02 union all
  select * from dc.DC_E_RBS_CARRIER_V_DAYBH_03 union all
  select * from dc.DC_E_RBS_CARRIER_V_DAYBH_04;

create view dcpublic.DC_E_RBS_CARRIER_V_DAYBH
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    DCVECTOR_INDEX,
    DATE_ID,
    MIN_ID,
    BHTYPE,
    BUSYHOUR,
    BHCLASS,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmAverageRssi,
    pmTransmittedCarrierPower from
    dc.DC_E_RBS_CARRIER_V_DAYBH where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_DLBASEBANDPOOL_V_RAW as
  select * from dc.DC_E_RBS_DLBASEBANDPOOL_V_RAW_01 union all
  select * from dc.DC_E_RBS_DLBASEBANDPOOL_V_RAW_02 union all
  select * from dc.DC_E_RBS_DLBASEBANDPOOL_V_RAW_03 union all
  select * from dc.DC_E_RBS_DLBASEBANDPOOL_V_RAW_04 union all
  select * from dc.DC_E_RBS_DLBASEBANDPOOL_V_RAW_05 union all
  select * from dc.DC_E_RBS_DLBASEBANDPOOL_V_RAW_06 union all
  select * from dc.DC_E_RBS_DLBASEBANDPOOL_V_RAW_07 union all
  select * from dc.DC_E_RBS_DLBASEBANDPOOL_V_RAW_08 union all
  select * from dc.DC_E_RBS_DLBASEBANDPOOL_V_RAW_09;

create view dcpublic.DC_E_RBS_DLBASEBANDPOOL_V_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    Equipment,
    Subrack,
    DownlinkBaseBandPool,
    DCVECTOR_INDEX,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfRadioLinksSf128,
    pmNoOfRadioLinksSf16,
    pmNoOfRadioLinksSf256,
    pmNoOfRadioLinksSf32,
    pmNoOfRadioLinksSf4,
    pmNoOfRadioLinksSf64,
    pmNoOfRadioLinksSf8,
    pmUsedADch from
    dc.DC_E_RBS_DLBASEBANDPOOL_V_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_DLBASEBANDPOOL_V_DAY as
  select * from dc.DC_E_RBS_DLBASEBANDPOOL_V_DAY_01 union all
  select * from dc.DC_E_RBS_DLBASEBANDPOOL_V_DAY_02 union all
  select * from dc.DC_E_RBS_DLBASEBANDPOOL_V_DAY_03 union all
  select * from dc.DC_E_RBS_DLBASEBANDPOOL_V_DAY_04;

create view dcpublic.DC_E_RBS_DLBASEBANDPOOL_V_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    Equipment,
    Subrack,
    DownlinkBaseBandPool,
    DCVECTOR_INDEX,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmNoOfRadioLinksSf128,
    pmNoOfRadioLinksSf16,
    pmNoOfRadioLinksSf256,
    pmNoOfRadioLinksSf32,
    pmNoOfRadioLinksSf4,
    pmNoOfRadioLinksSf64,
    pmNoOfRadioLinksSf8,
    pmUsedADch from
    dc.DC_E_RBS_DLBASEBANDPOOL_V_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_EDCHRESOURCES_V_RAW as
  select * from dc.DC_E_RBS_EDCHRESOURCES_V_RAW_01 union all
  select * from dc.DC_E_RBS_EDCHRESOURCES_V_RAW_02 union all
  select * from dc.DC_E_RBS_EDCHRESOURCES_V_RAW_03 union all
  select * from dc.DC_E_RBS_EDCHRESOURCES_V_RAW_04 union all
  select * from dc.DC_E_RBS_EDCHRESOURCES_V_RAW_05 union all
  select * from dc.DC_E_RBS_EDCHRESOURCES_V_RAW_06 union all
  select * from dc.DC_E_RBS_EDCHRESOURCES_V_RAW_07 union all
  select * from dc.DC_E_RBS_EDCHRESOURCES_V_RAW_08 union all
  select * from dc.DC_E_RBS_EDCHRESOURCES_V_RAW_09;

create view dcpublic.DC_E_RBS_EDCHRESOURCES_V_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    EDchResources,
    DCVECTOR_INDEX,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmTotRateGrantedEul,
    pmWaitingTimeEul,
    pmTotalRotCoverage,
    pmOwnUuLoad,
    pmNoiseFloor,
    pmCommonChPowerEul,
    pmNoSchEdchEul,
    pmLEDchTot,
    pmLMaxEDch,
    pmCapacityServEDchUsers from
    dc.DC_E_RBS_EDCHRESOURCES_V_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_EDCHRESOURCES_V_DAY as
  select * from dc.DC_E_RBS_EDCHRESOURCES_V_DAY_01 union all
  select * from dc.DC_E_RBS_EDCHRESOURCES_V_DAY_02 union all
  select * from dc.DC_E_RBS_EDCHRESOURCES_V_DAY_03 union all
  select * from dc.DC_E_RBS_EDCHRESOURCES_V_DAY_04;

create view dcpublic.DC_E_RBS_EDCHRESOURCES_V_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    EDchResources,
    DCVECTOR_INDEX,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmTotRateGrantedEul,
    pmWaitingTimeEul,
    pmTotalRotCoverage,
    pmOwnUuLoad,
    pmNoiseFloor,
    pmCommonChPowerEul,
    pmNoSchEdchEul,
    pmLEDchTot,
    pmLMaxEDch,
    pmCapacityServEDchUsers from
    dc.DC_E_RBS_EDCHRESOURCES_V_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_EDCHRESOURCES_V_DAYBH as
  select * from dc.DC_E_RBS_EDCHRESOURCES_V_DAYBH_01 union all
  select * from dc.DC_E_RBS_EDCHRESOURCES_V_DAYBH_02 union all
  select * from dc.DC_E_RBS_EDCHRESOURCES_V_DAYBH_03 union all
  select * from dc.DC_E_RBS_EDCHRESOURCES_V_DAYBH_04;

create view dcpublic.DC_E_RBS_EDCHRESOURCES_V_DAYBH
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    EDchResources,
    DCVECTOR_INDEX,
    DATE_ID,
    MIN_ID,
    BHTYPE,
    BUSYHOUR,
    BHCLASS,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmTotRateGrantedEul,
    pmWaitingTimeEul,
    pmTotalRotCoverage,
    pmOwnUuLoad,
    pmNoiseFloor,
    pmCommonChPowerEul,
    pmNoSchEdchEul,
    pmLEDchTot,
    pmLMaxEDch,
    pmCapacityServEDchUsers from
    dc.DC_E_RBS_EDCHRESOURCES_V_DAYBH where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_HSDSCHRES_V_RAW as
  select * from dc.DC_E_RBS_HSDSCHRES_V_RAW_01 union all
  select * from dc.DC_E_RBS_HSDSCHRES_V_RAW_02 union all
  select * from dc.DC_E_RBS_HSDSCHRES_V_RAW_03 union all
  select * from dc.DC_E_RBS_HSDSCHRES_V_RAW_04 union all
  select * from dc.DC_E_RBS_HSDSCHRES_V_RAW_05 union all
  select * from dc.DC_E_RBS_HSDSCHRES_V_RAW_06 union all
  select * from dc.DC_E_RBS_HSDSCHRES_V_RAW_07 union all
  select * from dc.DC_E_RBS_HSDSCHRES_V_RAW_08 union all
  select * from dc.DC_E_RBS_HSDSCHRES_V_RAW_09;

create view dcpublic.DC_E_RBS_HSDSCHRES_V_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    HsDschResources,
    DCVECTOR_INDEX,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmAverageUserRate,
    pmReportedCqi,
    pmTransCarrierPwrNonHs,
    pmUsedCqi,
    pmSumOfHsScchUsedPwr,
    pmNoOfHsUsersPerTti,
    pmRemainingResourceCheck,
    pmUsedTbs16Qam,
    pmAck16Qam,
    pmUsedTbsQpsk,
    pmAckQpsk,
    pmDelayDistributionSpi00,
    pmDelayDistributionSpi01,
    pmDelayDistributionSpi02,
    pmDelayDistributionSpi03,
    pmDelayDistributionSpi04,
    pmDelayDistributionSpi05,
    pmDelayDistributionSpi06,
    pmDelayDistributionSpi07,
    pmDelayDistributionSpi08,
    pmDelayDistributionSpi09,
    pmDelayDistributionSpi10,
    pmDelayDistributionSpi11,
    pmDelayDistributionSpi12,
    pmDelayDistributionSpi13,
    pmDelayDistributionSpi14,
    pmDelayDistributionSpi15,
    pmTransmittedCarrierPowerHs,
    pmAck64Qam,
    pmReportedCqi64Qam,
    pmReportedCqiMimoDs1,
    pmReportedCqiMimoDs2,
    pmReportedCqiMimoSs,
    pmUsedHsPdschCodes,
    pmUsedTbs64Qam,
    pmCapacityHsDschUsers,
    pmCapacityHsPdschCodes from
    dc.DC_E_RBS_HSDSCHRES_V_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_HSDSCHRES_V_DAY as
  select * from dc.DC_E_RBS_HSDSCHRES_V_DAY_01 union all
  select * from dc.DC_E_RBS_HSDSCHRES_V_DAY_02 union all
  select * from dc.DC_E_RBS_HSDSCHRES_V_DAY_03 union all
  select * from dc.DC_E_RBS_HSDSCHRES_V_DAY_04;

create view dcpublic.DC_E_RBS_HSDSCHRES_V_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    HsDschResources,
    DCVECTOR_INDEX,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmAverageUserRate,
    pmReportedCqi,
    pmTransCarrierPwrNonHs,
    pmUsedCqi,
    pmSumOfHsScchUsedPwr,
    pmNoOfHsUsersPerTti,
    pmRemainingResourceCheck,
    pmUsedTbs16Qam,
    pmAck16Qam,
    pmUsedTbsQpsk,
    pmAckQpsk,
    pmDelayDistributionSpi00,
    pmDelayDistributionSpi01,
    pmDelayDistributionSpi02,
    pmDelayDistributionSpi03,
    pmDelayDistributionSpi04,
    pmDelayDistributionSpi05,
    pmDelayDistributionSpi06,
    pmDelayDistributionSpi07,
    pmDelayDistributionSpi08,
    pmDelayDistributionSpi09,
    pmDelayDistributionSpi10,
    pmDelayDistributionSpi11,
    pmDelayDistributionSpi12,
    pmDelayDistributionSpi13,
    pmDelayDistributionSpi14,
    pmDelayDistributionSpi15,
    pmTransmittedCarrierPowerHs,
    pmAck64Qam,
    pmReportedCqi64Qam,
    pmReportedCqiMimoDs1,
    pmReportedCqiMimoDs2,
    pmReportedCqiMimoSs,
    pmUsedHsPdschCodes,
    pmUsedTbs64Qam,
    pmCapacityHsDschUsers,
    pmCapacityHsPdschCodes from
    dc.DC_E_RBS_HSDSCHRES_V_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_HSDSCHRES_V_DAYBH as
  select * from dc.DC_E_RBS_HSDSCHRES_V_DAYBH_01 union all
  select * from dc.DC_E_RBS_HSDSCHRES_V_DAYBH_02 union all
  select * from dc.DC_E_RBS_HSDSCHRES_V_DAYBH_03 union all
  select * from dc.DC_E_RBS_HSDSCHRES_V_DAYBH_04;

create view dcpublic.DC_E_RBS_HSDSCHRES_V_DAYBH
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    HsDschResources,
    DCVECTOR_INDEX,
    DATE_ID,
    MIN_ID,
    BHTYPE,
    BUSYHOUR,
    BHCLASS,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmAverageUserRate,
    pmReportedCqi,
    pmTransCarrierPwrNonHs,
    pmUsedCqi,
    pmSumOfHsScchUsedPwr,
    pmNoOfHsUsersPerTti,
    pmRemainingResourceCheck,
    pmUsedTbs16Qam,
    pmAck16Qam,
    pmUsedTbsQpsk,
    pmAckQpsk,
    pmDelayDistributionSpi00,
    pmDelayDistributionSpi01,
    pmDelayDistributionSpi02,
    pmDelayDistributionSpi03,
    pmDelayDistributionSpi04,
    pmDelayDistributionSpi05,
    pmDelayDistributionSpi06,
    pmDelayDistributionSpi07,
    pmDelayDistributionSpi08,
    pmDelayDistributionSpi09,
    pmDelayDistributionSpi10,
    pmDelayDistributionSpi11,
    pmDelayDistributionSpi12,
    pmDelayDistributionSpi13,
    pmDelayDistributionSpi14,
    pmDelayDistributionSpi15,
    pmTransmittedCarrierPowerHs,
    pmAck64Qam,
    pmReportedCqi64Qam,
    pmReportedCqiMimoDs1,
    pmReportedCqiMimoDs2,
    pmReportedCqiMimoSs,
    pmUsedHsPdschCodes,
    pmUsedTbs64Qam,
    pmCapacityHsDschUsers,
    pmCapacityHsPdschCodes from
    dc.DC_E_RBS_HSDSCHRES_V_DAYBH where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_IUBDATASTREAMS_V_RAW as
  select * from dc.DC_E_RBS_IUBDATASTREAMS_V_RAW_01 union all
  select * from dc.DC_E_RBS_IUBDATASTREAMS_V_RAW_02 union all
  select * from dc.DC_E_RBS_IUBDATASTREAMS_V_RAW_03 union all
  select * from dc.DC_E_RBS_IUBDATASTREAMS_V_RAW_04 union all
  select * from dc.DC_E_RBS_IUBDATASTREAMS_V_RAW_05 union all
  select * from dc.DC_E_RBS_IUBDATASTREAMS_V_RAW_06 union all
  select * from dc.DC_E_RBS_IUBDATASTREAMS_V_RAW_07 union all
  select * from dc.DC_E_RBS_IUBDATASTREAMS_V_RAW_08 union all
  select * from dc.DC_E_RBS_IUBDATASTREAMS_V_RAW_09;

create view dcpublic.DC_E_RBS_IUBDATASTREAMS_V_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    Iub,
    IubDataStreams,
    DCVECTOR_INDEX,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmHsDataFrameDelayIub,
    pmIubMacdPduRbsReceivedBits,
    pmTargetHsRate,
    pmHsDataFrameDelayIubSpi00,
    pmHsDataFrameDelayIubSpi01,
    pmHsDataFrameDelayIubSpi02,
    pmHsDataFrameDelayIubSpi03,
    pmHsDataFrameDelayIubSpi04,
    pmHsDataFrameDelayIubSpi05,
    pmHsDataFrameDelayIubSpi06,
    pmHsDataFrameDelayIubSpi07,
    pmHsDataFrameDelayIubSpi08,
    pmHsDataFrameDelayIubSpi09,
    pmHsDataFrameDelayIubSpi10,
    pmHsDataFrameDelayIubSpi11,
    pmHsDataFrameDelayIubSpi12,
    pmHsDataFrameDelayIubSpi13,
    pmHsDataFrameDelayIubSpi14,
    pmHsDataFrameDelayIubSpi15 from
    dc.DC_E_RBS_IUBDATASTREAMS_V_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_IUBDATASTREAMS_V_DAY as
  select * from dc.DC_E_RBS_IUBDATASTREAMS_V_DAY_01 union all
  select * from dc.DC_E_RBS_IUBDATASTREAMS_V_DAY_02 union all
  select * from dc.DC_E_RBS_IUBDATASTREAMS_V_DAY_03 union all
  select * from dc.DC_E_RBS_IUBDATASTREAMS_V_DAY_04;

create view dcpublic.DC_E_RBS_IUBDATASTREAMS_V_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    Iub,
    IubDataStreams,
    DCVECTOR_INDEX,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmHsDataFrameDelayIub,
    pmIubMacdPduRbsReceivedBits,
    pmTargetHsRate,
    pmHsDataFrameDelayIubSpi00,
    pmHsDataFrameDelayIubSpi01,
    pmHsDataFrameDelayIubSpi02,
    pmHsDataFrameDelayIubSpi03,
    pmHsDataFrameDelayIubSpi04,
    pmHsDataFrameDelayIubSpi05,
    pmHsDataFrameDelayIubSpi06,
    pmHsDataFrameDelayIubSpi07,
    pmHsDataFrameDelayIubSpi08,
    pmHsDataFrameDelayIubSpi09,
    pmHsDataFrameDelayIubSpi10,
    pmHsDataFrameDelayIubSpi11,
    pmHsDataFrameDelayIubSpi12,
    pmHsDataFrameDelayIubSpi13,
    pmHsDataFrameDelayIubSpi14,
    pmHsDataFrameDelayIubSpi15 from
    dc.DC_E_RBS_IUBDATASTREAMS_V_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_PRACH_V_RAW as
  select * from dc.DC_E_RBS_PRACH_V_RAW_01 union all
  select * from dc.DC_E_RBS_PRACH_V_RAW_02 union all
  select * from dc.DC_E_RBS_PRACH_V_RAW_03 union all
  select * from dc.DC_E_RBS_PRACH_V_RAW_04 union all
  select * from dc.DC_E_RBS_PRACH_V_RAW_05 union all
  select * from dc.DC_E_RBS_PRACH_V_RAW_06 union all
  select * from dc.DC_E_RBS_PRACH_V_RAW_07 union all
  select * from dc.DC_E_RBS_PRACH_V_RAW_08 union all
  select * from dc.DC_E_RBS_PRACH_V_RAW_09;

create view dcpublic.DC_E_RBS_PRACH_V_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    Prach,
    DCVECTOR_INDEX,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmReceivedPreambleSir,
    pmPropagationDelay from
    dc.DC_E_RBS_PRACH_V_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_PRACH_V_DAY as
  select * from dc.DC_E_RBS_PRACH_V_DAY_01 union all
  select * from dc.DC_E_RBS_PRACH_V_DAY_02 union all
  select * from dc.DC_E_RBS_PRACH_V_DAY_03 union all
  select * from dc.DC_E_RBS_PRACH_V_DAY_04;

create view dcpublic.DC_E_RBS_PRACH_V_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    Prach,
    DCVECTOR_INDEX,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmReceivedPreambleSir,
    pmPropagationDelay from
    dc.DC_E_RBS_PRACH_V_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_PRACH_V_DAYBH as
  select * from dc.DC_E_RBS_PRACH_V_DAYBH_01 union all
  select * from dc.DC_E_RBS_PRACH_V_DAYBH_02 union all
  select * from dc.DC_E_RBS_PRACH_V_DAYBH_03 union all
  select * from dc.DC_E_RBS_PRACH_V_DAYBH_04;

create view dcpublic.DC_E_RBS_PRACH_V_DAYBH
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    Prach,
    DCVECTOR_INDEX,
    DATE_ID,
    MIN_ID,
    BHTYPE,
    BUSYHOUR,
    BHCLASS,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmReceivedPreambleSir,
    pmPropagationDelay from
    dc.DC_E_RBS_PRACH_V_DAYBH where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_ULBASEBANDPOOL_V_RAW as
  select * from dc.DC_E_RBS_ULBASEBANDPOOL_V_RAW_01 union all
  select * from dc.DC_E_RBS_ULBASEBANDPOOL_V_RAW_02 union all
  select * from dc.DC_E_RBS_ULBASEBANDPOOL_V_RAW_03 union all
  select * from dc.DC_E_RBS_ULBASEBANDPOOL_V_RAW_04 union all
  select * from dc.DC_E_RBS_ULBASEBANDPOOL_V_RAW_05 union all
  select * from dc.DC_E_RBS_ULBASEBANDPOOL_V_RAW_06 union all
  select * from dc.DC_E_RBS_ULBASEBANDPOOL_V_RAW_07 union all
  select * from dc.DC_E_RBS_ULBASEBANDPOOL_V_RAW_08 union all
  select * from dc.DC_E_RBS_ULBASEBANDPOOL_V_RAW_09;

create view dcpublic.DC_E_RBS_ULBASEBANDPOOL_V_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    Equipment,
    Subrack,
    UplinkBaseBandPool,
    DCVECTOR_INDEX,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfRadioLinksSf128,
    pmNoOfRadioLinksSf16,
    pmNoOfRadioLinksSf256,
    pmNoOfRadioLinksSf32,
    pmNoOfRadioLinksSf4,
    pmNoOfRadioLinksSf64,
    pmNoOfRadioLinksSf8,
    pmHwCePoolEul,
    pmCapacityUlCe from
    dc.DC_E_RBS_ULBASEBANDPOOL_V_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_ULBASEBANDPOOL_V_DAY as
  select * from dc.DC_E_RBS_ULBASEBANDPOOL_V_DAY_01 union all
  select * from dc.DC_E_RBS_ULBASEBANDPOOL_V_DAY_02 union all
  select * from dc.DC_E_RBS_ULBASEBANDPOOL_V_DAY_03 union all
  select * from dc.DC_E_RBS_ULBASEBANDPOOL_V_DAY_04;

create view dcpublic.DC_E_RBS_ULBASEBANDPOOL_V_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    Equipment,
    Subrack,
    UplinkBaseBandPool,
    DCVECTOR_INDEX,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmNoOfRadioLinksSf128,
    pmNoOfRadioLinksSf16,
    pmNoOfRadioLinksSf256,
    pmNoOfRadioLinksSf32,
    pmNoOfRadioLinksSf4,
    pmNoOfRadioLinksSf64,
    pmNoOfRadioLinksSf8,
    pmHwCePoolEul,
    pmCapacityUlCe from
    dc.DC_E_RBS_ULBASEBANDPOOL_V_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_ELEMBH_RANKBH as
  select * from dc.DC_E_RBS_ELEMBH_RANKBH_01 union all
  select * from dc.DC_E_RBS_ELEMBH_RANKBH_02 union all
  select * from dc.DC_E_RBS_ELEMBH_RANKBH_03 union all
  select * from dc.DC_E_RBS_ELEMBH_RANKBH_04 union all
  select * from dc.DC_E_RBS_ELEMBH_RANKBH_05 union all
  select * from dc.DC_E_RBS_ELEMBH_RANKBH_06;

create view dcpublic.DC_E_RBS_ELEMBH_RANKBH
  as select OSS_ID,
    RBS,
    ELEMENT_NAME,
    ELEMENT_TYPE,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG from
    dc.DC_E_RBS_ELEMBH_RANKBH where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_CARRIERBH_RANKBH as
  select * from dc.DC_E_RBS_CARRIERBH_RANKBH_01 union all
  select * from dc.DC_E_RBS_CARRIERBH_RANKBH_02 union all
  select * from dc.DC_E_RBS_CARRIERBH_RANKBH_03 union all
  select * from dc.DC_E_RBS_CARRIERBH_RANKBH_04 union all
  select * from dc.DC_E_RBS_CARRIERBH_RANKBH_05 union all
  select * from dc.DC_E_RBS_CARRIERBH_RANKBH_06;

create view dcpublic.DC_E_RBS_CARRIERBH_RANKBH
  as select OSS_ID,
    RBS,
    RNC,
    Sector,
    Carrier,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG from
    dc.DC_E_RBS_CARRIERBH_RANKBH where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_NODEBFUNCTION_RAW as
  select * from dc.DC_E_RBS_NODEBFUNCTION_RAW_01 union all
  select * from dc.DC_E_RBS_NODEBFUNCTION_RAW_02 union all
  select * from dc.DC_E_RBS_NODEBFUNCTION_RAW_03;

create view dcpublic.DC_E_RBS_NODEBFUNCTION_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmCapacityNodeBDlCe,
    pmCapacityNodeBUlCe from
    dc.DC_E_RBS_NODEBFUNCTION_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_NODEBFUNCTION_DAY as
  select * from dc.DC_E_RBS_NODEBFUNCTION_DAY_01 union all
  select * from dc.DC_E_RBS_NODEBFUNCTION_DAY_02 union all
  select * from dc.DC_E_RBS_NODEBFUNCTION_DAY_03;

create view dcpublic.DC_E_RBS_NODEBFUNCTION_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmCapacityNodeBDlCe,
    pmCapacityNodeBUlCe from
    dc.DC_E_RBS_NODEBFUNCTION_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_NODEBFUNCTION_V_RAW as
  select * from dc.DC_E_RBS_NODEBFUNCTION_V_RAW_01 union all
  select * from dc.DC_E_RBS_NODEBFUNCTION_V_RAW_02 union all
  select * from dc.DC_E_RBS_NODEBFUNCTION_V_RAW_03 union all
  select * from dc.DC_E_RBS_NODEBFUNCTION_V_RAW_04 union all
  select * from dc.DC_E_RBS_NODEBFUNCTION_V_RAW_05 union all
  select * from dc.DC_E_RBS_NODEBFUNCTION_V_RAW_06 union all
  select * from dc.DC_E_RBS_NODEBFUNCTION_V_RAW_07 union all
  select * from dc.DC_E_RBS_NODEBFUNCTION_V_RAW_08 union all
  select * from dc.DC_E_RBS_NODEBFUNCTION_V_RAW_09;

create view dcpublic.DC_E_RBS_NODEBFUNCTION_V_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    DCVECTOR_INDEX,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmCapacityNodeBDlCe,
    pmCapacityNodeBUlCe from
    dc.DC_E_RBS_NODEBFUNCTION_V_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_NODEBFUNCTION_V_DAY as
  select * from dc.DC_E_RBS_NODEBFUNCTION_V_DAY_01 union all
  select * from dc.DC_E_RBS_NODEBFUNCTION_V_DAY_02 union all
  select * from dc.DC_E_RBS_NODEBFUNCTION_V_DAY_03 union all
  select * from dc.DC_E_RBS_NODEBFUNCTION_V_DAY_04;

create view dcpublic.DC_E_RBS_NODEBFUNCTION_V_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    DCVECTOR_INDEX,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmCapacityNodeBDlCe,
    pmCapacityNodeBUlCe from
    dc.DC_E_RBS_NODEBFUNCTION_V_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_SCCPCH_V_RAW as
  select * from dc.DC_E_RBS_SCCPCH_V_RAW_01 union all
  select * from dc.DC_E_RBS_SCCPCH_V_RAW_02 union all
  select * from dc.DC_E_RBS_SCCPCH_V_RAW_03 union all
  select * from dc.DC_E_RBS_SCCPCH_V_RAW_04 union all
  select * from dc.DC_E_RBS_SCCPCH_V_RAW_05 union all
  select * from dc.DC_E_RBS_SCCPCH_V_RAW_06 union all
  select * from dc.DC_E_RBS_SCCPCH_V_RAW_07 union all
  select * from dc.DC_E_RBS_SCCPCH_V_RAW_08 union all
  select * from dc.DC_E_RBS_SCCPCH_V_RAW_09;

create view dcpublic.DC_E_RBS_SCCPCH_V_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    Sccpch,
    DCVECTOR_INDEX,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmMbmsSccpchTransmittedTfc from
    dc.DC_E_RBS_SCCPCH_V_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_SCCPCH_V_DAY as
  select * from dc.DC_E_RBS_SCCPCH_V_DAY_01 union all
  select * from dc.DC_E_RBS_SCCPCH_V_DAY_02 union all
  select * from dc.DC_E_RBS_SCCPCH_V_DAY_03 union all
  select * from dc.DC_E_RBS_SCCPCH_V_DAY_04;

create view dcpublic.DC_E_RBS_SCCPCH_V_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    Sccpch,
    DCVECTOR_INDEX,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmMbmsSccpchTransmittedTfc from
    dc.DC_E_RBS_SCCPCH_V_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_RBS_SCCPCH_V_DAYBH as
  select * from dc.DC_E_RBS_SCCPCH_V_DAYBH_01 union all
  select * from dc.DC_E_RBS_SCCPCH_V_DAYBH_02 union all
  select * from dc.DC_E_RBS_SCCPCH_V_DAYBH_03 union all
  select * from dc.DC_E_RBS_SCCPCH_V_DAYBH_04;

create view dcpublic.DC_E_RBS_SCCPCH_V_DAYBH
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    Sccpch,
    DCVECTOR_INDEX,
    DATE_ID,
    MIN_ID,
    BHTYPE,
    BUSYHOUR,
    BHCLASS,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmMbmsSccpchTransmittedTfc from
    dc.DC_E_RBS_SCCPCH_V_DAYBH where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view
  dc.SELECT_E_RBS_AGGLEVEL( FIRSTDATE_ID,AGGLEVEL,DATE_ID,YEAR_ID,MONTH_ID,DAY_ID,WEEK_ID,WEEKDAY_ID,BUSINESSDAY,
  TIMELEVEL,TIMELEVELBH,DAYS) 
  as select FIRSTDATE_ID,AGGLEVEL,DATE_ID,YEAR_ID,MONTH_ID,DAY_ID,WEEK_ID,WEEKDAY_ID,BUSINESSDAY,
    TIMELEVEL,TIMELEVELBH,DAYS from dc.SELECT_AGGLEVEL_TABLE where
    TIMELEVELBH = 'DAYBH';

create view dcpublic.DIM_E_LTE_SITE
  as select OSS_ID,
    SITE_FDN,
    SITE_ID,
    SITE_NAME,
    TIMEZONE,
    TIMEZONE_VALUE,
    DST,
    VENDOR,
    STATUS,
    CREATED,
    MODIFIED,
    MODIFIER from
    dc.DIM_E_LTE_SITE;

create view dcpublic.DIM_E_LTE_ERBS
  as select OSS_ID,
    ERBS_FDN,
    ERBS_NAME,
    ERBS_ID,
    SITE_FDN,
    SITE_ID,
    MECONTEXTID,
    NEMIMVERSION,
    ERBS_VERSION,
    VENDOR,
    STATUS,
    CREATED,
    MODIFIED,
    MODIFIER from
    dc.DIM_E_LTE_ERBS;

create view dcpublic.DIM_E_LTE_SITEDST
  as select OSS_ID,
    SITE_FDN,
    SITE_ID,
    SITE_NAME,
    DST,
    VENDOR,
    STATUS,
    CREATED,
    MODIFIED,
    MODIFIER from
    dc.DIM_E_LTE_SITEDST;

create view dc.DIM_E_LTE_SITE_CURRENT as select * from dc.DIM_E_LTE_SITE_CURRENT_DC;

create view dc.DIM_E_LTE_SITEDST_CURRENT as select * from dc.DIM_E_LTE_SITEDST_CURRENT_DC;

create view dc.DIM_E_LTE_ERBS_CURRENT as select * from dc.DIM_E_LTE_ERBS_CURRENT_DC;

create view
  dc.SELECT_DIM_E_LTE_AGGLEVEL( FIRSTDATE_ID,AGGLEVEL,DATE_ID,YEAR_ID,MONTH_ID,DAY_ID,WEEK_ID,WEEKDAY_ID,BUSINESSDAY,
  TIMELEVEL,TIMELEVELBH,DAYS) 
  as select FIRSTDATE_ID,AGGLEVEL,DATE_ID,YEAR_ID,MONTH_ID,DAY_ID,WEEK_ID,WEEKDAY_ID,BUSINESSDAY,
    TIMELEVEL,TIMELEVELBH,DAYS from dc.SELECT_AGGLEVEL_TABLE where
    TIMELEVELBH = 'DAYBH';

create view dcpublic.DIM_E_TDRAN_SITE
  as select OSS_ID,
    SITE_FDN,
    SITE_ID,
    SITE_NAME,
    TIMEZONE,
    TIMEZONE_VALUE,
    DST,
    VENDOR,
    STATUS,
    CREATED,
    MODIFIED,
    MODIFIER from
    dc.DIM_E_TDRAN_SITE;

create view dcpublic.DIM_E_TDRAN_SITEDST
  as select OSS_ID,
    SITE_FDN,
    SITE_ID,
    SITE_NAME,
    DST,
    VENDOR,
    STATUS,
    CREATED,
    MODIFIED,
    MODIFIER from
    dc.DIM_E_TDRAN_SITEDST;

create view dcpublic.DIM_E_TDRAN_TDRNC
  as select OSS_ID,
    SUBNETWORK,
    TDRNC_FDN,
    TDRNC_NAME,
    TDRNC_ID,
    SITE_FDN,
    SITE,
    MECONTEXTID,
    NEMIMVERSION,
    TDRNC_VERSION,
    CPP_PLATFORM_VERSION,
    MSC,
    MCC,
    MNC,
    VENDOR,
    STATUS,
    CREATED,
    MODIFIED,
    MODIFIER from
    dc.DIM_E_TDRAN_TDRNC;

create view dcpublic.DIM_E_TDRAN_TDRBS
  as select OSS_ID,
    TDRBS_FDN,
    TDRBS_ID,
    TDRBS_NAME,
    MECONTEXTID,
    NEMIMVERSION,
    TDRBS_VERSION,
    CPP_PLATFORM_VERSION,
    RBSIUBID,
    TDRNC_ID,
    SITE_FDN,
    SITE,
    MCC,
    MNC,
    VENDOR,
    STATUS,
    CREATED,
    MODIFIED,
    MODIFIER from
    dc.DIM_E_TDRAN_TDRBS;

create view dcpublic.DIM_E_TDRAN_RBSLOCALCELL
  as select OSS_ID,
    RBSLOCALCELL_FDN,
    RBSLOCALCELL_ID,
    RBSLOCALCELL_NAME,
    RBSLOCALCELL_CARRIER,
    Sector,
    Carrier,
    TDRBS_ID,
    TDRNC_ID,
    VENDOR,
    STATUS,
    CREATED,
    MODIFIED,
    MODIFIER from
    dc.DIM_E_TDRAN_RBSLOCALCELL;

create view dcpublic.DIM_E_TDRAN_UCELL
  as select OSS_ID,
    UCELL_FDN,
    UCELL_ID,
    UCELL_NAME,
    LOCALCellID,
    TDRBS_ID,
    NODEB,
    SITE,
    TDRNC_ID,
    MSC,
    LAC,
    RAC,
    SAC,
    BCF_TYPE,
    MCC,
    MNC,
    UARFCNDL,
    UARFCNUL,
    SCRAMBLINGCODE,
    VENDOR,
    STATUS,
    CREATED,
    MODIFIED,
    MODIFIER from
    dc.DIM_E_TDRAN_UCELL;

create view dcpublic.DIM_E_TDRAN_UREL
  as select OSS_ID,
    UCELL_ID,
    UTRANRELATION_FDN,
    UTRANRELATION,
    ADJACENTCELL_FDN,
    ADJACENTCELL_SUB,
    ADJACENTCELL_ID,
    ADJACENT_TDRNC_ID,
    TDRNC_ID,
    VENDOR,
    STATUS,
    CREATED,
    MODIFIED,
    MODIFIER from
    dc.DIM_E_TDRAN_UREL;

create view dc.DIM_E_TDRAN_SITE_CURRENT as select * from dc.DIM_E_TDRAN_SITE_CURRENT_DC;

create view dc.DIM_E_TDRAN_SITEDST_CURRENT as select * from dc.DIM_E_TDRAN_SITEDST_CURRENT_DC;

create view dc.DIM_E_TDRAN_TDRNC_CURRENT as select * from dc.DIM_E_TDRAN_TDRNC_CURRENT_DC;

create view dc.DIM_E_TDRAN_TDRBS_CURRENT as select * from dc.DIM_E_TDRAN_TDRBS_CURRENT_DC;

create view dc.DIM_E_TDRAN_RBSLOCALCELL_CURRENT as select * from dc.DIM_E_TDRAN_RBSLOCALCELL_CURRENT_DC;

create view dc.DIM_E_TDRAN_UCELL_CURRENT as select * from dc.DIM_E_TDRAN_UCELL_CURRENT_DC;

create view dc.DIM_E_TDRAN_UREL_CURRENT as select * from dc.DIM_E_TDRAN_UREL_CURRENT_DC;

create view
  dc.SELECT_DIM_E_TDRAN_AGGLEVEL( FIRSTDATE_ID,AGGLEVEL,DATE_ID,YEAR_ID,MONTH_ID,DAY_ID,WEEK_ID,WEEKDAY_ID,BUSINESSDAY,
  TIMELEVEL,TIMELEVELBH,DAYS) 
  as select FIRSTDATE_ID,AGGLEVEL,DATE_ID,YEAR_ID,MONTH_ID,DAY_ID,WEEK_ID,WEEKDAY_ID,BUSINESSDAY,
    TIMELEVEL,TIMELEVELBH,DAYS from dc.SELECT_AGGLEVEL_TABLE where
    TIMELEVELBH = 'DAYBH';

create view dcpublic.DIM_E_CPP_VCLTPBH_BHTYPE
  as select BHTYPE,
    DESCRIPTION from
    dc.DIM_E_CPP_VCLTPBH_BHTYPE;

create view dcpublic.DIM_E_CPP_AAL2APBH_BHTYPE
  as select BHTYPE,
    DESCRIPTION from
    dc.DIM_E_CPP_AAL2APBH_BHTYPE;

create view dc.DC_E_CPP_IP_RAW as
  select * from dc.DC_E_CPP_IP_RAW_01 union all
  select * from dc.DC_E_CPP_IP_RAW_02 union all
  select * from dc.DC_E_CPP_IP_RAW_03;

create view dcpublic.DC_E_CPP_IP_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    IpOam,
    IpSystem,
    Ip,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfHdrErrors,
    pmNoOfIpAddrErrors,
    pmNoOfIpForwDatagrams,
    pmNoOfIpInDiscards,
    pmNoOfIpInReceives,
    pmNoOfIpOutDiscards,
    pmNoOfIpReasmOKs,
    pmNoOfIpReasmReqds from
    dc.DC_E_CPP_IP_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_AAL2PATHVCCTP_RAW as
  select * from dc.DC_E_CPP_AAL2PATHVCCTP_RAW_01 union all
  select * from dc.DC_E_CPP_AAL2PATHVCCTP_RAW_02 union all
  select * from dc.DC_E_CPP_AAL2PATHVCCTP_RAW_03 union all
  select * from dc.DC_E_CPP_AAL2PATHVCCTP_RAW_04 union all
  select * from dc.DC_E_CPP_AAL2PATHVCCTP_RAW_05 union all
  select * from dc.DC_E_CPP_AAL2PATHVCCTP_RAW_06;

create view dcpublic.DC_E_CPP_AAL2PATHVCCTP_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Aal2PathVccTp,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmBwErrBlocks,
    pmBwLostCells,
    pmBwMissinsCells,
    pmDiscardedEgressCpsPackets,
    pmEgressCpsPackets,
    pmFwErrBlocks,
    pmFwLostCells,
    pmFwMissinsCells,
    pmIngressCpsPackets,
    pmLostBrCells,
    pmLostFpmCells from
    dc.DC_E_CPP_AAL2PATHVCCTP_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_AAL2PATHVCCTP_DAY as
  select * from dc.DC_E_CPP_AAL2PATHVCCTP_DAY_01 union all
  select * from dc.DC_E_CPP_AAL2PATHVCCTP_DAY_02 union all
  select * from dc.DC_E_CPP_AAL2PATHVCCTP_DAY_03 union all
  select * from dc.DC_E_CPP_AAL2PATHVCCTP_DAY_04 union all
  select * from dc.DC_E_CPP_AAL2PATHVCCTP_DAY_05 union all
  select * from dc.DC_E_CPP_AAL2PATHVCCTP_DAY_06;

create view dcpublic.DC_E_CPP_AAL2PATHVCCTP_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Aal2PathVccTp,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmBwErrBlocks,
    pmBwLostCells,
    pmBwMissinsCells,
    pmDiscardedEgressCpsPackets,
    pmEgressCpsPackets,
    pmFwErrBlocks,
    pmFwLostCells,
    pmFwMissinsCells,
    pmIngressCpsPackets,
    pmLostBrCells,
    pmLostFpmCells from
    dc.DC_E_CPP_AAL2PATHVCCTP_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_AAL0TPVCCTP_RAW as
  select * from dc.DC_E_CPP_AAL0TPVCCTP_RAW_01 union all
  select * from dc.DC_E_CPP_AAL0TPVCCTP_RAW_02 union all
  select * from dc.DC_E_CPP_AAL0TPVCCTP_RAW_03 union all
  select * from dc.DC_E_CPP_AAL0TPVCCTP_RAW_04 union all
  select * from dc.DC_E_CPP_AAL0TPVCCTP_RAW_05 union all
  select * from dc.DC_E_CPP_AAL0TPVCCTP_RAW_06;

create view dcpublic.DC_E_CPP_AAL0TPVCCTP_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Aal0TpVccTp,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmBwErrBlocks,
    pmBwLostCells,
    pmBwMissinsCells,
    pmFwErrBlocks,
    pmFwLostCells,
    pmFwMissinsCells,
    pmLostBrCells,
    pmLostFpmCells from
    dc.DC_E_CPP_AAL0TPVCCTP_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_AAL0TPVCCTP_DAY as
  select * from dc.DC_E_CPP_AAL0TPVCCTP_DAY_01 union all
  select * from dc.DC_E_CPP_AAL0TPVCCTP_DAY_02 union all
  select * from dc.DC_E_CPP_AAL0TPVCCTP_DAY_03 union all
  select * from dc.DC_E_CPP_AAL0TPVCCTP_DAY_04 union all
  select * from dc.DC_E_CPP_AAL0TPVCCTP_DAY_05 union all
  select * from dc.DC_E_CPP_AAL0TPVCCTP_DAY_06;

create view dcpublic.DC_E_CPP_AAL0TPVCCTP_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Aal0TpVccTp,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmBwErrBlocks,
    pmBwLostCells,
    pmBwMissinsCells,
    pmFwErrBlocks,
    pmFwLostCells,
    pmFwMissinsCells,
    pmLostBrCells,
    pmLostFpmCells from
    dc.DC_E_CPP_AAL0TPVCCTP_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_AAL1TPVCCTP_RAW as
  select * from dc.DC_E_CPP_AAL1TPVCCTP_RAW_01 union all
  select * from dc.DC_E_CPP_AAL1TPVCCTP_RAW_02 union all
  select * from dc.DC_E_CPP_AAL1TPVCCTP_RAW_03;

create view dcpublic.DC_E_CPP_AAL1TPVCCTP_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Aal1TpVccTp,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmBwErrBlocks,
    pmBwLostCells,
    pmBwMissinsCells,
    pmFwErrBlocks,
    pmFwLostCells,
    pmFwMissinsCells,
    pmLostBrCells,
    pmLostFpmCells from
    dc.DC_E_CPP_AAL1TPVCCTP_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_AAL1TPVCCTP_DAY as
  select * from dc.DC_E_CPP_AAL1TPVCCTP_DAY_01 union all
  select * from dc.DC_E_CPP_AAL1TPVCCTP_DAY_02 union all
  select * from dc.DC_E_CPP_AAL1TPVCCTP_DAY_03;

create view dcpublic.DC_E_CPP_AAL1TPVCCTP_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Aal1TpVccTp,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmBwErrBlocks,
    pmBwLostCells,
    pmBwMissinsCells,
    pmFwErrBlocks,
    pmFwLostCells,
    pmFwMissinsCells,
    pmLostBrCells,
    pmLostFpmCells from
    dc.DC_E_CPP_AAL1TPVCCTP_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_AAL2AP_RAW as
  select * from dc.DC_E_CPP_AAL2AP_RAW_01 union all
  select * from dc.DC_E_CPP_AAL2AP_RAW_02 union all
  select * from dc.DC_E_CPP_AAL2AP_RAW_03 union all
  select * from dc.DC_E_CPP_AAL2AP_RAW_04 union all
  select * from dc.DC_E_CPP_AAL2AP_RAW_05 union all
  select * from dc.DC_E_CPP_AAL2AP_RAW_06;

create view dcpublic.DC_E_CPP_AAL2AP_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Aal2Sp,
    Aal2Ap,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmExisOrigConns,
    pmExisTermConns,
    pmExisTransConns,
    pmSuccInConnsRemoteQosClassA,
    pmSuccInConnsRemoteQosClassB,
    pmSuccInConnsRemoteQosClassC,
    pmSuccInConnsRemoteQosClassD,
    pmSuccOutConnsRemoteQosClassA,
    pmSuccOutConnsRemoteQosClassB,
    pmSuccOutConnsRemoteQosClassC,
    pmSuccOutConnsRemoteQosClassD,
    pmUnRecMessages,
    pmUnRecParams,
    pmUnSuccInConnsLocalQosClassA,
    pmUnSuccInConnsLocalQosClassB,
    pmUnSuccInConnsLocalQosClassC,
    pmUnSuccInConnsLocalQosClassD,
    pmUnSuccInConnsRemoteQosClassA,
    pmUnSuccInConnsRemoteQosClassB,
    pmUnSuccInConnsRemoteQosClassC,
    pmUnSuccInConnsRemoteQosClassD,
    pmUnSuccOutConnsLocalQosClassA,
    pmUnSuccOutConnsLocalQosClassB,
    pmUnSuccOutConnsLocalQosClassC,
    pmUnSuccOutConnsLocalQosClassD,
    pmUnSuccOutConnsRemoteQosClassA,
    pmUnSuccOutConnsRemoteQosClassB,
    pmUnSuccOutConnsRemoteQosClassC,
    pmUnSuccOutConnsRemoteQosClassD from
    dc.DC_E_CPP_AAL2AP_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_AAL2AP_DAY as
  select * from dc.DC_E_CPP_AAL2AP_DAY_01 union all
  select * from dc.DC_E_CPP_AAL2AP_DAY_02 union all
  select * from dc.DC_E_CPP_AAL2AP_DAY_03 union all
  select * from dc.DC_E_CPP_AAL2AP_DAY_04 union all
  select * from dc.DC_E_CPP_AAL2AP_DAY_05 union all
  select * from dc.DC_E_CPP_AAL2AP_DAY_06;

create view dcpublic.DC_E_CPP_AAL2AP_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Aal2Sp,
    Aal2Ap,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmExisOrigConns,
    pmExisTermConns,
    pmExisTransConns,
    pmSuccInConnsRemoteQosClassA,
    pmSuccInConnsRemoteQosClassB,
    pmSuccInConnsRemoteQosClassC,
    pmSuccInConnsRemoteQosClassD,
    pmSuccOutConnsRemoteQosClassA,
    pmSuccOutConnsRemoteQosClassB,
    pmSuccOutConnsRemoteQosClassC,
    pmSuccOutConnsRemoteQosClassD,
    pmUnRecMessages,
    pmUnRecParams,
    pmUnSuccInConnsLocalQosClassA,
    pmUnSuccInConnsLocalQosClassB,
    pmUnSuccInConnsLocalQosClassC,
    pmUnSuccInConnsLocalQosClassD,
    pmUnSuccInConnsRemoteQosClassA,
    pmUnSuccInConnsRemoteQosClassB,
    pmUnSuccInConnsRemoteQosClassC,
    pmUnSuccInConnsRemoteQosClassD,
    pmUnSuccOutConnsLocalQosClassA,
    pmUnSuccOutConnsLocalQosClassB,
    pmUnSuccOutConnsLocalQosClassC,
    pmUnSuccOutConnsLocalQosClassD,
    pmUnSuccOutConnsRemoteQosClassA,
    pmUnSuccOutConnsRemoteQosClassB,
    pmUnSuccOutConnsRemoteQosClassC,
    pmUnSuccOutConnsRemoteQosClassD from
    dc.DC_E_CPP_AAL2AP_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_AAL2AP_DAYBH as
  select * from dc.DC_E_CPP_AAL2AP_DAYBH_01 union all
  select * from dc.DC_E_CPP_AAL2AP_DAYBH_02 union all
  select * from dc.DC_E_CPP_AAL2AP_DAYBH_03 union all
  select * from dc.DC_E_CPP_AAL2AP_DAYBH_04 union all
  select * from dc.DC_E_CPP_AAL2AP_DAYBH_05 union all
  select * from dc.DC_E_CPP_AAL2AP_DAYBH_06;

create view dcpublic.DC_E_CPP_AAL2AP_DAYBH
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Aal2Sp,
    Aal2Ap,
    DATE_ID,
    MIN_ID,
    BHTYPE,
    BUSYHOUR,
    BHCLASS,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmExisOrigConns,
    pmExisTermConns,
    pmExisTransConns,
    pmSuccInConnsRemoteQosClassA,
    pmSuccInConnsRemoteQosClassB,
    pmSuccInConnsRemoteQosClassC,
    pmSuccInConnsRemoteQosClassD,
    pmSuccOutConnsRemoteQosClassA,
    pmSuccOutConnsRemoteQosClassB,
    pmSuccOutConnsRemoteQosClassC,
    pmSuccOutConnsRemoteQosClassD,
    pmUnRecMessages,
    pmUnRecParams,
    pmUnSuccInConnsLocalQosClassA,
    pmUnSuccInConnsLocalQosClassB,
    pmUnSuccInConnsLocalQosClassC,
    pmUnSuccInConnsLocalQosClassD,
    pmUnSuccInConnsRemoteQosClassA,
    pmUnSuccInConnsRemoteQosClassB,
    pmUnSuccInConnsRemoteQosClassC,
    pmUnSuccInConnsRemoteQosClassD,
    pmUnSuccOutConnsLocalQosClassA,
    pmUnSuccOutConnsLocalQosClassB,
    pmUnSuccOutConnsLocalQosClassC,
    pmUnSuccOutConnsLocalQosClassD,
    pmUnSuccOutConnsRemoteQosClassA,
    pmUnSuccOutConnsRemoteQosClassB,
    pmUnSuccOutConnsRemoteQosClassC,
    pmUnSuccOutConnsRemoteQosClassD from
    dc.DC_E_CPP_AAL2AP_DAYBH where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_AAL5TPVCCTP_RAW as
  select * from dc.DC_E_CPP_AAL5TPVCCTP_RAW_01 union all
  select * from dc.DC_E_CPP_AAL5TPVCCTP_RAW_02 union all
  select * from dc.DC_E_CPP_AAL5TPVCCTP_RAW_03 union all
  select * from dc.DC_E_CPP_AAL5TPVCCTP_RAW_04 union all
  select * from dc.DC_E_CPP_AAL5TPVCCTP_RAW_05 union all
  select * from dc.DC_E_CPP_AAL5TPVCCTP_RAW_06;

create view dcpublic.DC_E_CPP_AAL5TPVCCTP_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Aal5TpVccTp,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmBwErrBlocks,
    pmBwLostCells,
    pmBwMissinsCells,
    pmFwErrBlocks,
    pmFwLostCells,
    pmFwMissinsCells,
    pmLostBrCells,
    pmLostFpmCells from
    dc.DC_E_CPP_AAL5TPVCCTP_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_AAL5TPVCCTP_DAY as
  select * from dc.DC_E_CPP_AAL5TPVCCTP_DAY_01 union all
  select * from dc.DC_E_CPP_AAL5TPVCCTP_DAY_02 union all
  select * from dc.DC_E_CPP_AAL5TPVCCTP_DAY_03 union all
  select * from dc.DC_E_CPP_AAL5TPVCCTP_DAY_04 union all
  select * from dc.DC_E_CPP_AAL5TPVCCTP_DAY_05 union all
  select * from dc.DC_E_CPP_AAL5TPVCCTP_DAY_06;

create view dcpublic.DC_E_CPP_AAL5TPVCCTP_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Aal5TpVccTp,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmBwErrBlocks,
    pmBwLostCells,
    pmBwMissinsCells,
    pmFwErrBlocks,
    pmFwLostCells,
    pmFwMissinsCells,
    pmLostBrCells,
    pmLostFpmCells from
    dc.DC_E_CPP_AAL5TPVCCTP_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_ATMPORT_RAW as
  select * from dc.DC_E_CPP_ATMPORT_RAW_01 union all
  select * from dc.DC_E_CPP_ATMPORT_RAW_02 union all
  select * from dc.DC_E_CPP_ATMPORT_RAW_03 union all
  select * from dc.DC_E_CPP_ATMPORT_RAW_04 union all
  select * from dc.DC_E_CPP_ATMPORT_RAW_05 union all
  select * from dc.DC_E_CPP_ATMPORT_RAW_06;

create view dcpublic.DC_E_CPP_ATMPORT_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    AtmPort,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmReceivedAtmCells,
    pmSecondsWithUnexp,
    pmTransmittedAtmCells from
    dc.DC_E_CPP_ATMPORT_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_ATMPORT_DAY as
  select * from dc.DC_E_CPP_ATMPORT_DAY_01 union all
  select * from dc.DC_E_CPP_ATMPORT_DAY_02 union all
  select * from dc.DC_E_CPP_ATMPORT_DAY_03 union all
  select * from dc.DC_E_CPP_ATMPORT_DAY_04 union all
  select * from dc.DC_E_CPP_ATMPORT_DAY_05 union all
  select * from dc.DC_E_CPP_ATMPORT_DAY_06;

create view dcpublic.DC_E_CPP_ATMPORT_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    AtmPort,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmReceivedAtmCells,
    pmSecondsWithUnexp,
    pmTransmittedAtmCells from
    dc.DC_E_CPP_ATMPORT_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_ATMPORT_COUNT as
  select * from dc.DC_E_CPP_ATMPORT_COUNT_01 union all
  select * from dc.DC_E_CPP_ATMPORT_COUNT_02 union all
  select * from dc.DC_E_CPP_ATMPORT_COUNT_03 union all
  select * from dc.DC_E_CPP_ATMPORT_COUNT_04 union all
  select * from dc.DC_E_CPP_ATMPORT_COUNT_05 union all
  select * from dc.DC_E_CPP_ATMPORT_COUNT_06;

create view dcpublic.DC_E_CPP_ATMPORT_COUNT
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    AtmPort,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmReceivedAtmCells,
    pmSecondsWithUnexp,
    pmTransmittedAtmCells from
    dc.DC_E_CPP_ATMPORT_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_IMAGROUP_RAW as
  select * from dc.DC_E_CPP_IMAGROUP_RAW_01 union all
  select * from dc.DC_E_CPP_IMAGROUP_RAW_02 union all
  select * from dc.DC_E_CPP_IMAGROUP_RAW_03 union all
  select * from dc.DC_E_CPP_IMAGROUP_RAW_04 union all
  select * from dc.DC_E_CPP_IMAGROUP_RAW_05 union all
  select * from dc.DC_E_CPP_IMAGROUP_RAW_06;

create view dcpublic.DC_E_CPP_IMAGROUP_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    ImaGroup,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmGrFc,
    pmGrFcFe,
    pmGrUasIma from
    dc.DC_E_CPP_IMAGROUP_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_IMAGROUP_DAY as
  select * from dc.DC_E_CPP_IMAGROUP_DAY_01 union all
  select * from dc.DC_E_CPP_IMAGROUP_DAY_02 union all
  select * from dc.DC_E_CPP_IMAGROUP_DAY_03 union all
  select * from dc.DC_E_CPP_IMAGROUP_DAY_04 union all
  select * from dc.DC_E_CPP_IMAGROUP_DAY_05 union all
  select * from dc.DC_E_CPP_IMAGROUP_DAY_06;

create view dcpublic.DC_E_CPP_IMAGROUP_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    ImaGroup,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmGrFc,
    pmGrFcFe,
    pmGrUasIma from
    dc.DC_E_CPP_IMAGROUP_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_IMAGROUP_COUNT as
  select * from dc.DC_E_CPP_IMAGROUP_COUNT_01 union all
  select * from dc.DC_E_CPP_IMAGROUP_COUNT_02 union all
  select * from dc.DC_E_CPP_IMAGROUP_COUNT_03 union all
  select * from dc.DC_E_CPP_IMAGROUP_COUNT_04 union all
  select * from dc.DC_E_CPP_IMAGROUP_COUNT_05 union all
  select * from dc.DC_E_CPP_IMAGROUP_COUNT_06;

create view dcpublic.DC_E_CPP_IMAGROUP_COUNT
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    ImaGroup,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmGrFc,
    pmGrFcFe,
    pmGrUasIma from
    dc.DC_E_CPP_IMAGROUP_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_IMALINK_RAW as
  select * from dc.DC_E_CPP_IMALINK_RAW_01 union all
  select * from dc.DC_E_CPP_IMALINK_RAW_02 union all
  select * from dc.DC_E_CPP_IMALINK_RAW_03 union all
  select * from dc.DC_E_CPP_IMALINK_RAW_04 union all
  select * from dc.DC_E_CPP_IMALINK_RAW_05 union all
  select * from dc.DC_E_CPP_IMALINK_RAW_06;

create view dcpublic.DC_E_CPP_IMALINK_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    ImaGroup,
    ImaLink,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmIvIma,
    pmOifIma,
    pmRxFc,
    pmRxFcFe,
    pmRxStuffIma,
    pmRxUusIma,
    pmRxUusImaFe,
    pmSesIma,
    pmSesImaFe,
    pmTxFc,
    pmTxFcFe,
    pmTxStuffIma,
    pmTxUusIma,
    pmTxUusImaFe,
    pmUasIma,
    pmUasImaFe from
    dc.DC_E_CPP_IMALINK_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_IMALINK_DAY as
  select * from dc.DC_E_CPP_IMALINK_DAY_01 union all
  select * from dc.DC_E_CPP_IMALINK_DAY_02 union all
  select * from dc.DC_E_CPP_IMALINK_DAY_03 union all
  select * from dc.DC_E_CPP_IMALINK_DAY_04 union all
  select * from dc.DC_E_CPP_IMALINK_DAY_05 union all
  select * from dc.DC_E_CPP_IMALINK_DAY_06;

create view dcpublic.DC_E_CPP_IMALINK_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    ImaGroup,
    ImaLink,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmIvIma,
    pmOifIma,
    pmRxFc,
    pmRxFcFe,
    pmRxStuffIma,
    pmRxUusIma,
    pmRxUusImaFe,
    pmSesIma,
    pmSesImaFe,
    pmTxFc,
    pmTxFcFe,
    pmTxStuffIma,
    pmTxUusIma,
    pmTxUusImaFe,
    pmUasIma,
    pmUasImaFe from
    dc.DC_E_CPP_IMALINK_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_SCCPSP_RAW as
  select * from dc.DC_E_CPP_SCCPSP_RAW_01 union all
  select * from dc.DC_E_CPP_SCCPSP_RAW_02 union all
  select * from dc.DC_E_CPP_SCCPSP_RAW_03;

create view dcpublic.DC_E_CPP_SCCPSP_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    SccpSp,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfConInUseExceedHighWaterMark,
    pmNoOfConInUseReceededLowWaterMark,
    pmNoOfCREFRecFromNL,
    pmNoOfCREFSentToNL,
    pmNoOfCRRec,
    pmNoOfCRSent,
    pmNoOfDT1Rec,
    pmNoOfDT1Sent,
    pmNoOfERRRec,
    pmNoOfERRSent,
    pmNoOfLUDTRec,
    pmNoOfLUDTSSent,
    pmNoOfRLSDRecFromNL,
    pmNoOfRLSDSentToNL,
    pmNoOfSubsysAllowedSent,
    pmNoOfUDTRec,
    pmNoOfUDTSent,
    pmNoOfUDTSRec,
    pmNoOfUDTSSent,
    pmNoOfXUDTRec,
    pmNoOfXUDTSent,
    pmNoOfXUDTSRec,
    pmNoOfXUDTSSent from
    dc.DC_E_CPP_SCCPSP_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_IMALINK_COUNT as
  select * from dc.DC_E_CPP_IMALINK_COUNT_01 union all
  select * from dc.DC_E_CPP_IMALINK_COUNT_02 union all
  select * from dc.DC_E_CPP_IMALINK_COUNT_03 union all
  select * from dc.DC_E_CPP_IMALINK_COUNT_04 union all
  select * from dc.DC_E_CPP_IMALINK_COUNT_05 union all
  select * from dc.DC_E_CPP_IMALINK_COUNT_06;

create view dcpublic.DC_E_CPP_IMALINK_COUNT
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    ImaGroup,
    ImaLink,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmIvIma,
    pmOifIma,
    pmRxFc,
    pmRxFcFe,
    pmRxStuffIma,
    pmRxUusIma,
    pmRxUusImaFe,
    pmSesIma,
    pmSesImaFe,
    pmTxFc,
    pmTxFcFe,
    pmTxStuffIma,
    pmTxUusIma,
    pmTxUusImaFe,
    pmUasIma,
    pmUasImaFe from
    dc.DC_E_CPP_IMALINK_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MTP3BSLANSI_RAW as
  select * from dc.DC_E_CPP_MTP3BSLANSI_RAW_01 union all
  select * from dc.DC_E_CPP_MTP3BSLANSI_RAW_02 union all
  select * from dc.DC_E_CPP_MTP3BSLANSI_RAW_03 union all
  select * from dc.DC_E_CPP_MTP3BSLANSI_RAW_04 union all
  select * from dc.DC_E_CPP_MTP3BSLANSI_RAW_05 union all
  select * from dc.DC_E_CPP_MTP3BSLANSI_RAW_06;

create view dcpublic.DC_E_CPP_MTP3BSLANSI_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp3bSp,
    Mtp3bSpType,
    Mtp3bSls,
    Mtp3bSlAnsi,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfAALINServiceInd,
    pmNoOfAALOUTInd,
    pmNoOfCBDSent,
    pmNoOfCOOXCOSent,
    pmNoOfLocalLinkCongestCeaseRec,
    pmNoOfLocalLinkCongestRec,
    pmNoOfMSURec,
    pmNoOfMSUSent,
    pmNoOfRecUserData,
    pmNoOfSentUserData from
    dc.DC_E_CPP_MTP3BSLANSI_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MTP3BSLANSI_DAY as
  select * from dc.DC_E_CPP_MTP3BSLANSI_DAY_01 union all
  select * from dc.DC_E_CPP_MTP3BSLANSI_DAY_02 union all
  select * from dc.DC_E_CPP_MTP3BSLANSI_DAY_03 union all
  select * from dc.DC_E_CPP_MTP3BSLANSI_DAY_04 union all
  select * from dc.DC_E_CPP_MTP3BSLANSI_DAY_05 union all
  select * from dc.DC_E_CPP_MTP3BSLANSI_DAY_06;

create view dcpublic.DC_E_CPP_MTP3BSLANSI_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp3bSp,
    Mtp3bSpType,
    Mtp3bSls,
    Mtp3bSlAnsi,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmNoOfAALINServiceInd,
    pmNoOfAALOUTInd,
    pmNoOfCBDSent,
    pmNoOfCOOXCOSent,
    pmNoOfLocalLinkCongestCeaseRec,
    pmNoOfLocalLinkCongestRec,
    pmNoOfMSURec,
    pmNoOfMSUSent,
    pmNoOfRecUserData,
    pmNoOfSentUserData from
    dc.DC_E_CPP_MTP3BSLANSI_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MTP3BSLANSI_COUNT as
  select * from dc.DC_E_CPP_MTP3BSLANSI_COUNT_01 union all
  select * from dc.DC_E_CPP_MTP3BSLANSI_COUNT_02 union all
  select * from dc.DC_E_CPP_MTP3BSLANSI_COUNT_03 union all
  select * from dc.DC_E_CPP_MTP3BSLANSI_COUNT_04 union all
  select * from dc.DC_E_CPP_MTP3BSLANSI_COUNT_05 union all
  select * from dc.DC_E_CPP_MTP3BSLANSI_COUNT_06;

create view dcpublic.DC_E_CPP_MTP3BSLANSI_COUNT
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp3bSp,
    Mtp3bSpType,
    Mtp3bSls,
    Mtp3bSlAnsi,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfAALINServiceInd,
    pmNoOfAALOUTInd,
    pmNoOfCBDSent,
    pmNoOfCOOXCOSent,
    pmNoOfLocalLinkCongestCeaseRec,
    pmNoOfLocalLinkCongestRec,
    pmNoOfMSURec,
    pmNoOfMSUSent,
    pmNoOfRecUserData,
    pmNoOfSentUserData from
    dc.DC_E_CPP_MTP3BSLANSI_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MTP3BSLCHINA_RAW as
  select * from dc.DC_E_CPP_MTP3BSLCHINA_RAW_01 union all
  select * from dc.DC_E_CPP_MTP3BSLCHINA_RAW_02 union all
  select * from dc.DC_E_CPP_MTP3BSLCHINA_RAW_03 union all
  select * from dc.DC_E_CPP_MTP3BSLCHINA_RAW_04 union all
  select * from dc.DC_E_CPP_MTP3BSLCHINA_RAW_05 union all
  select * from dc.DC_E_CPP_MTP3BSLCHINA_RAW_06;

create view dcpublic.DC_E_CPP_MTP3BSLCHINA_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp3bSp,
    Mtp3bSpType,
    Mtp3bSls,
    Mtp3bSlChina,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfAALINServiceInd,
    pmNoOfAALOUTInd,
    pmNoOfCBDSent,
    pmNoOfCOOXCOSent,
    pmNoOfLocalLinkCongestCeaseRec,
    pmNoOfLocalLinkCongestRec,
    pmNoOfMSURec,
    pmNoOfMSUSent,
    pmNoOfRecUserData,
    pmNoOfSentUserData from
    dc.DC_E_CPP_MTP3BSLCHINA_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MTP3BSLCHINA_DAY as
  select * from dc.DC_E_CPP_MTP3BSLCHINA_DAY_01 union all
  select * from dc.DC_E_CPP_MTP3BSLCHINA_DAY_02 union all
  select * from dc.DC_E_CPP_MTP3BSLCHINA_DAY_03 union all
  select * from dc.DC_E_CPP_MTP3BSLCHINA_DAY_04 union all
  select * from dc.DC_E_CPP_MTP3BSLCHINA_DAY_05 union all
  select * from dc.DC_E_CPP_MTP3BSLCHINA_DAY_06;

create view dcpublic.DC_E_CPP_MTP3BSLCHINA_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp3bSp,
    Mtp3bSpType,
    Mtp3bSls,
    Mtp3bSlChina,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmNoOfAALINServiceInd,
    pmNoOfAALOUTInd,
    pmNoOfCBDSent,
    pmNoOfCOOXCOSent,
    pmNoOfLocalLinkCongestCeaseRec,
    pmNoOfLocalLinkCongestRec,
    pmNoOfMSURec,
    pmNoOfMSUSent,
    pmNoOfRecUserData,
    pmNoOfSentUserData from
    dc.DC_E_CPP_MTP3BSLCHINA_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MTP3BSLCHINA_COUNT as
  select * from dc.DC_E_CPP_MTP3BSLCHINA_COUNT_01 union all
  select * from dc.DC_E_CPP_MTP3BSLCHINA_COUNT_02 union all
  select * from dc.DC_E_CPP_MTP3BSLCHINA_COUNT_03 union all
  select * from dc.DC_E_CPP_MTP3BSLCHINA_COUNT_04 union all
  select * from dc.DC_E_CPP_MTP3BSLCHINA_COUNT_05 union all
  select * from dc.DC_E_CPP_MTP3BSLCHINA_COUNT_06;

create view dcpublic.DC_E_CPP_MTP3BSLCHINA_COUNT
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp3bSp,
    Mtp3bSpType,
    Mtp3bSls,
    Mtp3bSlChina,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfAALINServiceInd,
    pmNoOfAALOUTInd,
    pmNoOfCBDSent,
    pmNoOfCOOXCOSent,
    pmNoOfLocalLinkCongestCeaseRec,
    pmNoOfLocalLinkCongestRec,
    pmNoOfMSURec,
    pmNoOfMSUSent,
    pmNoOfRecUserData,
    pmNoOfSentUserData from
    dc.DC_E_CPP_MTP3BSLCHINA_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MTP3BSLITU_RAW as
  select * from dc.DC_E_CPP_MTP3BSLITU_RAW_01 union all
  select * from dc.DC_E_CPP_MTP3BSLITU_RAW_02 union all
  select * from dc.DC_E_CPP_MTP3BSLITU_RAW_03 union all
  select * from dc.DC_E_CPP_MTP3BSLITU_RAW_04 union all
  select * from dc.DC_E_CPP_MTP3BSLITU_RAW_05 union all
  select * from dc.DC_E_CPP_MTP3BSLITU_RAW_06;

create view dcpublic.DC_E_CPP_MTP3BSLITU_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp3bSp,
    Mtp3bSpType,
    Mtp3bSls,
    Mtp3bSlItu,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfAALINServiceInd,
    pmNoOfAALOUTInd,
    pmNoOfCBDSent,
    pmNoOfCOOXCOSent,
    pmNoOfLocalLinkCongestCeaseRec,
    pmNoOfLocalLinkCongestRec,
    pmNoOfMSURec,
    pmNoOfMSUSent,
    pmNoOfRecUserData,
    pmNoOfSentUserData from
    dc.DC_E_CPP_MTP3BSLITU_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MTP3BSLITU_DAY as
  select * from dc.DC_E_CPP_MTP3BSLITU_DAY_01 union all
  select * from dc.DC_E_CPP_MTP3BSLITU_DAY_02 union all
  select * from dc.DC_E_CPP_MTP3BSLITU_DAY_03 union all
  select * from dc.DC_E_CPP_MTP3BSLITU_DAY_04 union all
  select * from dc.DC_E_CPP_MTP3BSLITU_DAY_05 union all
  select * from dc.DC_E_CPP_MTP3BSLITU_DAY_06;

create view dcpublic.DC_E_CPP_MTP3BSLITU_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp3bSp,
    Mtp3bSpType,
    Mtp3bSls,
    Mtp3bSlItu,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmNoOfAALINServiceInd,
    pmNoOfAALOUTInd,
    pmNoOfCBDSent,
    pmNoOfCOOXCOSent,
    pmNoOfLocalLinkCongestCeaseRec,
    pmNoOfLocalLinkCongestRec,
    pmNoOfMSURec,
    pmNoOfMSUSent,
    pmNoOfRecUserData,
    pmNoOfSentUserData from
    dc.DC_E_CPP_MTP3BSLITU_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MTP3BSLITU_COUNT as
  select * from dc.DC_E_CPP_MTP3BSLITU_COUNT_01 union all
  select * from dc.DC_E_CPP_MTP3BSLITU_COUNT_02 union all
  select * from dc.DC_E_CPP_MTP3BSLITU_COUNT_03 union all
  select * from dc.DC_E_CPP_MTP3BSLITU_COUNT_04 union all
  select * from dc.DC_E_CPP_MTP3BSLITU_COUNT_05 union all
  select * from dc.DC_E_CPP_MTP3BSLITU_COUNT_06;

create view dcpublic.DC_E_CPP_MTP3BSLITU_COUNT
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp3bSp,
    Mtp3bSpType,
    Mtp3bSls,
    Mtp3bSlItu,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfAALINServiceInd,
    pmNoOfAALOUTInd,
    pmNoOfCBDSent,
    pmNoOfCOOXCOSent,
    pmNoOfLocalLinkCongestCeaseRec,
    pmNoOfLocalLinkCongestRec,
    pmNoOfMSURec,
    pmNoOfMSUSent,
    pmNoOfRecUserData,
    pmNoOfSentUserData from
    dc.DC_E_CPP_MTP3BSLITU_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_SCCPSP_DAY as
  select * from dc.DC_E_CPP_SCCPSP_DAY_01 union all
  select * from dc.DC_E_CPP_SCCPSP_DAY_02 union all
  select * from dc.DC_E_CPP_SCCPSP_DAY_03;

create view dcpublic.DC_E_CPP_SCCPSP_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    SccpSp,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmNoOfConInUseExceedHighWaterMark,
    pmNoOfConInUseReceededLowWaterMark,
    pmNoOfCREFRecFromNL,
    pmNoOfCREFSentToNL,
    pmNoOfCRRec,
    pmNoOfCRSent,
    pmNoOfDT1Rec,
    pmNoOfDT1Sent,
    pmNoOfERRRec,
    pmNoOfERRSent,
    pmNoOfLUDTRec,
    pmNoOfLUDTSSent,
    pmNoOfRLSDRecFromNL,
    pmNoOfRLSDSentToNL,
    pmNoOfSubsysAllowedSent,
    pmNoOfUDTRec,
    pmNoOfUDTSent,
    pmNoOfUDTSRec,
    pmNoOfUDTSSent,
    pmNoOfXUDTRec,
    pmNoOfXUDTSent,
    pmNoOfXUDTSRec,
    pmNoOfXUDTSSent from
    dc.DC_E_CPP_SCCPSP_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_SCCPSP_COUNT as
  select * from dc.DC_E_CPP_SCCPSP_COUNT_01 union all
  select * from dc.DC_E_CPP_SCCPSP_COUNT_02 union all
  select * from dc.DC_E_CPP_SCCPSP_COUNT_03;

create view dcpublic.DC_E_CPP_SCCPSP_COUNT
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    SccpSp,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfConInUseExceedHighWaterMark,
    pmNoOfConInUseReceededLowWaterMark,
    pmNoOfCREFRecFromNL,
    pmNoOfCREFSentToNL,
    pmNoOfCRRec,
    pmNoOfCRSent,
    pmNoOfDT1Rec,
    pmNoOfDT1Sent,
    pmNoOfERRRec,
    pmNoOfERRSent,
    pmNoOfLUDTRec,
    pmNoOfLUDTSSent,
    pmNoOfRLSDRecFromNL,
    pmNoOfRLSDSentToNL,
    pmNoOfSubsysAllowedSent,
    pmNoOfUDTRec,
    pmNoOfUDTSent,
    pmNoOfUDTSRec,
    pmNoOfUDTSSent,
    pmNoOfXUDTRec,
    pmNoOfXUDTSent,
    pmNoOfXUDTSRec,
    pmNoOfXUDTSSent from
    dc.DC_E_CPP_SCCPSP_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MTP3BSLTTC_RAW as
  select * from dc.DC_E_CPP_MTP3BSLTTC_RAW_01 union all
  select * from dc.DC_E_CPP_MTP3BSLTTC_RAW_02 union all
  select * from dc.DC_E_CPP_MTP3BSLTTC_RAW_03 union all
  select * from dc.DC_E_CPP_MTP3BSLTTC_RAW_04 union all
  select * from dc.DC_E_CPP_MTP3BSLTTC_RAW_05 union all
  select * from dc.DC_E_CPP_MTP3BSLTTC_RAW_06;

create view dcpublic.DC_E_CPP_MTP3BSLTTC_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp3bSp,
    Mtp3bSpType,
    Mtp3bSls,
    Mtp3bSlTtc,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfAALINServiceInd,
    pmNoOfAALOUTInd,
    pmNoOfCBDSent,
    pmNoOfCOOXCOSent,
    pmNoOfLocalLinkCongestCeaseRec,
    pmNoOfLocalLinkCongestRec,
    pmNoOfMSURec,
    pmNoOfMSUSent,
    pmNoOfRecUserData,
    pmNoOfSentUserData from
    dc.DC_E_CPP_MTP3BSLTTC_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MTP3BSLTTC_DAY as
  select * from dc.DC_E_CPP_MTP3BSLTTC_DAY_01 union all
  select * from dc.DC_E_CPP_MTP3BSLTTC_DAY_02 union all
  select * from dc.DC_E_CPP_MTP3BSLTTC_DAY_03 union all
  select * from dc.DC_E_CPP_MTP3BSLTTC_DAY_04 union all
  select * from dc.DC_E_CPP_MTP3BSLTTC_DAY_05 union all
  select * from dc.DC_E_CPP_MTP3BSLTTC_DAY_06;

create view dcpublic.DC_E_CPP_MTP3BSLTTC_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp3bSp,
    Mtp3bSpType,
    Mtp3bSls,
    Mtp3bSlTtc,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmNoOfAALINServiceInd,
    pmNoOfAALOUTInd,
    pmNoOfCBDSent,
    pmNoOfCOOXCOSent,
    pmNoOfLocalLinkCongestCeaseRec,
    pmNoOfLocalLinkCongestRec,
    pmNoOfMSURec,
    pmNoOfMSUSent,
    pmNoOfRecUserData,
    pmNoOfSentUserData from
    dc.DC_E_CPP_MTP3BSLTTC_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dcpublic.DIM_E_TDRBS_CARRIERBH_BHTYPE
  as select BHTYPE,
    DESCRIPTION from
    dc.DIM_E_TDRBS_CARRIERBH_BHTYPE;

create view dc.DC_E_CPP_MTP3BSLTTC_COUNT as
  select * from dc.DC_E_CPP_MTP3BSLTTC_COUNT_01 union all
  select * from dc.DC_E_CPP_MTP3BSLTTC_COUNT_02 union all
  select * from dc.DC_E_CPP_MTP3BSLTTC_COUNT_03 union all
  select * from dc.DC_E_CPP_MTP3BSLTTC_COUNT_04 union all
  select * from dc.DC_E_CPP_MTP3BSLTTC_COUNT_05 union all
  select * from dc.DC_E_CPP_MTP3BSLTTC_COUNT_06;

create view dcpublic.DC_E_CPP_MTP3BSLTTC_COUNT
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp3bSp,
    Mtp3bSpType,
    Mtp3bSls,
    Mtp3bSlTtc,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfAALINServiceInd,
    pmNoOfAALOUTInd,
    pmNoOfCBDSent,
    pmNoOfCOOXCOSent,
    pmNoOfLocalLinkCongestCeaseRec,
    pmNoOfLocalLinkCongestRec,
    pmNoOfMSURec,
    pmNoOfMSUSent,
    pmNoOfRecUserData,
    pmNoOfSentUserData from
    dc.DC_E_CPP_MTP3BSLTTC_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_VC12TTP_RAW as
  select * from dc.DC_E_CPP_VC12TTP_RAW_01 union all
  select * from dc.DC_E_CPP_VC12TTP_RAW_02 union all
  select * from dc.DC_E_CPP_VC12TTP_RAW_03 union all
  select * from dc.DC_E_CPP_VC12TTP_RAW_04 union all
  select * from dc.DC_E_CPP_VC12TTP_RAW_05 union all
  select * from dc.DC_E_CPP_VC12TTP_RAW_06;

create view dcpublic.DC_E_CPP_VC12TTP_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    Cbu,
    ExchangeTerminal,
    Os155SpiTtp,
    Vc4Ttp,
    Vc12Ttp,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmVcBbe,
    pmVcEs,
    pmVcSes,
    pmVcUas from
    dc.DC_E_CPP_VC12TTP_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_VC12TTP_DAY as
  select * from dc.DC_E_CPP_VC12TTP_DAY_01 union all
  select * from dc.DC_E_CPP_VC12TTP_DAY_02 union all
  select * from dc.DC_E_CPP_VC12TTP_DAY_03 union all
  select * from dc.DC_E_CPP_VC12TTP_DAY_04 union all
  select * from dc.DC_E_CPP_VC12TTP_DAY_05 union all
  select * from dc.DC_E_CPP_VC12TTP_DAY_06;

create view dcpublic.DC_E_CPP_VC12TTP_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    Cbu,
    ExchangeTerminal,
    Os155SpiTtp,
    Vc4Ttp,
    Vc12Ttp,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmVcBbe,
    pmVcEs,
    pmVcSes,
    pmVcUas from
    dc.DC_E_CPP_VC12TTP_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_VCLTP_RAW as
  select * from dc.DC_E_CPP_VCLTP_RAW_01 union all
  select * from dc.DC_E_CPP_VCLTP_RAW_02 union all
  select * from dc.DC_E_CPP_VCLTP_RAW_03 union all
  select * from dc.DC_E_CPP_VCLTP_RAW_04 union all
  select * from dc.DC_E_CPP_VCLTP_RAW_05 union all
  select * from dc.DC_E_CPP_VCLTP_RAW_06;

create view dcpublic.DC_E_CPP_VCLTP_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    AtmPort,
    VplTp,
    VpcTp,
    VclTp,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmReceivedAtmCells,
    pmTransmittedAtmCells from
    dc.DC_E_CPP_VCLTP_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_VCLTP_DAY as
  select * from dc.DC_E_CPP_VCLTP_DAY_01 union all
  select * from dc.DC_E_CPP_VCLTP_DAY_02 union all
  select * from dc.DC_E_CPP_VCLTP_DAY_03 union all
  select * from dc.DC_E_CPP_VCLTP_DAY_04 union all
  select * from dc.DC_E_CPP_VCLTP_DAY_05 union all
  select * from dc.DC_E_CPP_VCLTP_DAY_06;

create view dcpublic.DC_E_CPP_VCLTP_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    AtmPort,
    VplTp,
    VpcTp,
    VclTp,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmReceivedAtmCells,
    pmTransmittedAtmCells from
    dc.DC_E_CPP_VCLTP_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_VCLTP_DAYBH as
  select * from dc.DC_E_CPP_VCLTP_DAYBH_01 union all
  select * from dc.DC_E_CPP_VCLTP_DAYBH_02 union all
  select * from dc.DC_E_CPP_VCLTP_DAYBH_03 union all
  select * from dc.DC_E_CPP_VCLTP_DAYBH_04 union all
  select * from dc.DC_E_CPP_VCLTP_DAYBH_05 union all
  select * from dc.DC_E_CPP_VCLTP_DAYBH_06;

create view dcpublic.DC_E_CPP_VCLTP_DAYBH
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    AtmPort,
    VplTp,
    VpcTp,
    VclTp,
    DATE_ID,
    MIN_ID,
    BHTYPE,
    BUSYHOUR,
    BHCLASS,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmReceivedAtmCells,
    pmTransmittedAtmCells from
    dc.DC_E_CPP_VCLTP_DAYBH where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_VCLTP_COUNT as
  select * from dc.DC_E_CPP_VCLTP_COUNT_01 union all
  select * from dc.DC_E_CPP_VCLTP_COUNT_02 union all
  select * from dc.DC_E_CPP_VCLTP_COUNT_03 union all
  select * from dc.DC_E_CPP_VCLTP_COUNT_04 union all
  select * from dc.DC_E_CPP_VCLTP_COUNT_05 union all
  select * from dc.DC_E_CPP_VCLTP_COUNT_06;

create view dcpublic.DC_E_CPP_VCLTP_COUNT
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    AtmPort,
    VplTp,
    VpcTp,
    VclTp,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmReceivedAtmCells,
    pmTransmittedAtmCells from
    dc.DC_E_CPP_VCLTP_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_VCLTP_V_RAW as
  select * from dc.DC_E_CPP_VCLTP_V_RAW_01 union all
  select * from dc.DC_E_CPP_VCLTP_V_RAW_02 union all
  select * from dc.DC_E_CPP_VCLTP_V_RAW_03 union all
  select * from dc.DC_E_CPP_VCLTP_V_RAW_04 union all
  select * from dc.DC_E_CPP_VCLTP_V_RAW_05 union all
  select * from dc.DC_E_CPP_VCLTP_V_RAW_06 union all
  select * from dc.DC_E_CPP_VCLTP_V_RAW_07 union all
  select * from dc.DC_E_CPP_VCLTP_V_RAW_08 union all
  select * from dc.DC_E_CPP_VCLTP_V_RAW_09;

create view dcpublic.DC_E_CPP_VCLTP_V_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    AtmPort,
    VplTp,
    VpcTp,
    VclTp,
    DCVECTOR_INDEX,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmBwUtilizationRx,
    pmBwUtilizationTx from
    dc.DC_E_CPP_VCLTP_V_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_VCLTP_V_DAY as
  select * from dc.DC_E_CPP_VCLTP_V_DAY_01 union all
  select * from dc.DC_E_CPP_VCLTP_V_DAY_02 union all
  select * from dc.DC_E_CPP_VCLTP_V_DAY_03 union all
  select * from dc.DC_E_CPP_VCLTP_V_DAY_04;

create view dcpublic.DC_E_CPP_VCLTP_V_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    AtmPort,
    VplTp,
    VpcTp,
    VclTp,
    DCVECTOR_INDEX,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmBwUtilizationRx,
    pmBwUtilizationTx from
    dc.DC_E_CPP_VCLTP_V_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_VCLTP_V_DAYBH as
  select * from dc.DC_E_CPP_VCLTP_V_DAYBH_01 union all
  select * from dc.DC_E_CPP_VCLTP_V_DAYBH_02 union all
  select * from dc.DC_E_CPP_VCLTP_V_DAYBH_03 union all
  select * from dc.DC_E_CPP_VCLTP_V_DAYBH_04;

create view dcpublic.DC_E_CPP_VCLTP_V_DAYBH
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    AtmPort,
    VplTp,
    VpcTp,
    VclTp,
    DCVECTOR_INDEX,
    DATE_ID,
    MIN_ID,
    BHTYPE,
    BUSYHOUR,
    BHCLASS,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmBwUtilizationRx,
    pmBwUtilizationTx from
    dc.DC_E_CPP_VCLTP_V_DAYBH where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_VPCTP_RAW as
  select * from dc.DC_E_CPP_VPCTP_RAW_01 union all
  select * from dc.DC_E_CPP_VPCTP_RAW_02 union all
  select * from dc.DC_E_CPP_VPCTP_RAW_03 union all
  select * from dc.DC_E_CPP_VPCTP_RAW_04 union all
  select * from dc.DC_E_CPP_VPCTP_RAW_05 union all
  select * from dc.DC_E_CPP_VPCTP_RAW_06;

create view dcpublic.DC_E_CPP_VPCTP_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    AtmPort,
    VplTp,
    VpcTp,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmBwErrBlocks,
    pmBwLostCells,
    pmBwMissinsCells,
    pmFwErrBlocks,
    pmFwLostCells,
    pmFwMissinsCells,
    pmLostBrCells,
    pmLostFpmCells from
    dc.DC_E_CPP_VPCTP_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_VPCTP_DAY as
  select * from dc.DC_E_CPP_VPCTP_DAY_01 union all
  select * from dc.DC_E_CPP_VPCTP_DAY_02 union all
  select * from dc.DC_E_CPP_VPCTP_DAY_03 union all
  select * from dc.DC_E_CPP_VPCTP_DAY_04 union all
  select * from dc.DC_E_CPP_VPCTP_DAY_05 union all
  select * from dc.DC_E_CPP_VPCTP_DAY_06;

create view dcpublic.DC_E_CPP_VPCTP_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    AtmPort,
    VplTp,
    VpcTp,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmBwErrBlocks,
    pmBwLostCells,
    pmBwMissinsCells,
    pmFwErrBlocks,
    pmFwLostCells,
    pmFwMissinsCells,
    pmLostBrCells,
    pmLostFpmCells from
    dc.DC_E_CPP_VPCTP_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_VPCTP_COUNT as
  select * from dc.DC_E_CPP_VPCTP_COUNT_01 union all
  select * from dc.DC_E_CPP_VPCTP_COUNT_02 union all
  select * from dc.DC_E_CPP_VPCTP_COUNT_03 union all
  select * from dc.DC_E_CPP_VPCTP_COUNT_04 union all
  select * from dc.DC_E_CPP_VPCTP_COUNT_05 union all
  select * from dc.DC_E_CPP_VPCTP_COUNT_06;

create view dcpublic.DC_E_CPP_VPCTP_COUNT
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    AtmPort,
    VplTp,
    VpcTp,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmBwErrBlocks,
    pmBwLostCells,
    pmBwMissinsCells,
    pmFwErrBlocks,
    pmFwLostCells,
    pmFwMissinsCells,
    pmLostBrCells,
    pmLostFpmCells from
    dc.DC_E_CPP_VPCTP_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_VPLTP_RAW as
  select * from dc.DC_E_CPP_VPLTP_RAW_01 union all
  select * from dc.DC_E_CPP_VPLTP_RAW_02 union all
  select * from dc.DC_E_CPP_VPLTP_RAW_03 union all
  select * from dc.DC_E_CPP_VPLTP_RAW_04 union all
  select * from dc.DC_E_CPP_VPLTP_RAW_05 union all
  select * from dc.DC_E_CPP_VPLTP_RAW_06;

create view dcpublic.DC_E_CPP_VPLTP_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    AtmPort,
    VplTp,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmReceivedAtmCells,
    pmTransmittedAtmCells from
    dc.DC_E_CPP_VPLTP_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_VPLTP_DAY as
  select * from dc.DC_E_CPP_VPLTP_DAY_01 union all
  select * from dc.DC_E_CPP_VPLTP_DAY_02 union all
  select * from dc.DC_E_CPP_VPLTP_DAY_03 union all
  select * from dc.DC_E_CPP_VPLTP_DAY_04 union all
  select * from dc.DC_E_CPP_VPLTP_DAY_05 union all
  select * from dc.DC_E_CPP_VPLTP_DAY_06;

create view dcpublic.DC_E_CPP_VPLTP_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    AtmPort,
    VplTp,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmReceivedAtmCells,
    pmTransmittedAtmCells from
    dc.DC_E_CPP_VPLTP_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_VPLTP_COUNT as
  select * from dc.DC_E_CPP_VPLTP_COUNT_01 union all
  select * from dc.DC_E_CPP_VPLTP_COUNT_02 union all
  select * from dc.DC_E_CPP_VPLTP_COUNT_03 union all
  select * from dc.DC_E_CPP_VPLTP_COUNT_04 union all
  select * from dc.DC_E_CPP_VPLTP_COUNT_05 union all
  select * from dc.DC_E_CPP_VPLTP_COUNT_06;

create view dcpublic.DC_E_CPP_VPLTP_COUNT
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    AtmPort,
    VplTp,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmReceivedAtmCells,
    pmTransmittedAtmCells from
    dc.DC_E_CPP_VPLTP_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_AAL2SP_RAW as
  select * from dc.DC_E_CPP_AAL2SP_RAW_01 union all
  select * from dc.DC_E_CPP_AAL2SP_RAW_02 union all
  select * from dc.DC_E_CPP_AAL2SP_RAW_03;

create view dcpublic.DC_E_CPP_AAL2SP_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Aal2Sp,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmUnsuccessfulConnsInternal from
    dc.DC_E_CPP_AAL2SP_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_AAL2SP_DAY as
  select * from dc.DC_E_CPP_AAL2SP_DAY_01 union all
  select * from dc.DC_E_CPP_AAL2SP_DAY_02 union all
  select * from dc.DC_E_CPP_AAL2SP_DAY_03;

create view dcpublic.DC_E_CPP_AAL2SP_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Aal2Sp,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmUnsuccessfulConnsInternal from
    dc.DC_E_CPP_AAL2SP_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_AAL2SP_COUNT as
  select * from dc.DC_E_CPP_AAL2SP_COUNT_01 union all
  select * from dc.DC_E_CPP_AAL2SP_COUNT_02 union all
  select * from dc.DC_E_CPP_AAL2SP_COUNT_03;

create view dcpublic.DC_E_CPP_AAL2SP_COUNT
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Aal2Sp,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmUnsuccessfulConnsInternal from
    dc.DC_E_CPP_AAL2SP_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_E1PHYSPATHTERM_RAW as
  select * from dc.DC_E_CPP_E1PHYSPATHTERM_RAW_01 union all
  select * from dc.DC_E_CPP_E1PHYSPATHTERM_RAW_02 union all
  select * from dc.DC_E_CPP_E1PHYSPATHTERM_RAW_03 union all
  select * from dc.DC_E_CPP_E1PHYSPATHTERM_RAW_04 union all
  select * from dc.DC_E_CPP_E1PHYSPATHTERM_RAW_05 union all
  select * from dc.DC_E_CPP_E1PHYSPATHTERM_RAW_06;

create view dcpublic.DC_E_CPP_E1PHYSPATHTERM_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    Cbu,
    ExchangeTerminal,
    E1PhysPathTerm,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmEs,
    pmSes,
    pmUas from
    dc.DC_E_CPP_E1PHYSPATHTERM_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_E1PHYSPATHTERM_DAY as
  select * from dc.DC_E_CPP_E1PHYSPATHTERM_DAY_01 union all
  select * from dc.DC_E_CPP_E1PHYSPATHTERM_DAY_02 union all
  select * from dc.DC_E_CPP_E1PHYSPATHTERM_DAY_03 union all
  select * from dc.DC_E_CPP_E1PHYSPATHTERM_DAY_04 union all
  select * from dc.DC_E_CPP_E1PHYSPATHTERM_DAY_05 union all
  select * from dc.DC_E_CPP_E1PHYSPATHTERM_DAY_06;

create view dcpublic.DC_E_CPP_E1PHYSPATHTERM_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    Cbu,
    ExchangeTerminal,
    E1PhysPathTerm,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmEs,
    pmSes,
    pmUas from
    dc.DC_E_CPP_E1PHYSPATHTERM_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_E1TTP_RAW as
  select * from dc.DC_E_CPP_E1TTP_RAW_01 union all
  select * from dc.DC_E_CPP_E1TTP_RAW_02 union all
  select * from dc.DC_E_CPP_E1TTP_RAW_03 union all
  select * from dc.DC_E_CPP_E1TTP_RAW_04 union all
  select * from dc.DC_E_CPP_E1TTP_RAW_05 union all
  select * from dc.DC_E_CPP_E1TTP_RAW_06;

create view dcpublic.DC_E_CPP_E1TTP_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    Cbu,
    ExchangeTerminal,
    Os155SpiTtp,
    Vc4Ttp,
    Vc12Ttp,
    E1Ttp,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmEs,
    pmSes,
    pmUas from
    dc.DC_E_CPP_E1TTP_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_E1TTP_DAY as
  select * from dc.DC_E_CPP_E1TTP_DAY_01 union all
  select * from dc.DC_E_CPP_E1TTP_DAY_02 union all
  select * from dc.DC_E_CPP_E1TTP_DAY_03 union all
  select * from dc.DC_E_CPP_E1TTP_DAY_04 union all
  select * from dc.DC_E_CPP_E1TTP_DAY_05 union all
  select * from dc.DC_E_CPP_E1TTP_DAY_06;

create view
  dc.SELECT_E_IMS_PTT_AGGLEVEL( FIRSTDATE_ID,AGGLEVEL,DATE_ID,YEAR_ID,MONTH_ID,DAY_ID,WEEK_ID,WEEKDAY_ID,BUSINESSDAY,
  TIMELEVEL,TIMELEVELBH,DAYS) 
  as select FIRSTDATE_ID,AGGLEVEL,DATE_ID,YEAR_ID,MONTH_ID,DAY_ID,WEEK_ID,WEEKDAY_ID,BUSINESSDAY,
    TIMELEVEL,TIMELEVELBH,DAYS from dc.SELECT_AGGLEVEL_TABLE where
    TIMELEVELBH = 'DAYBH';

create view dcpublic.DC_E_IMS_PTT_PTTPOCXDMS_RAW
  as select OSS_ID,
    SN,
    IMS_DC,
    g3SubNetwork,
    g3ManagedElement,
    MOID,
    PTT_POCXDMS,
    Source,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pocxdmsGroups,
    pocxdmsXcapRequestsDelete,
    pocxdmsXcapRequestsGet,
    pocxdmsXcapRequestsPut,
    pocxdmsXcapRequestsDeleteAttempts,
    pocxdmsXcapRequestsGetAttempts,
    pocxdmsXcapRequestsPutAttempts from
    dc.DC_E_IMS_PTT_PTTPOCXDMS_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dcpublic.DC_E_CPP_E1TTP_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    Cbu,
    ExchangeTerminal,
    Os155SpiTtp,
    Vc4Ttp,
    Vc12Ttp,
    E1Ttp,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmEs,
    pmSes,
    pmUas from
    dc.DC_E_CPP_E1TTP_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_E3PHYSPATHTERM_RAW as
  select * from dc.DC_E_CPP_E3PHYSPATHTERM_RAW_01 union all
  select * from dc.DC_E_CPP_E3PHYSPATHTERM_RAW_02 union all
  select * from dc.DC_E_CPP_E3PHYSPATHTERM_RAW_03 union all
  select * from dc.DC_E_CPP_E3PHYSPATHTERM_RAW_04 union all
  select * from dc.DC_E_CPP_E3PHYSPATHTERM_RAW_05 union all
  select * from dc.DC_E_CPP_E3PHYSPATHTERM_RAW_06;

create view dcpublic.DC_E_CPP_E3PHYSPATHTERM_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    Cbu,
    ExchangeTerminal,
    E3PhysPathTerm,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmEs,
    pmSes,
    pmUas from
    dc.DC_E_CPP_E3PHYSPATHTERM_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_E3PHYSPATHTERM_DAY as
  select * from dc.DC_E_CPP_E3PHYSPATHTERM_DAY_01 union all
  select * from dc.DC_E_CPP_E3PHYSPATHTERM_DAY_02 union all
  select * from dc.DC_E_CPP_E3PHYSPATHTERM_DAY_03 union all
  select * from dc.DC_E_CPP_E3PHYSPATHTERM_DAY_04 union all
  select * from dc.DC_E_CPP_E3PHYSPATHTERM_DAY_05 union all
  select * from dc.DC_E_CPP_E3PHYSPATHTERM_DAY_06;

create view dcpublic.DC_E_CPP_E3PHYSPATHTERM_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    Cbu,
    ExchangeTerminal,
    E3PhysPathTerm,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmEs,
    pmSes,
    pmUas from
    dc.DC_E_CPP_E3PHYSPATHTERM_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_ETHERNETLINK_RAW as
  select * from dc.DC_E_CPP_ETHERNETLINK_RAW_01 union all
  select * from dc.DC_E_CPP_ETHERNETLINK_RAW_02 union all
  select * from dc.DC_E_CPP_ETHERNETLINK_RAW_03;

create view dcpublic.DC_E_CPP_ETHERNETLINK_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    IpOam,
    IpSystem,
    Ip,
    EthernetLink,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfIfInDiscards,
    pmNoOfIfInErrors,
    pmNoOfIfInNUcastPkts,
    pmNoOfIfInUcastPkts,
    pmNoOfIfOutDiscards,
    pmNoOfIfOutNUcastPkts,
    pmNoOfIfOutUcastPkts from
    dc.DC_E_CPP_ETHERNETLINK_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_ETHERNETLINK_DAY as
  select * from dc.DC_E_CPP_ETHERNETLINK_DAY_01 union all
  select * from dc.DC_E_CPP_ETHERNETLINK_DAY_02 union all
  select * from dc.DC_E_CPP_ETHERNETLINK_DAY_03;

create view dcpublic.DC_E_CPP_ETHERNETLINK_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    IpOam,
    IpSystem,
    Ip,
    EthernetLink,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmNoOfIfInDiscards,
    pmNoOfIfInErrors,
    pmNoOfIfInNUcastPkts,
    pmNoOfIfInUcastPkts,
    pmNoOfIfOutDiscards,
    pmNoOfIfOutNUcastPkts,
    pmNoOfIfOutUcastPkts from
    dc.DC_E_CPP_ETHERNETLINK_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_ETHERNETLINK_COUNT as
  select * from dc.DC_E_CPP_ETHERNETLINK_COUNT_01 union all
  select * from dc.DC_E_CPP_ETHERNETLINK_COUNT_02 union all
  select * from dc.DC_E_CPP_ETHERNETLINK_COUNT_03;

create view dcpublic.DC_E_CPP_ETHERNETLINK_COUNT
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    IpOam,
    IpSystem,
    Ip,
    EthernetLink,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfIfInDiscards,
    pmNoOfIfInErrors,
    pmNoOfIfInNUcastPkts,
    pmNoOfIfInUcastPkts,
    pmNoOfIfOutDiscards,
    pmNoOfIfOutNUcastPkts,
    pmNoOfIfOutUcastPkts from
    dc.DC_E_CPP_ETHERNETLINK_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_FASTETHERNET_RAW as
  select * from dc.DC_E_CPP_FASTETHERNET_RAW_01 union all
  select * from dc.DC_E_CPP_FASTETHERNET_RAW_02 union all
  select * from dc.DC_E_CPP_FASTETHERNET_RAW_03;

create view dcpublic.DC_E_CPP_FASTETHERNET_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    Cbu,
    GeneralProcessorUnit,
    FastEthernet,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmIfInBroadcastPkts,
    pmIfInDiscards,
    pmIfInErrors,
    pmIfInMulticastPkts,
    pmIfInOctetsHi,
    pmIfInOctetsLo,
    pmIfInUcastPkts,
    pmIfInUnknownProtos,
    pmIfOutBroadcastPkts,
    pmIfOutDiscards,
    pmIfOutErrors,
    pmIfOutMulticastPkts,
    pmIfOutOctetsHi,
    pmIfOutOctetsLo,
    pmIfOutUcastPkts from
    dc.DC_E_CPP_FASTETHERNET_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_FASTETHERNET_DAY as
  select * from dc.DC_E_CPP_FASTETHERNET_DAY_01 union all
  select * from dc.DC_E_CPP_FASTETHERNET_DAY_02 union all
  select * from dc.DC_E_CPP_FASTETHERNET_DAY_03;

create view dcpublic.DC_E_CPP_FASTETHERNET_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    Cbu,
    GeneralProcessorUnit,
    FastEthernet,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmIfInBroadcastPkts,
    pmIfInDiscards,
    pmIfInErrors,
    pmIfInMulticastPkts,
    pmIfInOctetsHi,
    pmIfInOctetsLo,
    pmIfInUcastPkts,
    pmIfInUnknownProtos,
    pmIfOutBroadcastPkts,
    pmIfOutDiscards,
    pmIfOutErrors,
    pmIfOutMulticastPkts,
    pmIfOutOctetsHi,
    pmIfOutOctetsLo,
    pmIfOutUcastPkts from
    dc.DC_E_CPP_FASTETHERNET_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_FASTETHERNET_COUNT as
  select * from dc.DC_E_CPP_FASTETHERNET_COUNT_01 union all
  select * from dc.DC_E_CPP_FASTETHERNET_COUNT_02 union all
  select * from dc.DC_E_CPP_FASTETHERNET_COUNT_03;

create view dcpublic.DC_E_CPP_FASTETHERNET_COUNT
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    Cbu,
    GeneralProcessorUnit,
    FastEthernet,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmIfInBroadcastPkts,
    pmIfInDiscards,
    pmIfInErrors,
    pmIfInMulticastPkts,
    pmIfInOctetsHi,
    pmIfInOctetsLo,
    pmIfInUcastPkts,
    pmIfInUnknownProtos,
    pmIfOutBroadcastPkts,
    pmIfOutDiscards,
    pmIfOutErrors,
    pmIfOutMulticastPkts,
    pmIfOutOctetsHi,
    pmIfOutOctetsLo,
    pmIfOutUcastPkts from
    dc.DC_E_CPP_FASTETHERNET_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_GIGABITETHERNET_RAW as
  select * from dc.DC_E_CPP_GIGABITETHERNET_RAW_01 union all
  select * from dc.DC_E_CPP_GIGABITETHERNET_RAW_02 union all
  select * from dc.DC_E_CPP_GIGABITETHERNET_RAW_03;

create view dcpublic.DC_E_CPP_GIGABITETHERNET_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    EtMfg,
    GigaBitEthernet,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmDot1qTpVlanPortInDiscardsLink1,
    pmDot1qTpVlanPortInDiscardsLink2,
    pmIfInBroadcastPktsLink1,
    pmIfInBroadcastPktsLink2,
    pmIfInDiscardsLink1,
    pmIfInDiscardsLink2,
    pmIfInErrorsLink1,
    pmIfInErrorsLink2,
    pmIfInMulticastPktsLink1,
    pmIfInMulticastPktsLink2,
    pmIfInOctetsLink1Hi,
    pmIfInOctetsLink1Lo,
    pmIfInOctetsLink2Hi,
    pmIfInOctetsLink2Lo,
    pmIfInUcastPktsLink1,
    pmIfInUcastPktsLink2,
    pmIfInUnknownProtosLink1,
    pmIfInUnknownProtosLink2,
    pmIfOutBroadcastPktsLink1,
    pmIfOutBroadcastPktsLink2,
    pmIfOutDiscardsLink1,
    pmIfOutDiscardsLink2,
    pmIfOutErrorsLink1,
    pmIfOutErrorsLink2,
    pmIfOutMulticastPktsLink1,
    pmIfOutMulticastPktsLink2,
    pmIfOutOctetsLink1Hi,
    pmIfOutOctetsLink1Lo,
    pmIfOutOctetsLink2Hi,
    pmIfOutOctetsLink2Lo,
    pmIfOutUcastPktsLink1,
    pmIfOutUcastPktsLink2 from
    dc.DC_E_CPP_GIGABITETHERNET_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_GIGABITETHERNET_DAY as
  select * from dc.DC_E_CPP_GIGABITETHERNET_DAY_01 union all
  select * from dc.DC_E_CPP_GIGABITETHERNET_DAY_02 union all
  select * from dc.DC_E_CPP_GIGABITETHERNET_DAY_03;

create view dcpublic.DC_E_CPP_GIGABITETHERNET_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    EtMfg,
    GigaBitEthernet,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmDot1qTpVlanPortInDiscardsLink1,
    pmDot1qTpVlanPortInDiscardsLink2,
    pmIfInBroadcastPktsLink1,
    pmIfInBroadcastPktsLink2,
    pmIfInDiscardsLink1,
    pmIfInDiscardsLink2,
    pmIfInErrorsLink1,
    pmIfInErrorsLink2,
    pmIfInMulticastPktsLink1,
    pmIfInMulticastPktsLink2,
    pmIfInOctetsLink1Hi,
    pmIfInOctetsLink1Lo,
    pmIfInOctetsLink2Hi,
    pmIfInOctetsLink2Lo,
    pmIfInUcastPktsLink1,
    pmIfInUcastPktsLink2,
    pmIfInUnknownProtosLink1,
    pmIfInUnknownProtosLink2,
    pmIfOutBroadcastPktsLink1,
    pmIfOutBroadcastPktsLink2,
    pmIfOutDiscardsLink1,
    pmIfOutDiscardsLink2,
    pmIfOutErrorsLink1,
    pmIfOutErrorsLink2,
    pmIfOutMulticastPktsLink1,
    pmIfOutMulticastPktsLink2,
    pmIfOutOctetsLink1Hi,
    pmIfOutOctetsLink1Lo,
    pmIfOutOctetsLink2Hi,
    pmIfOutOctetsLink2Lo,
    pmIfOutUcastPktsLink1,
    pmIfOutUcastPktsLink2 from
    dc.DC_E_CPP_GIGABITETHERNET_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_GIGABITETHERNET_COUNT as
  select * from dc.DC_E_CPP_GIGABITETHERNET_COUNT_01 union all
  select * from dc.DC_E_CPP_GIGABITETHERNET_COUNT_02 union all
  select * from dc.DC_E_CPP_GIGABITETHERNET_COUNT_03;

create view dcpublic.DC_E_CPP_GIGABITETHERNET_COUNT
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    EtMfg,
    GigaBitEthernet,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmDot1qTpVlanPortInDiscardsLink1,
    pmDot1qTpVlanPortInDiscardsLink2,
    pmIfInBroadcastPktsLink1,
    pmIfInBroadcastPktsLink2,
    pmIfInDiscardsLink1,
    pmIfInDiscardsLink2,
    pmIfInErrorsLink1,
    pmIfInErrorsLink2,
    pmIfInMulticastPktsLink1,
    pmIfInMulticastPktsLink2,
    pmIfInOctetsLink1Hi,
    pmIfInOctetsLink1Lo,
    pmIfInOctetsLink2Hi,
    pmIfInOctetsLink2Lo,
    pmIfInUcastPktsLink1,
    pmIfInUcastPktsLink2,
    pmIfInUnknownProtosLink1,
    pmIfInUnknownProtosLink2,
    pmIfOutBroadcastPktsLink1,
    pmIfOutBroadcastPktsLink2,
    pmIfOutDiscardsLink1,
    pmIfOutDiscardsLink2,
    pmIfOutErrorsLink1,
    pmIfOutErrorsLink2,
    pmIfOutMulticastPktsLink1,
    pmIfOutMulticastPktsLink2,
    pmIfOutOctetsLink1Hi,
    pmIfOutOctetsLink1Lo,
    pmIfOutOctetsLink2Hi,
    pmIfOutOctetsLink2Lo,
    pmIfOutUcastPktsLink1,
    pmIfOutUcastPktsLink2 from
    dc.DC_E_CPP_GIGABITETHERNET_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_IP_DAY as
  select * from dc.DC_E_CPP_IP_DAY_01 union all
  select * from dc.DC_E_CPP_IP_DAY_02 union all
  select * from dc.DC_E_CPP_IP_DAY_03;

create view dcpublic.DC_E_CPP_IP_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    IpOam,
    IpSystem,
    Ip,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmNoOfHdrErrors,
    pmNoOfIpAddrErrors,
    pmNoOfIpForwDatagrams,
    pmNoOfIpInDiscards,
    pmNoOfIpInReceives,
    pmNoOfIpOutDiscards,
    pmNoOfIpReasmOKs,
    pmNoOfIpReasmReqds from
    dc.DC_E_CPP_IP_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_IP_COUNT as
  select * from dc.DC_E_CPP_IP_COUNT_01 union all
  select * from dc.DC_E_CPP_IP_COUNT_02 union all
  select * from dc.DC_E_CPP_IP_COUNT_03;

create view dcpublic.DC_E_CPP_IP_COUNT
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    IpOam,
    IpSystem,
    Ip,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfHdrErrors,
    pmNoOfIpAddrErrors,
    pmNoOfIpForwDatagrams,
    pmNoOfIpInDiscards,
    pmNoOfIpInReceives,
    pmNoOfIpOutDiscards,
    pmNoOfIpReasmOKs,
    pmNoOfIpReasmReqds from
    dc.DC_E_CPP_IP_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_IPACCESSHOSTGPB_RAW as
  select * from dc.DC_E_CPP_IPACCESSHOSTGPB_RAW_01 union all
  select * from dc.DC_E_CPP_IPACCESSHOSTGPB_RAW_02 union all
  select * from dc.DC_E_CPP_IPACCESSHOSTGPB_RAW_03;

create view dcpublic.DC_E_CPP_IPACCESSHOSTGPB_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    IpSystem,
    IpAccessHostGpb,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmIcmpInDestUnreachs,
    pmIcmpInEchoReps,
    pmIcmpInEchos,
    pmIcmpInErrors,
    pmIcmpInMsgs,
    pmIcmpInParamProbs,
    pmIcmpInRedirects,
    pmIcmpInSrcQuenchs,
    pmIcmpInTimeExcds,
    pmIcmpOutDestUnreachs,
    pmIcmpOutEchoReps,
    pmIcmpOutEchos,
    pmIcmpOutErrors,
    pmIcmpOutMsgs,
    pmIcmpOutParmProbs,
    pmIpFragCreates,
    pmIpFragFails,
    pmIpFragOKs,
    pmIpInAddrErrors,
    pmIpInDelivers,
    pmIpInDiscards,
    pmIpInHdrErrors,
    pmIpInReceives,
    pmIpInUnknownProtos,
    pmIpOutDiscards,
    pmIpOutRequests,
    pmIpReasmFails,
    pmIpReasmOKs,
    pmIpReasmReqds,
    pmUdpInDatagrams,
    pmUdpInErrors,
    pmUdpNoPorts,
    pmUdpOutDatagrams from
    dc.DC_E_CPP_IPACCESSHOSTGPB_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_IPACCESSHOSTGPB_DAY as
  select * from dc.DC_E_CPP_IPACCESSHOSTGPB_DAY_01 union all
  select * from dc.DC_E_CPP_IPACCESSHOSTGPB_DAY_02 union all
  select * from dc.DC_E_CPP_IPACCESSHOSTGPB_DAY_03;

create view dcpublic.DC_E_CPP_IPACCESSHOSTGPB_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    IpSystem,
    IpAccessHostGpb,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmIcmpInDestUnreachs,
    pmIcmpInEchoReps,
    pmIcmpInEchos,
    pmIcmpInErrors,
    pmIcmpInMsgs,
    pmIcmpInParamProbs,
    pmIcmpInRedirects,
    pmIcmpInSrcQuenchs,
    pmIcmpInTimeExcds,
    pmIcmpOutDestUnreachs,
    pmIcmpOutEchoReps,
    pmIcmpOutEchos,
    pmIcmpOutErrors,
    pmIcmpOutMsgs,
    pmIcmpOutParmProbs,
    pmIpFragCreates,
    pmIpFragFails,
    pmIpFragOKs,
    pmIpInAddrErrors,
    pmIpInDelivers,
    pmIpInDiscards,
    pmIpInHdrErrors,
    pmIpInReceives,
    pmIpInUnknownProtos,
    pmIpOutDiscards,
    pmIpOutRequests,
    pmIpReasmFails,
    pmIpReasmOKs,
    pmIpReasmReqds,
    pmUdpInDatagrams,
    pmUdpInErrors,
    pmUdpNoPorts,
    pmUdpOutDatagrams from
    dc.DC_E_CPP_IPACCESSHOSTGPB_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_IPACCESSHOSTGPB_COUNT as
  select * from dc.DC_E_CPP_IPACCESSHOSTGPB_COUNT_01 union all
  select * from dc.DC_E_CPP_IPACCESSHOSTGPB_COUNT_02 union all
  select * from dc.DC_E_CPP_IPACCESSHOSTGPB_COUNT_03;

create view dcpublic.DC_E_CPP_IPACCESSHOSTGPB_COUNT
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    IpSystem,
    IpAccessHostGpb,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmIcmpInDestUnreachs,
    pmIcmpInEchoReps,
    pmIcmpInEchos,
    pmIcmpInErrors,
    pmIcmpInMsgs,
    pmIcmpInParamProbs,
    pmIcmpInRedirects,
    pmIcmpInSrcQuenchs,
    pmIcmpInTimeExcds,
    pmIcmpOutDestUnreachs,
    pmIcmpOutEchoReps,
    pmIcmpOutEchos,
    pmIcmpOutErrors,
    pmIcmpOutMsgs,
    pmIcmpOutParmProbs,
    pmIpFragCreates,
    pmIpFragFails,
    pmIpFragOKs,
    pmIpInAddrErrors,
    pmIpInDelivers,
    pmIpInDiscards,
    pmIpInHdrErrors,
    pmIpInReceives,
    pmIpInUnknownProtos,
    pmIpOutDiscards,
    pmIpOutRequests,
    pmIpReasmFails,
    pmIpReasmOKs,
    pmIpReasmReqds,
    pmUdpInDatagrams,
    pmUdpInErrors,
    pmUdpNoPorts,
    pmUdpOutDatagrams from
    dc.DC_E_CPP_IPACCESSHOSTGPB_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_IPACCESSHOSTSPB_RAW as
  select * from dc.DC_E_CPP_IPACCESSHOSTSPB_RAW_01 union all
  select * from dc.DC_E_CPP_IPACCESSHOSTSPB_RAW_02 union all
  select * from dc.DC_E_CPP_IPACCESSHOSTSPB_RAW_03;

create view dcpublic.DC_E_CPP_IPACCESSHOSTSPB_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    IpSystem,
    IpAccessHostSpb,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmIcmpInDestUnreachs,
    pmIcmpInEchoReps,
    pmIcmpInEchos,
    pmIcmpInErrors,
    pmIcmpInMsgs,
    pmIcmpInParamProbs,
    pmIcmpInRedirects,
    pmIcmpInSrcQuenchs,
    pmIcmpInTimeExcds,
    pmIcmpOutDestUnreachs,
    pmIcmpOutEchoReps,
    pmIcmpOutEchos,
    pmIcmpOutErrors,
    pmIcmpOutMsgs,
    pmIcmpOutParmProbs,
    pmIpFragCreates,
    pmIpFragFails,
    pmIpFragOKs,
    pmIpInAddrErrors,
    pmIpInDelivers,
    pmIpInDiscards,
    pmIpInHdrErrors,
    pmIpInReceives,
    pmIpInUnknownProtos,
    pmIpOutDiscards,
    pmIpOutRequests,
    pmIpReasmFails,
    pmIpReasmOKs,
    pmIpReasmReqds,
    pmUdpInDatagrams,
    pmUdpInErrors,
    pmUdpNoPorts,
    pmUdpOutDatagrams from
    dc.DC_E_CPP_IPACCESSHOSTSPB_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_IPACCESSHOSTSPB_DAY as
  select * from dc.DC_E_CPP_IPACCESSHOSTSPB_DAY_01 union all
  select * from dc.DC_E_CPP_IPACCESSHOSTSPB_DAY_02 union all
  select * from dc.DC_E_CPP_IPACCESSHOSTSPB_DAY_03;

create view dcpublic.DC_E_CPP_IPACCESSHOSTSPB_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    IpSystem,
    IpAccessHostSpb,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmIcmpInDestUnreachs,
    pmIcmpInEchoReps,
    pmIcmpInEchos,
    pmIcmpInErrors,
    pmIcmpInMsgs,
    pmIcmpInParamProbs,
    pmIcmpInRedirects,
    pmIcmpInSrcQuenchs,
    pmIcmpInTimeExcds,
    pmIcmpOutDestUnreachs,
    pmIcmpOutEchoReps,
    pmIcmpOutEchos,
    pmIcmpOutErrors,
    pmIcmpOutMsgs,
    pmIcmpOutParmProbs,
    pmIpFragCreates,
    pmIpFragFails,
    pmIpFragOKs,
    pmIpInAddrErrors,
    pmIpInDelivers,
    pmIpInDiscards,
    pmIpInHdrErrors,
    pmIpInReceives,
    pmIpInUnknownProtos,
    pmIpOutDiscards,
    pmIpOutRequests,
    pmIpReasmFails,
    pmIpReasmOKs,
    pmIpReasmReqds,
    pmUdpInDatagrams,
    pmUdpInErrors,
    pmUdpNoPorts,
    pmUdpOutDatagrams from
    dc.DC_E_CPP_IPACCESSHOSTSPB_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_IPACCESSHOSTSPB_COUNT as
  select * from dc.DC_E_CPP_IPACCESSHOSTSPB_COUNT_01 union all
  select * from dc.DC_E_CPP_IPACCESSHOSTSPB_COUNT_02 union all
  select * from dc.DC_E_CPP_IPACCESSHOSTSPB_COUNT_03;

create view dcpublic.DC_E_CPP_IPACCESSHOSTSPB_COUNT
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    IpSystem,
    IpAccessHostSpb,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmIcmpInDestUnreachs,
    pmIcmpInEchoReps,
    pmIcmpInEchos,
    pmIcmpInErrors,
    pmIcmpInMsgs,
    pmIcmpInParamProbs,
    pmIcmpInRedirects,
    pmIcmpInSrcQuenchs,
    pmIcmpInTimeExcds,
    pmIcmpOutDestUnreachs,
    pmIcmpOutEchoReps,
    pmIcmpOutEchos,
    pmIcmpOutErrors,
    pmIcmpOutMsgs,
    pmIcmpOutParmProbs,
    pmIpFragCreates,
    pmIpFragFails,
    pmIpFragOKs,
    pmIpInAddrErrors,
    pmIpInDelivers,
    pmIpInDiscards,
    pmIpInHdrErrors,
    pmIpInReceives,
    pmIpInUnknownProtos,
    pmIpOutDiscards,
    pmIpOutRequests,
    pmIpReasmFails,
    pmIpReasmOKs,
    pmIpReasmReqds,
    pmUdpInDatagrams,
    pmUdpInErrors,
    pmUdpNoPorts,
    pmUdpOutDatagrams from
    dc.DC_E_CPP_IPACCESSHOSTSPB_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_IPACCEUDPHOSTMSB_RAW as
  select * from dc.DC_E_CPP_IPACCEUDPHOSTMSB_RAW_01 union all
  select * from dc.DC_E_CPP_IPACCEUDPHOSTMSB_RAW_02 union all
  select * from dc.DC_E_CPP_IPACCEUDPHOSTMSB_RAW_03;

create view dcpublic.DC_E_CPP_IPACCEUDPHOSTMSB_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    IpSystem,
    UdpHostMainMsb,
    IpAccessUdpHostMsb,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmIcmpInDestUnreachs,
    pmIcmpInErrors,
    pmIcmpInMsgs,
    pmIcmpOutDestUnreachs,
    pmIcmpOutMsgs,
    pmIpInAddrErrors,
    pmIpInDelivers,
    pmIpInDiscards,
    pmIpInHdrErrors,
    pmIpInReceives,
    pmIpInUnknownProtos,
    pmIpOutDiscards,
    pmIpOutRequests,
    pmUdpInDatagrams,
    pmUdpInErrors,
    pmUdpNoPorts,
    pmUdpOutDatagrams from
    dc.DC_E_CPP_IPACCEUDPHOSTMSB_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_IPACCEUDPHOSTMSB_DAY as
  select * from dc.DC_E_CPP_IPACCEUDPHOSTMSB_DAY_01 union all
  select * from dc.DC_E_CPP_IPACCEUDPHOSTMSB_DAY_02 union all
  select * from dc.DC_E_CPP_IPACCEUDPHOSTMSB_DAY_03;

create view dcpublic.DC_E_CPP_IPACCEUDPHOSTMSB_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    IpSystem,
    UdpHostMainMsb,
    IpAccessUdpHostMsb,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmIcmpInDestUnreachs,
    pmIcmpInErrors,
    pmIcmpInMsgs,
    pmIcmpOutDestUnreachs,
    pmIcmpOutMsgs,
    pmIpInAddrErrors,
    pmIpInDelivers,
    pmIpInDiscards,
    pmIpInHdrErrors,
    pmIpInReceives,
    pmIpInUnknownProtos,
    pmIpOutDiscards,
    pmIpOutRequests,
    pmUdpInDatagrams,
    pmUdpInErrors,
    pmUdpNoPorts,
    pmUdpOutDatagrams from
    dc.DC_E_CPP_IPACCEUDPHOSTMSB_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_IPACCEUDPHOSTMSB_COUNT as
  select * from dc.DC_E_CPP_IPACCEUDPHOSTMSB_COUNT_01 union all
  select * from dc.DC_E_CPP_IPACCEUDPHOSTMSB_COUNT_02 union all
  select * from dc.DC_E_CPP_IPACCEUDPHOSTMSB_COUNT_03;

create view dcpublic.DC_E_CPP_IPACCEUDPHOSTMSB_COUNT
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    IpSystem,
    UdpHostMainMsb,
    IpAccessUdpHostMsb,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmIcmpInDestUnreachs,
    pmIcmpInErrors,
    pmIcmpInMsgs,
    pmIcmpOutDestUnreachs,
    pmIcmpOutMsgs,
    pmIpInAddrErrors,
    pmIpInDelivers,
    pmIpInDiscards,
    pmIpInHdrErrors,
    pmIpInReceives,
    pmIpInUnknownProtos,
    pmIpOutDiscards,
    pmIpOutRequests,
    pmUdpInDatagrams,
    pmUdpInErrors,
    pmUdpNoPorts,
    pmUdpOutDatagrams from
    dc.DC_E_CPP_IPACCEUDPHOSTMSB_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_IPATMLINK_RAW as
  select * from dc.DC_E_CPP_IPATMLINK_RAW_01 union all
  select * from dc.DC_E_CPP_IPATMLINK_RAW_02 union all
  select * from dc.DC_E_CPP_IPATMLINK_RAW_03 union all
  select * from dc.DC_E_CPP_IPATMLINK_RAW_04 union all
  select * from dc.DC_E_CPP_IPATMLINK_RAW_05 union all
  select * from dc.DC_E_CPP_IPATMLINK_RAW_06;

create view dcpublic.DC_E_CPP_IPATMLINK_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    IpOam,
    IpSystem,
    Ip,
    IpAtmLink,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfIfInDiscards,
    pmNoOfIfInErrors,
    pmNoOfIfInNUcastPkts,
    pmNoOfIfInUcastPkts,
    pmNoOfIfOutDiscards,
    pmNoOfIfOutNUcastPkts,
    pmNoOfIfOutUcastPkts from
    dc.DC_E_CPP_IPATMLINK_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_IPATMLINK_DAY as
  select * from dc.DC_E_CPP_IPATMLINK_DAY_01 union all
  select * from dc.DC_E_CPP_IPATMLINK_DAY_02 union all
  select * from dc.DC_E_CPP_IPATMLINK_DAY_03 union all
  select * from dc.DC_E_CPP_IPATMLINK_DAY_04 union all
  select * from dc.DC_E_CPP_IPATMLINK_DAY_05 union all
  select * from dc.DC_E_CPP_IPATMLINK_DAY_06;

create view dcpublic.DC_E_CPP_IPATMLINK_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    IpOam,
    IpSystem,
    Ip,
    IpAtmLink,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmNoOfIfInDiscards,
    pmNoOfIfInErrors,
    pmNoOfIfInNUcastPkts,
    pmNoOfIfInUcastPkts,
    pmNoOfIfOutDiscards,
    pmNoOfIfOutNUcastPkts,
    pmNoOfIfOutUcastPkts from
    dc.DC_E_CPP_IPATMLINK_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_IPATMLINK_COUNT as
  select * from dc.DC_E_CPP_IPATMLINK_COUNT_01 union all
  select * from dc.DC_E_CPP_IPATMLINK_COUNT_02 union all
  select * from dc.DC_E_CPP_IPATMLINK_COUNT_03 union all
  select * from dc.DC_E_CPP_IPATMLINK_COUNT_04 union all
  select * from dc.DC_E_CPP_IPATMLINK_COUNT_05 union all
  select * from dc.DC_E_CPP_IPATMLINK_COUNT_06;

create view dcpublic.DC_E_CPP_IPATMLINK_COUNT
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    IpOam,
    IpSystem,
    Ip,
    IpAtmLink,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfIfInDiscards,
    pmNoOfIfInErrors,
    pmNoOfIfInNUcastPkts,
    pmNoOfIfInUcastPkts,
    pmNoOfIfOutDiscards,
    pmNoOfIfOutNUcastPkts,
    pmNoOfIfOutUcastPkts from
    dc.DC_E_CPP_IPATMLINK_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_IPINTERFACE_RAW as
  select * from dc.DC_E_CPP_IPINTERFACE_RAW_01 union all
  select * from dc.DC_E_CPP_IPINTERFACE_RAW_02 union all
  select * from dc.DC_E_CPP_IPINTERFACE_RAW_03;

create view dcpublic.DC_E_CPP_IPINTERFACE_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    EtMfg,
    GigaBitEthernet,
    IpInterface,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmDot1qTpVlanPortInFrames,
    pmDot1qTpVlanPortOutFrames,
    pmIfStatsIpAddrErrors,
    pmIfStatsIpInDiscards,
    pmIfStatsIpInHdrErrors,
    pmIfStatsIpInReceives,
    pmIfStatsIpOutDiscards,
    pmIfStatsIpOutRequests,
    pmIfStatsIpUnknownProtos,
    pmFramesExcTrafDsc,
    pmNoOfFailedPingsDefaultRouter0,
    pmNoOfFailedPingsDefaultRouter1,
    pmNoOfFailedPingsDefaultRouter2,
    pmOctetsExcTrafDsc from
    dc.DC_E_CPP_IPINTERFACE_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_IPINTERFACE_DAY as
  select * from dc.DC_E_CPP_IPINTERFACE_DAY_01 union all
  select * from dc.DC_E_CPP_IPINTERFACE_DAY_02 union all
  select * from dc.DC_E_CPP_IPINTERFACE_DAY_03;

create view dcpublic.DC_E_CPP_IPINTERFACE_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    EtMfg,
    GigaBitEthernet,
    IpInterface,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmDot1qTpVlanPortInFrames,
    pmDot1qTpVlanPortOutFrames,
    pmIfStatsIpAddrErrors,
    pmIfStatsIpInDiscards,
    pmIfStatsIpInHdrErrors,
    pmIfStatsIpInReceives,
    pmIfStatsIpOutDiscards,
    pmIfStatsIpOutRequests,
    pmIfStatsIpUnknownProtos,
    pmFramesExcTrafDsc,
    pmNoOfFailedPingsDefaultRouter0,
    pmNoOfFailedPingsDefaultRouter1,
    pmNoOfFailedPingsDefaultRouter2,
    pmOctetsExcTrafDsc from
    dc.DC_E_CPP_IPINTERFACE_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_IPINTERFACE_COUNT as
  select * from dc.DC_E_CPP_IPINTERFACE_COUNT_01 union all
  select * from dc.DC_E_CPP_IPINTERFACE_COUNT_02 union all
  select * from dc.DC_E_CPP_IPINTERFACE_COUNT_03;

create view dcpublic.DC_E_CPP_IPINTERFACE_COUNT
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    EtMfg,
    GigaBitEthernet,
    IpInterface,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmDot1qTpVlanPortInFrames,
    pmDot1qTpVlanPortOutFrames,
    pmIfStatsIpAddrErrors,
    pmIfStatsIpInDiscards,
    pmIfStatsIpInHdrErrors,
    pmIfStatsIpInReceives,
    pmIfStatsIpOutDiscards,
    pmIfStatsIpOutRequests,
    pmIfStatsIpUnknownProtos,
    pmFramesExcTrafDsc,
    pmNoOfFailedPingsDefaultRouter0,
    pmNoOfFailedPingsDefaultRouter1,
    pmNoOfFailedPingsDefaultRouter2,
    pmOctetsExcTrafDsc from
    dc.DC_E_CPP_IPINTERFACE_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_J1PHYSPATHTERM_RAW as
  select * from dc.DC_E_CPP_J1PHYSPATHTERM_RAW_01 union all
  select * from dc.DC_E_CPP_J1PHYSPATHTERM_RAW_02 union all
  select * from dc.DC_E_CPP_J1PHYSPATHTERM_RAW_03 union all
  select * from dc.DC_E_CPP_J1PHYSPATHTERM_RAW_04 union all
  select * from dc.DC_E_CPP_J1PHYSPATHTERM_RAW_05 union all
  select * from dc.DC_E_CPP_J1PHYSPATHTERM_RAW_06;

create view dcpublic.DC_E_CPP_J1PHYSPATHTERM_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    Cbu,
    ExchangeTerminal,
    J1PhysPathTerm,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmEs,
    pmSes,
    pmUas from
    dc.DC_E_CPP_J1PHYSPATHTERM_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_J1PHYSPATHTERM_DAY as
  select * from dc.DC_E_CPP_J1PHYSPATHTERM_DAY_01 union all
  select * from dc.DC_E_CPP_J1PHYSPATHTERM_DAY_02 union all
  select * from dc.DC_E_CPP_J1PHYSPATHTERM_DAY_03 union all
  select * from dc.DC_E_CPP_J1PHYSPATHTERM_DAY_04 union all
  select * from dc.DC_E_CPP_J1PHYSPATHTERM_DAY_05 union all
  select * from dc.DC_E_CPP_J1PHYSPATHTERM_DAY_06;

create view dcpublic.DC_E_CPP_J1PHYSPATHTERM_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    Cbu,
    ExchangeTerminal,
    J1PhysPathTerm,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmEs,
    pmSes,
    pmUas from
    dc.DC_E_CPP_J1PHYSPATHTERM_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_M3UASSOCIATION_RAW as
  select * from dc.DC_E_CPP_M3UASSOCIATION_RAW_01 union all
  select * from dc.DC_E_CPP_M3UASSOCIATION_RAW_02 union all
  select * from dc.DC_E_CPP_M3UASSOCIATION_RAW_03;

create view dcpublic.DC_E_CPP_M3UASSOCIATION_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp3bSp,
    Mtp3bSpType,
    M3uAssociation,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfAspacAckReceived,
    pmNoOfAspacAckSent,
    pmNoOfAspacReceived,
    pmNoOfAspacSent,
    pmNoOfAspdnAckReceived,
    pmNoOfAspdnAckSent,
    pmNoOfAspdnReceived,
    pmNoOfAspdnSent,
    pmNoOfAspiaAckReceived,
    pmNoOfAspiaAckSent,
    pmNoOfAspiaReceived,
    pmNoOfAspiaSent,
    pmNoOfAspupAckReceived,
    pmNoOfAspupAckSent,
    pmNoOfAspupReceived,
    pmNoOfAspupSent,
    pmNoOfCommunicationLost,
    pmNoOfCongestions,
    pmNoOfDataMsgRec,
    pmNoOfDataMsgSent,
    pmNoOfDaudMsgRec,
    pmNoOfDaudMsgSent,
    pmNoOfDavaRec,
    pmNoOfDavaSent,
    pmNoOfDunaRec,
    pmNoOfDunaSent,
    pmNoOfDupuRec,
    pmNoOfDupuSent,
    pmNoOfErrorMsgRec,
    pmNoOfErrorMsgSent,
    pmNoOfM3uaDataMsgDiscarded,
    pmNoOfNotifyMsgRec,
    pmNoOfSconRec,
    pmNoOfSconSent,
    pmNoOfRecUserData,
    pmNoOfSentUserData from
    dc.DC_E_CPP_M3UASSOCIATION_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_M3UASSOCIATION_DAY as
  select * from dc.DC_E_CPP_M3UASSOCIATION_DAY_01 union all
  select * from dc.DC_E_CPP_M3UASSOCIATION_DAY_02 union all
  select * from dc.DC_E_CPP_M3UASSOCIATION_DAY_03;

create view dcpublic.DC_E_CPP_M3UASSOCIATION_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp3bSp,
    Mtp3bSpType,
    M3uAssociation,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmNoOfAspacAckReceived,
    pmNoOfAspacAckSent,
    pmNoOfAspacReceived,
    pmNoOfAspacSent,
    pmNoOfAspdnAckReceived,
    pmNoOfAspdnAckSent,
    pmNoOfAspdnReceived,
    pmNoOfAspdnSent,
    pmNoOfAspiaAckReceived,
    pmNoOfAspiaAckSent,
    pmNoOfAspiaReceived,
    pmNoOfAspiaSent,
    pmNoOfAspupAckReceived,
    pmNoOfAspupAckSent,
    pmNoOfAspupReceived,
    pmNoOfAspupSent,
    pmNoOfCommunicationLost,
    pmNoOfCongestions,
    pmNoOfDataMsgRec,
    pmNoOfDataMsgSent,
    pmNoOfDaudMsgRec,
    pmNoOfDaudMsgSent,
    pmNoOfDavaRec,
    pmNoOfDavaSent,
    pmNoOfDunaRec,
    pmNoOfDunaSent,
    pmNoOfDupuRec,
    pmNoOfDupuSent,
    pmNoOfErrorMsgRec,
    pmNoOfErrorMsgSent,
    pmNoOfM3uaDataMsgDiscarded,
    pmNoOfNotifyMsgRec,
    pmNoOfSconRec,
    pmNoOfSconSent,
    pmNoOfRecUserData,
    pmNoOfSentUserData from
    dc.DC_E_CPP_M3UASSOCIATION_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_M3UASSOCIATION_COUNT as
  select * from dc.DC_E_CPP_M3UASSOCIATION_COUNT_01 union all
  select * from dc.DC_E_CPP_M3UASSOCIATION_COUNT_02 union all
  select * from dc.DC_E_CPP_M3UASSOCIATION_COUNT_03;

create view dcpublic.DC_E_CPP_M3UASSOCIATION_COUNT
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp3bSp,
    Mtp3bSpType,
    M3uAssociation,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfAspacAckReceived,
    pmNoOfAspacAckSent,
    pmNoOfAspacReceived,
    pmNoOfAspacSent,
    pmNoOfAspdnAckReceived,
    pmNoOfAspdnAckSent,
    pmNoOfAspdnReceived,
    pmNoOfAspdnSent,
    pmNoOfAspiaAckReceived,
    pmNoOfAspiaAckSent,
    pmNoOfAspiaReceived,
    pmNoOfAspiaSent,
    pmNoOfAspupAckReceived,
    pmNoOfAspupAckSent,
    pmNoOfAspupReceived,
    pmNoOfAspupSent,
    pmNoOfCommunicationLost,
    pmNoOfCongestions,
    pmNoOfDataMsgRec,
    pmNoOfDataMsgSent,
    pmNoOfDaudMsgRec,
    pmNoOfDaudMsgSent,
    pmNoOfDavaRec,
    pmNoOfDavaSent,
    pmNoOfDunaRec,
    pmNoOfDunaSent,
    pmNoOfDupuRec,
    pmNoOfDupuSent,
    pmNoOfErrorMsgRec,
    pmNoOfErrorMsgSent,
    pmNoOfM3uaDataMsgDiscarded,
    pmNoOfNotifyMsgRec,
    pmNoOfSconRec,
    pmNoOfSconSent,
    pmNoOfRecUserData,
    pmNoOfSentUserData from
    dc.DC_E_CPP_M3UASSOCIATION_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MEDIUMACCESSUNIT_RAW as
  select * from dc.DC_E_CPP_MEDIUMACCESSUNIT_RAW_01 union all
  select * from dc.DC_E_CPP_MEDIUMACCESSUNIT_RAW_02 union all
  select * from dc.DC_E_CPP_MEDIUMACCESSUNIT_RAW_03;

create view dcpublic.DC_E_CPP_MEDIUMACCESSUNIT_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    Cbu,
    GeneralProcessorUnit,
    MediumAccessUnit,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfDot3StatsFCSErrors,
    pmNoOfDot3StatsLateCollisions from
    dc.DC_E_CPP_MEDIUMACCESSUNIT_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MEDIUMACCESSUNIT_DAY as
  select * from dc.DC_E_CPP_MEDIUMACCESSUNIT_DAY_01 union all
  select * from dc.DC_E_CPP_MEDIUMACCESSUNIT_DAY_02 union all
  select * from dc.DC_E_CPP_MEDIUMACCESSUNIT_DAY_03;

create view dcpublic.DC_E_CPP_MEDIUMACCESSUNIT_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    Cbu,
    GeneralProcessorUnit,
    MediumAccessUnit,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmNoOfDot3StatsFCSErrors,
    pmNoOfDot3StatsLateCollisions from
    dc.DC_E_CPP_MEDIUMACCESSUNIT_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MEDIUMACCESSUNIT_COUNT as
  select * from dc.DC_E_CPP_MEDIUMACCESSUNIT_COUNT_01 union all
  select * from dc.DC_E_CPP_MEDIUMACCESSUNIT_COUNT_02 union all
  select * from dc.DC_E_CPP_MEDIUMACCESSUNIT_COUNT_03;

create view dcpublic.DC_E_CPP_MEDIUMACCESSUNIT_COUNT
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    Cbu,
    GeneralProcessorUnit,
    MediumAccessUnit,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfDot3StatsFCSErrors,
    pmNoOfDot3StatsLateCollisions from
    dc.DC_E_CPP_MEDIUMACCESSUNIT_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MTP2TPANSI_RAW as
  select * from dc.DC_E_CPP_MTP2TPANSI_RAW_01 union all
  select * from dc.DC_E_CPP_MTP2TPANSI_RAW_02 union all
  select * from dc.DC_E_CPP_MTP2TPANSI_RAW_03;

create view dcpublic.DC_E_CPP_MTP2TPANSI_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp2TpAnsi,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmLocalSIBTime,
    pmNoOfMSUReceived,
    pmNoOfMSUTransmitted,
    pmNoOfNacks,
    pmNoOfReTransmittedOctets,
    pmNoOfSendBufferOctets,
    pmNoOfSIOSIFReceived,
    pmNoOfSIOSIFTransmitted,
    pmNoOfStartedRBCongestion,
    pmNoOfSuReceivedInError,
    pmRemoteSIBTime from
    dc.DC_E_CPP_MTP2TPANSI_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MTP2TPANSI_DAY as
  select * from dc.DC_E_CPP_MTP2TPANSI_DAY_01 union all
  select * from dc.DC_E_CPP_MTP2TPANSI_DAY_02 union all
  select * from dc.DC_E_CPP_MTP2TPANSI_DAY_03;

create view dcpublic.DC_E_CPP_MTP2TPANSI_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp2TpAnsi,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmLocalSIBTime,
    pmNoOfMSUReceived,
    pmNoOfMSUTransmitted,
    pmNoOfNacks,
    pmNoOfReTransmittedOctets,
    pmNoOfSendBufferOctets,
    pmNoOfSIOSIFReceived,
    pmNoOfSIOSIFTransmitted,
    pmNoOfStartedRBCongestion,
    pmNoOfSuReceivedInError,
    pmRemoteSIBTime from
    dc.DC_E_CPP_MTP2TPANSI_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MTP2TPANSI_COUNT as
  select * from dc.DC_E_CPP_MTP2TPANSI_COUNT_01 union all
  select * from dc.DC_E_CPP_MTP2TPANSI_COUNT_02 union all
  select * from dc.DC_E_CPP_MTP2TPANSI_COUNT_03;

create view dcpublic.DC_E_CPP_MTP2TPANSI_COUNT
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp2TpAnsi,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmLocalSIBTime,
    pmNoOfMSUReceived,
    pmNoOfMSUTransmitted,
    pmNoOfNacks,
    pmNoOfReTransmittedOctets,
    pmNoOfSendBufferOctets,
    pmNoOfSIOSIFReceived,
    pmNoOfSIOSIFTransmitted,
    pmNoOfStartedRBCongestion,
    pmNoOfSuReceivedInError,
    pmRemoteSIBTime from
    dc.DC_E_CPP_MTP2TPANSI_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MTP2TPCHINA_RAW as
  select * from dc.DC_E_CPP_MTP2TPCHINA_RAW_01 union all
  select * from dc.DC_E_CPP_MTP2TPCHINA_RAW_02 union all
  select * from dc.DC_E_CPP_MTP2TPCHINA_RAW_03;

create view dcpublic.DC_E_CPP_MTP2TPCHINA_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp2TpChina,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmLocalSIBTime,
    pmNoOfMSUReceived,
    pmNoOfMSUTransmitted,
    pmNoOfNacks,
    pmNoOfReTransmittedOctets,
    pmNoOfSendBufferOctets,
    pmNoOfSIOSIFReceived,
    pmNoOfSIOSIFTransmitted,
    pmNoOfStartedRBCongestion,
    pmNoOfSuReceivedInError,
    pmRemoteSIBTime from
    dc.DC_E_CPP_MTP2TPCHINA_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MTP2TPCHINA_DAY as
  select * from dc.DC_E_CPP_MTP2TPCHINA_DAY_01 union all
  select * from dc.DC_E_CPP_MTP2TPCHINA_DAY_02 union all
  select * from dc.DC_E_CPP_MTP2TPCHINA_DAY_03;

create view dcpublic.DC_E_CPP_MTP2TPCHINA_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp2TpChina,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmLocalSIBTime,
    pmNoOfMSUReceived,
    pmNoOfMSUTransmitted,
    pmNoOfNacks,
    pmNoOfReTransmittedOctets,
    pmNoOfSendBufferOctets,
    pmNoOfSIOSIFReceived,
    pmNoOfSIOSIFTransmitted,
    pmNoOfStartedRBCongestion,
    pmNoOfSuReceivedInError,
    pmRemoteSIBTime from
    dc.DC_E_CPP_MTP2TPCHINA_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MTP2TPCHINA_COUNT as
  select * from dc.DC_E_CPP_MTP2TPCHINA_COUNT_01 union all
  select * from dc.DC_E_CPP_MTP2TPCHINA_COUNT_02 union all
  select * from dc.DC_E_CPP_MTP2TPCHINA_COUNT_03;

create view dcpublic.DC_E_CPP_MTP2TPCHINA_COUNT
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp2TpChina,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmLocalSIBTime,
    pmNoOfMSUReceived,
    pmNoOfMSUTransmitted,
    pmNoOfNacks,
    pmNoOfReTransmittedOctets,
    pmNoOfSendBufferOctets,
    pmNoOfSIOSIFReceived,
    pmNoOfSIOSIFTransmitted,
    pmNoOfStartedRBCongestion,
    pmNoOfSuReceivedInError,
    pmRemoteSIBTime from
    dc.DC_E_CPP_MTP2TPCHINA_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MTP2TPITU_RAW as
  select * from dc.DC_E_CPP_MTP2TPITU_RAW_01 union all
  select * from dc.DC_E_CPP_MTP2TPITU_RAW_02 union all
  select * from dc.DC_E_CPP_MTP2TPITU_RAW_03;

create view dcpublic.DC_E_CPP_MTP2TPITU_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp2TpItu,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmLocalSIBTime,
    pmNoOfMSUReceived,
    pmNoOfMSUTransmitted,
    pmNoOfNacks,
    pmNoOfReTransmittedOctets,
    pmNoOfSendBufferOctets,
    pmNoOfSIOSIFReceived,
    pmNoOfSIOSIFTransmitted,
    pmNoOfStartedRBCongestion,
    pmNoOfSuReceivedInError,
    pmRemoteSIBTime from
    dc.DC_E_CPP_MTP2TPITU_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MTP2TPITU_DAY as
  select * from dc.DC_E_CPP_MTP2TPITU_DAY_01 union all
  select * from dc.DC_E_CPP_MTP2TPITU_DAY_02 union all
  select * from dc.DC_E_CPP_MTP2TPITU_DAY_03;

create view dcpublic.DC_E_CPP_MTP2TPITU_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp2TpItu,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmLocalSIBTime,
    pmNoOfMSUReceived,
    pmNoOfMSUTransmitted,
    pmNoOfNacks,
    pmNoOfReTransmittedOctets,
    pmNoOfSendBufferOctets,
    pmNoOfSIOSIFReceived,
    pmNoOfSIOSIFTransmitted,
    pmNoOfStartedRBCongestion,
    pmNoOfSuReceivedInError,
    pmRemoteSIBTime from
    dc.DC_E_CPP_MTP2TPITU_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MTP2TPITU_COUNT as
  select * from dc.DC_E_CPP_MTP2TPITU_COUNT_01 union all
  select * from dc.DC_E_CPP_MTP2TPITU_COUNT_02 union all
  select * from dc.DC_E_CPP_MTP2TPITU_COUNT_03;

create view dcpublic.DC_E_CPP_MTP2TPITU_COUNT
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp2TpItu,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmLocalSIBTime,
    pmNoOfMSUReceived,
    pmNoOfMSUTransmitted,
    pmNoOfNacks,
    pmNoOfReTransmittedOctets,
    pmNoOfSendBufferOctets,
    pmNoOfSIOSIFReceived,
    pmNoOfSIOSIFTransmitted,
    pmNoOfStartedRBCongestion,
    pmNoOfSuReceivedInError,
    pmRemoteSIBTime from
    dc.DC_E_CPP_MTP2TPITU_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MTP2TPTTC_RAW as
  select * from dc.DC_E_CPP_MTP2TPTTC_RAW_01 union all
  select * from dc.DC_E_CPP_MTP2TPTTC_RAW_02 union all
  select * from dc.DC_E_CPP_MTP2TPTTC_RAW_03 union all
  select * from dc.DC_E_CPP_MTP2TPTTC_RAW_04 union all
  select * from dc.DC_E_CPP_MTP2TPTTC_RAW_05 union all
  select * from dc.DC_E_CPP_MTP2TPTTC_RAW_06;

create view dcpublic.DC_E_CPP_MTP2TPTTC_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp2TpTtc,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmLocalSIBTime,
    pmNoOfMSUReceived,
    pmNoOfMSUTransmitted,
    pmNoOfNacks,
    pmNoOfReTransmittedOctets,
    pmNoOfSendBufferOctets,
    pmNoOfSIOSIFReceived,
    pmNoOfSIOSIFTransmitted,
    pmNoOfStartedRBCongestion,
    pmNoOfSuReceivedInError,
    pmRemoteSIBTime from
    dc.DC_E_CPP_MTP2TPTTC_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MTP2TPTTC_DAY as
  select * from dc.DC_E_CPP_MTP2TPTTC_DAY_01 union all
  select * from dc.DC_E_CPP_MTP2TPTTC_DAY_02 union all
  select * from dc.DC_E_CPP_MTP2TPTTC_DAY_03 union all
  select * from dc.DC_E_CPP_MTP2TPTTC_DAY_04 union all
  select * from dc.DC_E_CPP_MTP2TPTTC_DAY_05 union all
  select * from dc.DC_E_CPP_MTP2TPTTC_DAY_06;

create view dcpublic.DC_E_CPP_MTP2TPTTC_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp2TpTtc,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmLocalSIBTime,
    pmNoOfMSUReceived,
    pmNoOfMSUTransmitted,
    pmNoOfNacks,
    pmNoOfReTransmittedOctets,
    pmNoOfSendBufferOctets,
    pmNoOfSIOSIFReceived,
    pmNoOfSIOSIFTransmitted,
    pmNoOfStartedRBCongestion,
    pmNoOfSuReceivedInError,
    pmRemoteSIBTime from
    dc.DC_E_CPP_MTP2TPTTC_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MTP2TPTTC_COUNT as
  select * from dc.DC_E_CPP_MTP2TPTTC_COUNT_01 union all
  select * from dc.DC_E_CPP_MTP2TPTTC_COUNT_02 union all
  select * from dc.DC_E_CPP_MTP2TPTTC_COUNT_03 union all
  select * from dc.DC_E_CPP_MTP2TPTTC_COUNT_04 union all
  select * from dc.DC_E_CPP_MTP2TPTTC_COUNT_05 union all
  select * from dc.DC_E_CPP_MTP2TPTTC_COUNT_06;

create view dcpublic.DC_E_CPP_MTP2TPTTC_COUNT
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp2TpTtc,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmLocalSIBTime,
    pmNoOfMSUReceived,
    pmNoOfMSUTransmitted,
    pmNoOfNacks,
    pmNoOfReTransmittedOctets,
    pmNoOfSendBufferOctets,
    pmNoOfSIOSIFReceived,
    pmNoOfSIOSIFTransmitted,
    pmNoOfStartedRBCongestion,
    pmNoOfSuReceivedInError,
    pmRemoteSIBTime from
    dc.DC_E_CPP_MTP2TPTTC_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MTP3BAP_RAW as
  select * from dc.DC_E_CPP_MTP3BAP_RAW_01 union all
  select * from dc.DC_E_CPP_MTP3BAP_RAW_02 union all
  select * from dc.DC_E_CPP_MTP3BAP_RAW_03 union all
  select * from dc.DC_E_CPP_MTP3BAP_RAW_04 union all
  select * from dc.DC_E_CPP_MTP3BAP_RAW_05 union all
  select * from dc.DC_E_CPP_MTP3BAP_RAW_06;

create view dcpublic.DC_E_CPP_MTP3BAP_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp3bSp,
    Mtp3bSpType,
    Mtp3bAp,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfAdjacentSPNotAccessible,
    pmNoOfUserPartUnavailRec from
    dc.DC_E_CPP_MTP3BAP_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MTP3BAP_DAY as
  select * from dc.DC_E_CPP_MTP3BAP_DAY_01 union all
  select * from dc.DC_E_CPP_MTP3BAP_DAY_02 union all
  select * from dc.DC_E_CPP_MTP3BAP_DAY_03 union all
  select * from dc.DC_E_CPP_MTP3BAP_DAY_04 union all
  select * from dc.DC_E_CPP_MTP3BAP_DAY_05 union all
  select * from dc.DC_E_CPP_MTP3BAP_DAY_06;

create view dcpublic.DC_E_CPP_MTP3BAP_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp3bSp,
    Mtp3bSpType,
    Mtp3bAp,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmNoOfAdjacentSPNotAccessible,
    pmNoOfUserPartUnavailRec from
    dc.DC_E_CPP_MTP3BAP_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MTP3BAP_COUNT as
  select * from dc.DC_E_CPP_MTP3BAP_COUNT_01 union all
  select * from dc.DC_E_CPP_MTP3BAP_COUNT_02 union all
  select * from dc.DC_E_CPP_MTP3BAP_COUNT_03 union all
  select * from dc.DC_E_CPP_MTP3BAP_COUNT_04 union all
  select * from dc.DC_E_CPP_MTP3BAP_COUNT_05 union all
  select * from dc.DC_E_CPP_MTP3BAP_COUNT_06;

create view dcpublic.DC_E_CPP_MTP3BAP_COUNT
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp3bSp,
    Mtp3bSpType,
    Mtp3bAp,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfAdjacentSPNotAccessible,
    pmNoOfUserPartUnavailRec from
    dc.DC_E_CPP_MTP3BAP_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MTP3BSPANSI_RAW as
  select * from dc.DC_E_CPP_MTP3BSPANSI_RAW_01 union all
  select * from dc.DC_E_CPP_MTP3BSPANSI_RAW_02 union all
  select * from dc.DC_E_CPP_MTP3BSPANSI_RAW_03;

create view dcpublic.DC_E_CPP_MTP3BSPANSI_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp3bSpAnsi,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfCBARec,
    pmNoOfCBASent,
    pmNoOfChangeBackDeclRec,
    pmNoOfChangeOverRec,
    pmNoOfCOAXCARec,
    pmNoOfCOAXCASent,
    pmNoOfControlledRerouteSuccessPerf,
    pmNoOfECARec,
    pmNoOfECASent,
    pmNoOfECOSent,
    pmNoOfEmergencyChangeOverRec,
    pmNoOfForcedRerouteSuccessPerf,
    pmNoOfIncomingAssocEstabRequestInStateDownWhenStateEstabIsBlocked,
    pmNoOfLowerPrioMsgDiscarded,
    pmNoOfMaxTrialsForAssocActivReached,
    pmNoOfMaxTrialsForAssocEstabReached,
    pmNoOfSctpAssociationRestart,
    pmNoOfSctpBufOverflow,
    pmNoOfSctpCommunicationErr,
    pmNoOfSctpNetworkStatusChange,
    pmNoOfSctpResumeSending,
    pmNoOfSctpSendFailure,
    pmNoOfSLTAFirstTimeOutRec,
    pmNoOfSLTASecondTimeOutRec,
    pmNoOfSuccessAssocAbort,
    pmNoOfSuccessAssocEstablish,
    pmNoOfSuccessAssocShutDown,
    pmNoOfTimerT21WasStarted,
    pmNoOfTRARec,
    pmNoOfTRASent,
    pmNoOfUnsuccessAssocEstablish,
    pmNoOfUnsuccessAssocShutDown,
    pmNoOfUnsuccessForcedRerouting,
    pmNoOfUPMsgDiscardedDueToRoutingErr from
    dc.DC_E_CPP_MTP3BSPANSI_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MTP3BSPANSI_DAY as
  select * from dc.DC_E_CPP_MTP3BSPANSI_DAY_01 union all
  select * from dc.DC_E_CPP_MTP3BSPANSI_DAY_02 union all
  select * from dc.DC_E_CPP_MTP3BSPANSI_DAY_03;

create view dcpublic.DC_E_CPP_MTP3BSPANSI_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp3bSpAnsi,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmNoOfCBARec,
    pmNoOfCBASent,
    pmNoOfChangeBackDeclRec,
    pmNoOfChangeOverRec,
    pmNoOfCOAXCARec,
    pmNoOfCOAXCASent,
    pmNoOfControlledRerouteSuccessPerf,
    pmNoOfECARec,
    pmNoOfECASent,
    pmNoOfECOSent,
    pmNoOfEmergencyChangeOverRec,
    pmNoOfForcedRerouteSuccessPerf,
    pmNoOfIncomingAssocEstabRequestInStateDownWhenStateEstabIsBlocked,
    pmNoOfLowerPrioMsgDiscarded,
    pmNoOfMaxTrialsForAssocActivReached,
    pmNoOfMaxTrialsForAssocEstabReached,
    pmNoOfSctpAssociationRestart,
    pmNoOfSctpBufOverflow,
    pmNoOfSctpCommunicationErr,
    pmNoOfSctpNetworkStatusChange,
    pmNoOfSctpResumeSending,
    pmNoOfSctpSendFailure,
    pmNoOfSLTAFirstTimeOutRec,
    pmNoOfSLTASecondTimeOutRec,
    pmNoOfSuccessAssocAbort,
    pmNoOfSuccessAssocEstablish,
    pmNoOfSuccessAssocShutDown,
    pmNoOfTimerT21WasStarted,
    pmNoOfTRARec,
    pmNoOfTRASent,
    pmNoOfUnsuccessAssocEstablish,
    pmNoOfUnsuccessAssocShutDown,
    pmNoOfUnsuccessForcedRerouting,
    pmNoOfUPMsgDiscardedDueToRoutingErr from
    dc.DC_E_CPP_MTP3BSPANSI_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MTP3BSPANSI_COUNT as
  select * from dc.DC_E_CPP_MTP3BSPANSI_COUNT_01 union all
  select * from dc.DC_E_CPP_MTP3BSPANSI_COUNT_02 union all
  select * from dc.DC_E_CPP_MTP3BSPANSI_COUNT_03;

create view dcpublic.DC_E_CPP_MTP3BSPANSI_COUNT
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp3bSpAnsi,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfCBARec,
    pmNoOfCBASent,
    pmNoOfChangeBackDeclRec,
    pmNoOfChangeOverRec,
    pmNoOfCOAXCARec,
    pmNoOfCOAXCASent,
    pmNoOfControlledRerouteSuccessPerf,
    pmNoOfECARec,
    pmNoOfECASent,
    pmNoOfECOSent,
    pmNoOfEmergencyChangeOverRec,
    pmNoOfForcedRerouteSuccessPerf,
    pmNoOfIncomingAssocEstabRequestInStateDownWhenStateEstabIsBlocked,
    pmNoOfLowerPrioMsgDiscarded,
    pmNoOfMaxTrialsForAssocActivReached,
    pmNoOfMaxTrialsForAssocEstabReached,
    pmNoOfSctpAssociationRestart,
    pmNoOfSctpBufOverflow,
    pmNoOfSctpCommunicationErr,
    pmNoOfSctpNetworkStatusChange,
    pmNoOfSctpResumeSending,
    pmNoOfSctpSendFailure,
    pmNoOfSLTAFirstTimeOutRec,
    pmNoOfSLTASecondTimeOutRec,
    pmNoOfSuccessAssocAbort,
    pmNoOfSuccessAssocEstablish,
    pmNoOfSuccessAssocShutDown,
    pmNoOfTimerT21WasStarted,
    pmNoOfTRARec,
    pmNoOfTRASent,
    pmNoOfUnsuccessAssocEstablish,
    pmNoOfUnsuccessAssocShutDown,
    pmNoOfUnsuccessForcedRerouting,
    pmNoOfUPMsgDiscardedDueToRoutingErr from
    dc.DC_E_CPP_MTP3BSPANSI_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MTP3BSPCHINA_RAW as
  select * from dc.DC_E_CPP_MTP3BSPCHINA_RAW_01 union all
  select * from dc.DC_E_CPP_MTP3BSPCHINA_RAW_02 union all
  select * from dc.DC_E_CPP_MTP3BSPCHINA_RAW_03;

create view dcpublic.DC_E_CPP_MTP3BSPCHINA_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp3bSpChina,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfCBARec,
    pmNoOfCBASent,
    pmNoOfChangeBackDeclRec,
    pmNoOfChangeOverRec,
    pmNoOfCOAXCARec,
    pmNoOfCOAXCASent,
    pmNoOfControlledRerouteSuccessPerf,
    pmNoOfECARec,
    pmNoOfECASent,
    pmNoOfECOSent,
    pmNoOfEmergencyChangeOverRec,
    pmNoOfForcedRerouteSuccessPerf,
    pmNoOfIncomingAssocEstabRequestInStateDownWhenStateEstabIsBlocked,
    pmNoOfLowerPrioMsgDiscarded,
    pmNoOfMaxTrialsForAssocActivReached,
    pmNoOfMaxTrialsForAssocEstabReached,
    pmNoOfSctpAssociationRestart,
    pmNoOfSctpBufOverflow,
    pmNoOfSctpCommunicationErr,
    pmNoOfSctpNetworkStatusChange,
    pmNoOfSctpResumeSending,
    pmNoOfSctpSendFailure,
    pmNoOfSLTAFirstTimeOutRec,
    pmNoOfSLTASecondTimeOutRec,
    pmNoOfSuccessAssocAbort,
    pmNoOfSuccessAssocEstablish,
    pmNoOfSuccessAssocShutDown,
    pmNoOfTimerT21WasStarted,
    pmNoOfTRARec,
    pmNoOfTRASent,
    pmNoOfUnsuccessAssocEstablish,
    pmNoOfUnsuccessAssocShutDown,
    pmNoOfUnsuccessForcedRerouting,
    pmNoOfUPMsgDiscardedDueToRoutingErr from
    dc.DC_E_CPP_MTP3BSPCHINA_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MTP3BSPCHINA_DAY as
  select * from dc.DC_E_CPP_MTP3BSPCHINA_DAY_01 union all
  select * from dc.DC_E_CPP_MTP3BSPCHINA_DAY_02 union all
  select * from dc.DC_E_CPP_MTP3BSPCHINA_DAY_03;

create view dcpublic.DC_E_CPP_MTP3BSPCHINA_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp3bSpChina,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmNoOfCBARec,
    pmNoOfCBASent,
    pmNoOfChangeBackDeclRec,
    pmNoOfChangeOverRec,
    pmNoOfCOAXCARec,
    pmNoOfCOAXCASent,
    pmNoOfControlledRerouteSuccessPerf,
    pmNoOfECARec,
    pmNoOfECASent,
    pmNoOfECOSent,
    pmNoOfEmergencyChangeOverRec,
    pmNoOfForcedRerouteSuccessPerf,
    pmNoOfIncomingAssocEstabRequestInStateDownWhenStateEstabIsBlocked,
    pmNoOfLowerPrioMsgDiscarded,
    pmNoOfMaxTrialsForAssocActivReached,
    pmNoOfMaxTrialsForAssocEstabReached,
    pmNoOfSctpAssociationRestart,
    pmNoOfSctpBufOverflow,
    pmNoOfSctpCommunicationErr,
    pmNoOfSctpNetworkStatusChange,
    pmNoOfSctpResumeSending,
    pmNoOfSctpSendFailure,
    pmNoOfSLTAFirstTimeOutRec,
    pmNoOfSLTASecondTimeOutRec,
    pmNoOfSuccessAssocAbort,
    pmNoOfSuccessAssocEstablish,
    pmNoOfSuccessAssocShutDown,
    pmNoOfTimerT21WasStarted,
    pmNoOfTRARec,
    pmNoOfTRASent,
    pmNoOfUnsuccessAssocEstablish,
    pmNoOfUnsuccessAssocShutDown,
    pmNoOfUnsuccessForcedRerouting,
    pmNoOfUPMsgDiscardedDueToRoutingErr from
    dc.DC_E_CPP_MTP3BSPCHINA_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MTP3BSPCHINA_COUNT as
  select * from dc.DC_E_CPP_MTP3BSPCHINA_COUNT_01 union all
  select * from dc.DC_E_CPP_MTP3BSPCHINA_COUNT_02 union all
  select * from dc.DC_E_CPP_MTP3BSPCHINA_COUNT_03;

create view dcpublic.DC_E_CPP_MTP3BSPCHINA_COUNT
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp3bSpChina,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfCBARec,
    pmNoOfCBASent,
    pmNoOfChangeBackDeclRec,
    pmNoOfChangeOverRec,
    pmNoOfCOAXCARec,
    pmNoOfCOAXCASent,
    pmNoOfControlledRerouteSuccessPerf,
    pmNoOfECARec,
    pmNoOfECASent,
    pmNoOfECOSent,
    pmNoOfEmergencyChangeOverRec,
    pmNoOfForcedRerouteSuccessPerf,
    pmNoOfIncomingAssocEstabRequestInStateDownWhenStateEstabIsBlocked,
    pmNoOfLowerPrioMsgDiscarded,
    pmNoOfMaxTrialsForAssocActivReached,
    pmNoOfMaxTrialsForAssocEstabReached,
    pmNoOfSctpAssociationRestart,
    pmNoOfSctpBufOverflow,
    pmNoOfSctpCommunicationErr,
    pmNoOfSctpNetworkStatusChange,
    pmNoOfSctpResumeSending,
    pmNoOfSctpSendFailure,
    pmNoOfSLTAFirstTimeOutRec,
    pmNoOfSLTASecondTimeOutRec,
    pmNoOfSuccessAssocAbort,
    pmNoOfSuccessAssocEstablish,
    pmNoOfSuccessAssocShutDown,
    pmNoOfTimerT21WasStarted,
    pmNoOfTRARec,
    pmNoOfTRASent,
    pmNoOfUnsuccessAssocEstablish,
    pmNoOfUnsuccessAssocShutDown,
    pmNoOfUnsuccessForcedRerouting,
    pmNoOfUPMsgDiscardedDueToRoutingErr from
    dc.DC_E_CPP_MTP3BSPCHINA_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MTP3BSPITU_RAW as
  select * from dc.DC_E_CPP_MTP3BSPITU_RAW_01 union all
  select * from dc.DC_E_CPP_MTP3BSPITU_RAW_02 union all
  select * from dc.DC_E_CPP_MTP3BSPITU_RAW_03;

create view dcpublic.DC_E_CPP_MTP3BSPITU_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp3bSpItu,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfCBARec,
    pmNoOfCBASent,
    pmNoOfChangeBackDeclRec,
    pmNoOfChangeOverRec,
    pmNoOfCOAXCARec,
    pmNoOfCOAXCASent,
    pmNoOfControlledRerouteSuccessPerf,
    pmNoOfECARec,
    pmNoOfECASent,
    pmNoOfECOSent,
    pmNoOfEmergencyChangeOverRec,
    pmNoOfForcedRerouteSuccessPerf,
    pmNoOfIncomingAssocEstabRequestInStateDownWhenStateEstabIsBlocked,
    pmNoOfLowerPrioMsgDiscarded,
    pmNoOfMaxTrialsForAssocActivReached,
    pmNoOfMaxTrialsForAssocEstabReached,
    pmNoOfSctpAssociationRestart,
    pmNoOfSctpBufOverflow,
    pmNoOfSctpCommunicationErr,
    pmNoOfSctpNetworkStatusChange,
    pmNoOfSctpResumeSending,
    pmNoOfSctpSendFailure,
    pmNoOfSLTAFirstTimeOutRec,
    pmNoOfSLTASecondTimeOutRec,
    pmNoOfSuccessAssocAbort,
    pmNoOfSuccessAssocEstablish,
    pmNoOfSuccessAssocShutDown,
    pmNoOfTimerT21WasStarted,
    pmNoOfTRARec,
    pmNoOfTRASent,
    pmNoOfUnsuccessAssocEstablish,
    pmNoOfUnsuccessAssocShutDown,
    pmNoOfUnsuccessForcedRerouting,
    pmNoOfUPMsgDiscardedDueToRoutingErr from
    dc.DC_E_CPP_MTP3BSPITU_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MTP3BSPITU_DAY as
  select * from dc.DC_E_CPP_MTP3BSPITU_DAY_01 union all
  select * from dc.DC_E_CPP_MTP3BSPITU_DAY_02 union all
  select * from dc.DC_E_CPP_MTP3BSPITU_DAY_03;

create view dcpublic.DC_E_CPP_MTP3BSPITU_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp3bSpItu,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmNoOfCBARec,
    pmNoOfCBASent,
    pmNoOfChangeBackDeclRec,
    pmNoOfChangeOverRec,
    pmNoOfCOAXCARec,
    pmNoOfCOAXCASent,
    pmNoOfControlledRerouteSuccessPerf,
    pmNoOfECARec,
    pmNoOfECASent,
    pmNoOfECOSent,
    pmNoOfEmergencyChangeOverRec,
    pmNoOfForcedRerouteSuccessPerf,
    pmNoOfIncomingAssocEstabRequestInStateDownWhenStateEstabIsBlocked,
    pmNoOfLowerPrioMsgDiscarded,
    pmNoOfMaxTrialsForAssocActivReached,
    pmNoOfMaxTrialsForAssocEstabReached,
    pmNoOfSctpAssociationRestart,
    pmNoOfSctpBufOverflow,
    pmNoOfSctpCommunicationErr,
    pmNoOfSctpNetworkStatusChange,
    pmNoOfSctpResumeSending,
    pmNoOfSctpSendFailure,
    pmNoOfSLTAFirstTimeOutRec,
    pmNoOfSLTASecondTimeOutRec,
    pmNoOfSuccessAssocAbort,
    pmNoOfSuccessAssocEstablish,
    pmNoOfSuccessAssocShutDown,
    pmNoOfTimerT21WasStarted,
    pmNoOfTRARec,
    pmNoOfTRASent,
    pmNoOfUnsuccessAssocEstablish,
    pmNoOfUnsuccessAssocShutDown,
    pmNoOfUnsuccessForcedRerouting,
    pmNoOfUPMsgDiscardedDueToRoutingErr from
    dc.DC_E_CPP_MTP3BSPITU_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MTP3BSPITU_COUNT as
  select * from dc.DC_E_CPP_MTP3BSPITU_COUNT_01 union all
  select * from dc.DC_E_CPP_MTP3BSPITU_COUNT_02 union all
  select * from dc.DC_E_CPP_MTP3BSPITU_COUNT_03;

create view dcpublic.DC_E_CPP_MTP3BSPITU_COUNT
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp3bSpItu,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfCBARec,
    pmNoOfCBASent,
    pmNoOfChangeBackDeclRec,
    pmNoOfChangeOverRec,
    pmNoOfCOAXCARec,
    pmNoOfCOAXCASent,
    pmNoOfControlledRerouteSuccessPerf,
    pmNoOfECARec,
    pmNoOfECASent,
    pmNoOfECOSent,
    pmNoOfEmergencyChangeOverRec,
    pmNoOfForcedRerouteSuccessPerf,
    pmNoOfIncomingAssocEstabRequestInStateDownWhenStateEstabIsBlocked,
    pmNoOfLowerPrioMsgDiscarded,
    pmNoOfMaxTrialsForAssocActivReached,
    pmNoOfMaxTrialsForAssocEstabReached,
    pmNoOfSctpAssociationRestart,
    pmNoOfSctpBufOverflow,
    pmNoOfSctpCommunicationErr,
    pmNoOfSctpNetworkStatusChange,
    pmNoOfSctpResumeSending,
    pmNoOfSctpSendFailure,
    pmNoOfSLTAFirstTimeOutRec,
    pmNoOfSLTASecondTimeOutRec,
    pmNoOfSuccessAssocAbort,
    pmNoOfSuccessAssocEstablish,
    pmNoOfSuccessAssocShutDown,
    pmNoOfTimerT21WasStarted,
    pmNoOfTRARec,
    pmNoOfTRASent,
    pmNoOfUnsuccessAssocEstablish,
    pmNoOfUnsuccessAssocShutDown,
    pmNoOfUnsuccessForcedRerouting,
    pmNoOfUPMsgDiscardedDueToRoutingErr from
    dc.DC_E_CPP_MTP3BSPITU_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MTP3BSPTTC_RAW as
  select * from dc.DC_E_CPP_MTP3BSPTTC_RAW_01 union all
  select * from dc.DC_E_CPP_MTP3BSPTTC_RAW_02 union all
  select * from dc.DC_E_CPP_MTP3BSPTTC_RAW_03;

create view dcpublic.DC_E_CPP_MTP3BSPTTC_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp3bSpTtc,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfCBARec,
    pmNoOfCBASent,
    pmNoOfChangeBackDeclRec,
    pmNoOfChangeOverRec,
    pmNoOfCOAXCARec,
    pmNoOfCOAXCASent,
    pmNoOfControlledRerouteSuccessPerf,
    pmNoOfECARec,
    pmNoOfECASent,
    pmNoOfECOSent,
    pmNoOfEmergencyChangeOverRec,
    pmNoOfForcedRerouteSuccessPerf,
    pmNoOfIncomingAssocEstabRequestInStateDownWhenStateEstabIsBlocked,
    pmNoOfMaxTrialsForAssocActivReached,
    pmNoOfMaxTrialsForAssocEstabReached,
    pmNoOfSctpAssociationRestart,
    pmNoOfSctpBufOverflow,
    pmNoOfSctpCommunicationErr,
    pmNoOfSctpNetworkStatusChange,
    pmNoOfSctpResumeSending,
    pmNoOfSctpSendFailure,
    pmNoOfSuccessAssocAbort,
    pmNoOfSuccessAssocEstablish,
    pmNoOfSuccessAssocShutDown,
    pmNoOfTRARec,
    pmNoOfTRASent,
    pmNoOfUnsuccessAssocEstablish,
    pmNoOfUnsuccessAssocShutDown,
    pmNoOfUnsuccessForcedRerouting,
    pmNoOfUPMsgDiscardedDueToRoutingErr,
    pmNoOfSLTAFirstTimeOutRec,
    pmNoOfSLTASecondTimeOutRec from
    dc.DC_E_CPP_MTP3BSPTTC_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_IMS_PTT_PTTCSS_DAY as
  select * from dc.DC_E_IMS_PTT_PTTCSS_DAY_01 union all
  select * from dc.DC_E_IMS_PTT_PTTCSS_DAY_03 union all
  select * from dc.DC_E_IMS_PTT_PTTCSS_DAY_05 union all
  select * from dc.DC_E_IMS_PTT_PTTCSS_DAY_02 union all
  select * from dc.DC_E_IMS_PTT_PTTCSS_DAY_04 union all
  select * from dc.DC_E_IMS_PTT_PTTCSS_DAY_06;

create view dc.DC_E_CPP_MTP3BSPTTC_DAY as
  select * from dc.DC_E_CPP_MTP3BSPTTC_DAY_01 union all
  select * from dc.DC_E_CPP_MTP3BSPTTC_DAY_02 union all
  select * from dc.DC_E_CPP_MTP3BSPTTC_DAY_03;

create view dcpublic.DC_E_CPP_MTP3BSPTTC_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp3bSpTtc,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmNoOfCBARec,
    pmNoOfCBASent,
    pmNoOfChangeBackDeclRec,
    pmNoOfChangeOverRec,
    pmNoOfCOAXCARec,
    pmNoOfCOAXCASent,
    pmNoOfControlledRerouteSuccessPerf,
    pmNoOfECARec,
    pmNoOfECASent,
    pmNoOfECOSent,
    pmNoOfEmergencyChangeOverRec,
    pmNoOfForcedRerouteSuccessPerf,
    pmNoOfIncomingAssocEstabRequestInStateDownWhenStateEstabIsBlocked,
    pmNoOfMaxTrialsForAssocActivReached,
    pmNoOfMaxTrialsForAssocEstabReached,
    pmNoOfSctpAssociationRestart,
    pmNoOfSctpBufOverflow,
    pmNoOfSctpCommunicationErr,
    pmNoOfSctpNetworkStatusChange,
    pmNoOfSctpResumeSending,
    pmNoOfSctpSendFailure,
    pmNoOfSuccessAssocAbort,
    pmNoOfSuccessAssocEstablish,
    pmNoOfSuccessAssocShutDown,
    pmNoOfTRARec,
    pmNoOfTRASent,
    pmNoOfUnsuccessAssocEstablish,
    pmNoOfUnsuccessAssocShutDown,
    pmNoOfUnsuccessForcedRerouting,
    pmNoOfUPMsgDiscardedDueToRoutingErr,
    pmNoOfSLTAFirstTimeOutRec,
    pmNoOfSLTASecondTimeOutRec from
    dc.DC_E_CPP_MTP3BSPTTC_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MTP3BSPTTC_COUNT as
  select * from dc.DC_E_CPP_MTP3BSPTTC_COUNT_01 union all
  select * from dc.DC_E_CPP_MTP3BSPTTC_COUNT_02 union all
  select * from dc.DC_E_CPP_MTP3BSPTTC_COUNT_03;

create view dcpublic.DC_E_CPP_MTP3BSPTTC_COUNT
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp3bSpTtc,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfCBARec,
    pmNoOfCBASent,
    pmNoOfChangeBackDeclRec,
    pmNoOfChangeOverRec,
    pmNoOfCOAXCARec,
    pmNoOfCOAXCASent,
    pmNoOfControlledRerouteSuccessPerf,
    pmNoOfECARec,
    pmNoOfECASent,
    pmNoOfECOSent,
    pmNoOfEmergencyChangeOverRec,
    pmNoOfForcedRerouteSuccessPerf,
    pmNoOfIncomingAssocEstabRequestInStateDownWhenStateEstabIsBlocked,
    pmNoOfMaxTrialsForAssocActivReached,
    pmNoOfMaxTrialsForAssocEstabReached,
    pmNoOfSctpAssociationRestart,
    pmNoOfSctpBufOverflow,
    pmNoOfSctpCommunicationErr,
    pmNoOfSctpNetworkStatusChange,
    pmNoOfSctpResumeSending,
    pmNoOfSctpSendFailure,
    pmNoOfSuccessAssocAbort,
    pmNoOfSuccessAssocEstablish,
    pmNoOfSuccessAssocShutDown,
    pmNoOfTRARec,
    pmNoOfTRASent,
    pmNoOfUnsuccessAssocEstablish,
    pmNoOfUnsuccessAssocShutDown,
    pmNoOfUnsuccessForcedRerouting,
    pmNoOfUPMsgDiscardedDueToRoutingErr,
    pmNoOfSLTAFirstTimeOutRec,
    pmNoOfSLTASecondTimeOutRec from
    dc.DC_E_CPP_MTP3BSPTTC_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MTP3BSR_RAW as
  select * from dc.DC_E_CPP_MTP3BSR_RAW_01 union all
  select * from dc.DC_E_CPP_MTP3BSR_RAW_02 union all
  select * from dc.DC_E_CPP_MTP3BSR_RAW_03 union all
  select * from dc.DC_E_CPP_MTP3BSR_RAW_04 union all
  select * from dc.DC_E_CPP_MTP3BSR_RAW_05 union all
  select * from dc.DC_E_CPP_MTP3BSR_RAW_06;

create view dcpublic.DC_E_CPP_MTP3BSR_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp3bSp,
    Mtp3bSpType,
    Mtp3bSrs,
    Mtp3bSr,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfSecondsAccumulatedRouteUnavailable from
    dc.DC_E_CPP_MTP3BSR_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MTP3BSR_DAY as
  select * from dc.DC_E_CPP_MTP3BSR_DAY_01 union all
  select * from dc.DC_E_CPP_MTP3BSR_DAY_02 union all
  select * from dc.DC_E_CPP_MTP3BSR_DAY_03 union all
  select * from dc.DC_E_CPP_MTP3BSR_DAY_04 union all
  select * from dc.DC_E_CPP_MTP3BSR_DAY_05 union all
  select * from dc.DC_E_CPP_MTP3BSR_DAY_06;

create view dcpublic.DC_E_CPP_MTP3BSR_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp3bSp,
    Mtp3bSpType,
    Mtp3bSrs,
    Mtp3bSr,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmNoOfSecondsAccumulatedRouteUnavailable from
    dc.DC_E_CPP_MTP3BSR_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MTP3BSR_COUNT as
  select * from dc.DC_E_CPP_MTP3BSR_COUNT_01 union all
  select * from dc.DC_E_CPP_MTP3BSR_COUNT_02 union all
  select * from dc.DC_E_CPP_MTP3BSR_COUNT_03 union all
  select * from dc.DC_E_CPP_MTP3BSR_COUNT_04 union all
  select * from dc.DC_E_CPP_MTP3BSR_COUNT_05 union all
  select * from dc.DC_E_CPP_MTP3BSR_COUNT_06;

create view dcpublic.DC_E_CPP_MTP3BSR_COUNT
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp3bSp,
    Mtp3bSpType,
    Mtp3bSrs,
    Mtp3bSr,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfSecondsAccumulatedRouteUnavailable from
    dc.DC_E_CPP_MTP3BSR_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MTP3BSRS_RAW as
  select * from dc.DC_E_CPP_MTP3BSRS_RAW_01 union all
  select * from dc.DC_E_CPP_MTP3BSRS_RAW_02 union all
  select * from dc.DC_E_CPP_MTP3BSRS_RAW_03 union all
  select * from dc.DC_E_CPP_MTP3BSRS_RAW_04 union all
  select * from dc.DC_E_CPP_MTP3BSRS_RAW_05 union all
  select * from dc.DC_E_CPP_MTP3BSRS_RAW_06;

create view dcpublic.DC_E_CPP_MTP3BSRS_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp3bSp,
    Mtp3bSpType,
    Mtp3bSrs,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfDiscardedMsgFromBroadToNarrow,
    pmNoOfSecsAccRouteSetUnavailable,
    pmNoOfTransferAllowedRec,
    pmNoOfTransferControlledRec,
    pmNoOfTransferProhibitedRec from
    dc.DC_E_CPP_MTP3BSRS_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MTP3BSRS_DAY as
  select * from dc.DC_E_CPP_MTP3BSRS_DAY_01 union all
  select * from dc.DC_E_CPP_MTP3BSRS_DAY_02 union all
  select * from dc.DC_E_CPP_MTP3BSRS_DAY_03 union all
  select * from dc.DC_E_CPP_MTP3BSRS_DAY_04 union all
  select * from dc.DC_E_CPP_MTP3BSRS_DAY_05 union all
  select * from dc.DC_E_CPP_MTP3BSRS_DAY_06;

create view dcpublic.DC_E_CPP_MTP3BSRS_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp3bSp,
    Mtp3bSpType,
    Mtp3bSrs,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmNoOfDiscardedMsgFromBroadToNarrow,
    pmNoOfSecsAccRouteSetUnavailable,
    pmNoOfTransferAllowedRec,
    pmNoOfTransferControlledRec,
    pmNoOfTransferProhibitedRec from
    dc.DC_E_CPP_MTP3BSRS_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MTP3BSRS_COUNT as
  select * from dc.DC_E_CPP_MTP3BSRS_COUNT_01 union all
  select * from dc.DC_E_CPP_MTP3BSRS_COUNT_02 union all
  select * from dc.DC_E_CPP_MTP3BSRS_COUNT_03 union all
  select * from dc.DC_E_CPP_MTP3BSRS_COUNT_04 union all
  select * from dc.DC_E_CPP_MTP3BSRS_COUNT_05 union all
  select * from dc.DC_E_CPP_MTP3BSRS_COUNT_06;

create view dcpublic.DC_E_CPP_MTP3BSRS_COUNT
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp3bSp,
    Mtp3bSpType,
    Mtp3bSrs,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfDiscardedMsgFromBroadToNarrow,
    pmNoOfSecsAccRouteSetUnavailable,
    pmNoOfTransferAllowedRec,
    pmNoOfTransferControlledRec,
    pmNoOfTransferProhibitedRec from
    dc.DC_E_CPP_MTP3BSRS_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_NNISAALTP_RAW as
  select * from dc.DC_E_CPP_NNISAALTP_RAW_01 union all
  select * from dc.DC_E_CPP_NNISAALTP_RAW_02 union all
  select * from dc.DC_E_CPP_NNISAALTP_RAW_03;

create view dcpublic.DC_E_CPP_NNISAALTP_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    NniSaalTp,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmLinkInServiceTime,
    pmNoOfAlignmentFailures,
    pmNoOfAllSLFailures,
    pmNoOfLocalCongestions,
    pmNoOfNoResponses,
    pmNoOfOtherErrors,
    pmNoOfProtocolErrors,
    pmNoOfReceivedSDUs,
    pmNoOfRemoteCongestions,
    pmNoOfSentSDUs,
    pmNoOfSequenceDataLosses,
    pmNoOfUnsuccReTransmissions from
    dc.DC_E_CPP_NNISAALTP_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_NNISAALTP_DAY as
  select * from dc.DC_E_CPP_NNISAALTP_DAY_01 union all
  select * from dc.DC_E_CPP_NNISAALTP_DAY_02 union all
  select * from dc.DC_E_CPP_NNISAALTP_DAY_03;

create view dcpublic.DC_E_CPP_NNISAALTP_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    NniSaalTp,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmLinkInServiceTime,
    pmNoOfAlignmentFailures,
    pmNoOfAllSLFailures,
    pmNoOfLocalCongestions,
    pmNoOfNoResponses,
    pmNoOfOtherErrors,
    pmNoOfProtocolErrors,
    pmNoOfReceivedSDUs,
    pmNoOfRemoteCongestions,
    pmNoOfSentSDUs,
    pmNoOfSequenceDataLosses,
    pmNoOfUnsuccReTransmissions from
    dc.DC_E_CPP_NNISAALTP_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_NNISAALTP_COUNT as
  select * from dc.DC_E_CPP_NNISAALTP_COUNT_01 union all
  select * from dc.DC_E_CPP_NNISAALTP_COUNT_02 union all
  select * from dc.DC_E_CPP_NNISAALTP_COUNT_03;

create view dcpublic.DC_E_CPP_NNISAALTP_COUNT
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    NniSaalTp,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmLinkInServiceTime,
    pmNoOfAlignmentFailures,
    pmNoOfAllSLFailures,
    pmNoOfLocalCongestions,
    pmNoOfNoResponses,
    pmNoOfOtherErrors,
    pmNoOfProtocolErrors,
    pmNoOfReceivedSDUs,
    pmNoOfRemoteCongestions,
    pmNoOfSentSDUs,
    pmNoOfSequenceDataLosses,
    pmNoOfUnsuccReTransmissions from
    dc.DC_E_CPP_NNISAALTP_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_OS155SPITTP_RAW as
  select * from dc.DC_E_CPP_OS155SPITTP_RAW_01 union all
  select * from dc.DC_E_CPP_OS155SPITTP_RAW_02 union all
  select * from dc.DC_E_CPP_OS155SPITTP_RAW_03;

create view dcpublic.DC_E_CPP_OS155SPITTP_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    Cbu,
    ExchangeTerminal,
    Os155SpiTtp,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmMsBbe,
    pmMsEs,
    pmMsSes,
    pmMsUas from
    dc.DC_E_CPP_OS155SPITTP_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_OS155SPITTP_DAY as
  select * from dc.DC_E_CPP_OS155SPITTP_DAY_01 union all
  select * from dc.DC_E_CPP_OS155SPITTP_DAY_02 union all
  select * from dc.DC_E_CPP_OS155SPITTP_DAY_03;

create view dcpublic.DC_E_CPP_OS155SPITTP_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    Cbu,
    ExchangeTerminal,
    Os155SpiTtp,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmMsBbe,
    pmMsEs,
    pmMsSes,
    pmMsUas from
    dc.DC_E_CPP_OS155SPITTP_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_OSPF_RAW as
  select * from dc.DC_E_CPP_OSPF_RAW_01 union all
  select * from dc.DC_E_CPP_OSPF_RAW_02 union all
  select * from dc.DC_E_CPP_OSPF_RAW_03;

create view dcpublic.DC_E_CPP_OSPF_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    IpOam,
    IpSystem,
    Ospf,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfOspfOriginateNewLsas,
    pmNoOfOspfRxNewLsas from
    dc.DC_E_CPP_OSPF_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_OSPF_DAY as
  select * from dc.DC_E_CPP_OSPF_DAY_01 union all
  select * from dc.DC_E_CPP_OSPF_DAY_02 union all
  select * from dc.DC_E_CPP_OSPF_DAY_03;

create view dcpublic.DC_E_CPP_OSPF_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    IpOam,
    IpSystem,
    Ospf,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmNoOfOspfOriginateNewLsas,
    pmNoOfOspfRxNewLsas from
    dc.DC_E_CPP_OSPF_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_OSPF_COUNT as
  select * from dc.DC_E_CPP_OSPF_COUNT_01 union all
  select * from dc.DC_E_CPP_OSPF_COUNT_02 union all
  select * from dc.DC_E_CPP_OSPF_COUNT_03;

create view dcpublic.DC_E_CPP_OSPF_COUNT
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    IpOam,
    IpSystem,
    Ospf,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfOspfOriginateNewLsas,
    pmNoOfOspfRxNewLsas from
    dc.DC_E_CPP_OSPF_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_OSPFAREA_RAW as
  select * from dc.DC_E_CPP_OSPFAREA_RAW_01 union all
  select * from dc.DC_E_CPP_OSPFAREA_RAW_02 union all
  select * from dc.DC_E_CPP_OSPFAREA_RAW_03;

create view dcpublic.DC_E_CPP_OSPFAREA_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    IpOam,
    IpSystem,
    Ospf,
    OspfArea,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfOspfSpfRuns from
    dc.DC_E_CPP_OSPFAREA_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_OSPFAREA_DAY as
  select * from dc.DC_E_CPP_OSPFAREA_DAY_01 union all
  select * from dc.DC_E_CPP_OSPFAREA_DAY_02 union all
  select * from dc.DC_E_CPP_OSPFAREA_DAY_03;

create view dcpublic.DC_E_CPP_OSPFAREA_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    IpOam,
    IpSystem,
    Ospf,
    OspfArea,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmNoOfOspfSpfRuns from
    dc.DC_E_CPP_OSPFAREA_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_OSPFAREA_COUNT as
  select * from dc.DC_E_CPP_OSPFAREA_COUNT_01 union all
  select * from dc.DC_E_CPP_OSPFAREA_COUNT_02 union all
  select * from dc.DC_E_CPP_OSPFAREA_COUNT_03;

create view dcpublic.DC_E_CPP_OSPFAREA_COUNT
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    IpOam,
    IpSystem,
    Ospf,
    OspfArea,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfOspfSpfRuns from
    dc.DC_E_CPP_OSPFAREA_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_SCCPSCRC_RAW as
  select * from dc.DC_E_CPP_SCCPSCRC_RAW_01 union all
  select * from dc.DC_E_CPP_SCCPSCRC_RAW_02 union all
  select * from dc.DC_E_CPP_SCCPSCRC_RAW_03;

create view dcpublic.DC_E_CPP_SCCPSCRC_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    SccpSp,
    SccpScrc,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfConnectFailure,
    pmNoOfHopCounterViolation,
    pmNoOfRoutingFailNetworkCongest,
    pmNoOfRoutingFailNoTransAddrOfSuchNature,
    pmNoOfRoutingFailNoTransSpecificAddr,
    pmNoOfRoutingFailReasonUnknown,
    pmNoOfRoutingFailSubsysUnavail,
    pmNoOfRoutingFailUnequippedSubsys,
    pmNoOfRoutingFailure,
    pmNoOfRoutingFailurePointCodeUnAvail from
    dc.DC_E_CPP_SCCPSCRC_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_SCCPSCRC_DAY as
  select * from dc.DC_E_CPP_SCCPSCRC_DAY_01 union all
  select * from dc.DC_E_CPP_SCCPSCRC_DAY_02 union all
  select * from dc.DC_E_CPP_SCCPSCRC_DAY_03;

create view dcpublic.DC_E_CPP_SCCPSCRC_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    SccpSp,
    SccpScrc,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmNoOfConnectFailure,
    pmNoOfHopCounterViolation,
    pmNoOfRoutingFailNetworkCongest,
    pmNoOfRoutingFailNoTransAddrOfSuchNature,
    pmNoOfRoutingFailNoTransSpecificAddr,
    pmNoOfRoutingFailReasonUnknown,
    pmNoOfRoutingFailSubsysUnavail,
    pmNoOfRoutingFailUnequippedSubsys,
    pmNoOfRoutingFailure,
    pmNoOfRoutingFailurePointCodeUnAvail from
    dc.DC_E_CPP_SCCPSCRC_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_SCCPSCRC_COUNT as
  select * from dc.DC_E_CPP_SCCPSCRC_COUNT_01 union all
  select * from dc.DC_E_CPP_SCCPSCRC_COUNT_02 union all
  select * from dc.DC_E_CPP_SCCPSCRC_COUNT_03;

create view dcpublic.DC_E_CPP_SCCPSCRC_COUNT
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    SccpSp,
    SccpScrc,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfConnectFailure,
    pmNoOfHopCounterViolation,
    pmNoOfRoutingFailNetworkCongest,
    pmNoOfRoutingFailNoTransAddrOfSuchNature,
    pmNoOfRoutingFailNoTransSpecificAddr,
    pmNoOfRoutingFailReasonUnknown,
    pmNoOfRoutingFailSubsysUnavail,
    pmNoOfRoutingFailUnequippedSubsys,
    pmNoOfRoutingFailure,
    pmNoOfRoutingFailurePointCodeUnAvail from
    dc.DC_E_CPP_SCCPSCRC_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_OSPFINTERFACE_RAW as
  select * from dc.DC_E_CPP_OSPFINTERFACE_RAW_01 union all
  select * from dc.DC_E_CPP_OSPFINTERFACE_RAW_02 union all
  select * from dc.DC_E_CPP_OSPFINTERFACE_RAW_03;

create view dcpublic.DC_E_CPP_OSPFINTERFACE_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    IpOam,
    IpSystem,
    Ospf,
    OspfInterface,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfOspfIfEvents from
    dc.DC_E_CPP_OSPFINTERFACE_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_OSPFINTERFACE_DAY as
  select * from dc.DC_E_CPP_OSPFINTERFACE_DAY_01 union all
  select * from dc.DC_E_CPP_OSPFINTERFACE_DAY_02 union all
  select * from dc.DC_E_CPP_OSPFINTERFACE_DAY_03;

create view dcpublic.DC_E_CPP_OSPFINTERFACE_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    IpOam,
    IpSystem,
    Ospf,
    OspfInterface,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmNoOfOspfIfEvents from
    dc.DC_E_CPP_OSPFINTERFACE_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_OSPFINTERFACE_COUNT as
  select * from dc.DC_E_CPP_OSPFINTERFACE_COUNT_01 union all
  select * from dc.DC_E_CPP_OSPFINTERFACE_COUNT_02 union all
  select * from dc.DC_E_CPP_OSPFINTERFACE_COUNT_03;

create view dcpublic.DC_E_CPP_OSPFINTERFACE_COUNT
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    IpOam,
    IpSystem,
    Ospf,
    OspfInterface,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfOspfIfEvents from
    dc.DC_E_CPP_OSPFINTERFACE_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_PLUGINUNIT_RAW as
  select * from dc.DC_E_CPP_PLUGINUNIT_RAW_01 union all
  select * from dc.DC_E_CPP_PLUGINUNIT_RAW_02 union all
  select * from dc.DC_E_CPP_PLUGINUNIT_RAW_03;

create view dcpublic.DC_E_CPP_PLUGINUNIT_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmProcessorLoad from
    dc.DC_E_CPP_PLUGINUNIT_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_PLUGINUNIT_DAY as
  select * from dc.DC_E_CPP_PLUGINUNIT_DAY_01 union all
  select * from dc.DC_E_CPP_PLUGINUNIT_DAY_02 union all
  select * from dc.DC_E_CPP_PLUGINUNIT_DAY_03;

create view dcpublic.DC_E_CPP_PLUGINUNIT_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmProcessorLoad from
    dc.DC_E_CPP_PLUGINUNIT_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_PLUGINUNIT_COUNT as
  select * from dc.DC_E_CPP_PLUGINUNIT_COUNT_01 union all
  select * from dc.DC_E_CPP_PLUGINUNIT_COUNT_02 union all
  select * from dc.DC_E_CPP_PLUGINUNIT_COUNT_03;

create view dcpublic.DC_E_CPP_PLUGINUNIT_COUNT
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmProcessorLoad from
    dc.DC_E_CPP_PLUGINUNIT_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_SCCPACCOCRIT_RAW as
  select * from dc.DC_E_CPP_SCCPACCOCRIT_RAW_01 union all
  select * from dc.DC_E_CPP_SCCPACCOCRIT_RAW_02 union all
  select * from dc.DC_E_CPP_SCCPACCOCRIT_RAW_03 union all
  select * from dc.DC_E_CPP_SCCPACCOCRIT_RAW_04 union all
  select * from dc.DC_E_CPP_SCCPACCOCRIT_RAW_05 union all
  select * from dc.DC_E_CPP_SCCPACCOCRIT_RAW_06;

create view dcpublic.DC_E_CPP_SCCPACCOCRIT_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    SccpSp,
    SccpScrc,
    SccpAccountingCriteria,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfMsg,
    pmNoOfOctets from
    dc.DC_E_CPP_SCCPACCOCRIT_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_SCCPACCOCRIT_DAY as
  select * from dc.DC_E_CPP_SCCPACCOCRIT_DAY_01 union all
  select * from dc.DC_E_CPP_SCCPACCOCRIT_DAY_02 union all
  select * from dc.DC_E_CPP_SCCPACCOCRIT_DAY_03 union all
  select * from dc.DC_E_CPP_SCCPACCOCRIT_DAY_04 union all
  select * from dc.DC_E_CPP_SCCPACCOCRIT_DAY_05 union all
  select * from dc.DC_E_CPP_SCCPACCOCRIT_DAY_06;

create view dcpublic.DC_E_CPP_SCCPACCOCRIT_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    SccpSp,
    SccpScrc,
    SccpAccountingCriteria,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmNoOfMsg,
    pmNoOfOctets from
    dc.DC_E_CPP_SCCPACCOCRIT_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_SCCPACCOCRIT_COUNT as
  select * from dc.DC_E_CPP_SCCPACCOCRIT_COUNT_01 union all
  select * from dc.DC_E_CPP_SCCPACCOCRIT_COUNT_02 union all
  select * from dc.DC_E_CPP_SCCPACCOCRIT_COUNT_03 union all
  select * from dc.DC_E_CPP_SCCPACCOCRIT_COUNT_04 union all
  select * from dc.DC_E_CPP_SCCPACCOCRIT_COUNT_05 union all
  select * from dc.DC_E_CPP_SCCPACCOCRIT_COUNT_06;

create view dcpublic.DC_E_CPP_SCCPACCOCRIT_COUNT
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    SccpSp,
    SccpScrc,
    SccpAccountingCriteria,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfMsg,
    pmNoOfOctets from
    dc.DC_E_CPP_SCCPACCOCRIT_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_SCCPPOLICING_RAW as
  select * from dc.DC_E_CPP_SCCPPOLICING_RAW_01 union all
  select * from dc.DC_E_CPP_SCCPPOLICING_RAW_02 union all
  select * from dc.DC_E_CPP_SCCPPOLICING_RAW_03 union all
  select * from dc.DC_E_CPP_SCCPPOLICING_RAW_04 union all
  select * from dc.DC_E_CPP_SCCPPOLICING_RAW_05 union all
  select * from dc.DC_E_CPP_SCCPPOLICING_RAW_06;

create view dcpublic.DC_E_CPP_SCCPPOLICING_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    SccpSp,
    SccpScrc,
    SccpPolicing,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfRejectMsg from
    dc.DC_E_CPP_SCCPPOLICING_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_SCCPPOLICING_DAY as
  select * from dc.DC_E_CPP_SCCPPOLICING_DAY_01 union all
  select * from dc.DC_E_CPP_SCCPPOLICING_DAY_02 union all
  select * from dc.DC_E_CPP_SCCPPOLICING_DAY_03 union all
  select * from dc.DC_E_CPP_SCCPPOLICING_DAY_04 union all
  select * from dc.DC_E_CPP_SCCPPOLICING_DAY_05 union all
  select * from dc.DC_E_CPP_SCCPPOLICING_DAY_06;

create view dcpublic.DC_E_CPP_SCCPPOLICING_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    SccpSp,
    SccpScrc,
    SccpPolicing,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmNoOfRejectMsg from
    dc.DC_E_CPP_SCCPPOLICING_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_SCCPPOLICING_COUNT as
  select * from dc.DC_E_CPP_SCCPPOLICING_COUNT_01 union all
  select * from dc.DC_E_CPP_SCCPPOLICING_COUNT_02 union all
  select * from dc.DC_E_CPP_SCCPPOLICING_COUNT_03 union all
  select * from dc.DC_E_CPP_SCCPPOLICING_COUNT_04 union all
  select * from dc.DC_E_CPP_SCCPPOLICING_COUNT_05 union all
  select * from dc.DC_E_CPP_SCCPPOLICING_COUNT_06;

create view dcpublic.DC_E_CPP_SCCPPOLICING_COUNT
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    SccpSp,
    SccpScrc,
    SccpPolicing,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfRejectMsg from
    dc.DC_E_CPP_SCCPPOLICING_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_SCTP_RAW as
  select * from dc.DC_E_CPP_SCTP_RAW_01 union all
  select * from dc.DC_E_CPP_SCTP_RAW_02 union all
  select * from dc.DC_E_CPP_SCTP_RAW_03;

create view dcpublic.DC_E_CPP_SCTP_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Sctp,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmSctpAborted,
    pmSctpActiveEstab,
    pmSctpCurrEstab,
    pmSctpInErrors,
    pmSctpInNoPorts,
    pmSctpPassiveEstab,
    pmSctpShutdowns,
    pmSctpStatAssocOutOfBlue,
    pmSctpStatChecksumErrorCounter,
    pmSctpStatCommResume,
    pmSctpStatCommStop,
    pmSctpStatFragmentedUserMsg,
    pmSctpStatOutOfOrderRecChunks,
    pmSctpStatOutOfOrderSendChunks,
    pmSctpStatReassembledUserMsg,
    pmSctpStatRecChunks,
    pmSctpStatRecChunksDropped,
    pmSctpStatReceivedControlChunks,
    pmSctpStatReceivedPackages,
    pmSctpStatRetransChunks,
    pmSctpStatSentChunks,
    pmSctpStatSentChunksDropped,
    pmSctpStatSentControlChunks,
    pmSctpStatSentPackages from
    dc.DC_E_CPP_SCTP_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_SCTP_DAY as
  select * from dc.DC_E_CPP_SCTP_DAY_01 union all
  select * from dc.DC_E_CPP_SCTP_DAY_02 union all
  select * from dc.DC_E_CPP_SCTP_DAY_03;

create view dcpublic.DC_E_CPP_SCTP_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Sctp,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmSctpAborted,
    pmSctpActiveEstab,
    pmSctpCurrEstab,
    pmSctpInErrors,
    pmSctpInNoPorts,
    pmSctpPassiveEstab,
    pmSctpShutdowns,
    pmSctpStatAssocOutOfBlue,
    pmSctpStatChecksumErrorCounter,
    pmSctpStatCommResume,
    pmSctpStatCommStop,
    pmSctpStatFragmentedUserMsg,
    pmSctpStatOutOfOrderRecChunks,
    pmSctpStatOutOfOrderSendChunks,
    pmSctpStatReassembledUserMsg,
    pmSctpStatRecChunks,
    pmSctpStatRecChunksDropped,
    pmSctpStatReceivedControlChunks,
    pmSctpStatReceivedPackages,
    pmSctpStatRetransChunks,
    pmSctpStatSentChunks,
    pmSctpStatSentChunksDropped,
    pmSctpStatSentControlChunks,
    pmSctpStatSentPackages from
    dc.DC_E_CPP_SCTP_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_SCTP_COUNT as
  select * from dc.DC_E_CPP_SCTP_COUNT_01 union all
  select * from dc.DC_E_CPP_SCTP_COUNT_02 union all
  select * from dc.DC_E_CPP_SCTP_COUNT_03;

create view dcpublic.DC_E_CPP_SCTP_COUNT
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Sctp,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmSctpAborted,
    pmSctpActiveEstab,
    pmSctpCurrEstab,
    pmSctpInErrors,
    pmSctpInNoPorts,
    pmSctpPassiveEstab,
    pmSctpShutdowns,
    pmSctpStatAssocOutOfBlue,
    pmSctpStatChecksumErrorCounter,
    pmSctpStatCommResume,
    pmSctpStatCommStop,
    pmSctpStatFragmentedUserMsg,
    pmSctpStatOutOfOrderRecChunks,
    pmSctpStatOutOfOrderSendChunks,
    pmSctpStatReassembledUserMsg,
    pmSctpStatRecChunks,
    pmSctpStatRecChunksDropped,
    pmSctpStatReceivedControlChunks,
    pmSctpStatReceivedPackages,
    pmSctpStatRetransChunks,
    pmSctpStatSentChunks,
    pmSctpStatSentChunksDropped,
    pmSctpStatSentControlChunks,
    pmSctpStatSentPackages from
    dc.DC_E_CPP_SCTP_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_STS1SPETTP_RAW as
  select * from dc.DC_E_CPP_STS1SPETTP_RAW_01 union all
  select * from dc.DC_E_CPP_STS1SPETTP_RAW_02 union all
  select * from dc.DC_E_CPP_STS1SPETTP_RAW_03 union all
  select * from dc.DC_E_CPP_STS1SPETTP_RAW_04 union all
  select * from dc.DC_E_CPP_STS1SPETTP_RAW_05 union all
  select * from dc.DC_E_CPP_STS1SPETTP_RAW_06;

create view dcpublic.DC_E_CPP_STS1SPETTP_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    Cbu,
    ExchangeTerminal,
    Os155SpiTtp,
    Sts1SpeTtp,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmEsp,
    pmSesp,
    pmUasp from
    dc.DC_E_CPP_STS1SPETTP_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_STS1SPETTP_DAY as
  select * from dc.DC_E_CPP_STS1SPETTP_DAY_01 union all
  select * from dc.DC_E_CPP_STS1SPETTP_DAY_02 union all
  select * from dc.DC_E_CPP_STS1SPETTP_DAY_03 union all
  select * from dc.DC_E_CPP_STS1SPETTP_DAY_04 union all
  select * from dc.DC_E_CPP_STS1SPETTP_DAY_05 union all
  select * from dc.DC_E_CPP_STS1SPETTP_DAY_06;

create view dcpublic.DC_E_CPP_STS1SPETTP_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    Cbu,
    ExchangeTerminal,
    Os155SpiTtp,
    Sts1SpeTtp,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmEsp,
    pmSesp,
    pmUasp from
    dc.DC_E_CPP_STS1SPETTP_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_STS3CSPETTP_RAW as
  select * from dc.DC_E_CPP_STS3CSPETTP_RAW_01 union all
  select * from dc.DC_E_CPP_STS3CSPETTP_RAW_02 union all
  select * from dc.DC_E_CPP_STS3CSPETTP_RAW_03 union all
  select * from dc.DC_E_CPP_STS3CSPETTP_RAW_04 union all
  select * from dc.DC_E_CPP_STS3CSPETTP_RAW_05 union all
  select * from dc.DC_E_CPP_STS3CSPETTP_RAW_06;

create view dcpublic.DC_E_CPP_STS3CSPETTP_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    Cbu,
    ExchangeTerminal,
    Os155SpiTtp,
    Sts3CspeTtp,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmEsp,
    pmSesp,
    pmUasp from
    dc.DC_E_CPP_STS3CSPETTP_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_STS3CSPETTP_DAY as
  select * from dc.DC_E_CPP_STS3CSPETTP_DAY_01 union all
  select * from dc.DC_E_CPP_STS3CSPETTP_DAY_02 union all
  select * from dc.DC_E_CPP_STS3CSPETTP_DAY_03 union all
  select * from dc.DC_E_CPP_STS3CSPETTP_DAY_04 union all
  select * from dc.DC_E_CPP_STS3CSPETTP_DAY_05 union all
  select * from dc.DC_E_CPP_STS3CSPETTP_DAY_06;

create view dcpublic.DC_E_CPP_STS3CSPETTP_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    Cbu,
    ExchangeTerminal,
    Os155SpiTtp,
    Sts3CspeTtp,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmEsp,
    pmSesp,
    pmUasp from
    dc.DC_E_CPP_STS3CSPETTP_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_T1PHYSPATHTERM_RAW as
  select * from dc.DC_E_CPP_T1PHYSPATHTERM_RAW_01 union all
  select * from dc.DC_E_CPP_T1PHYSPATHTERM_RAW_02 union all
  select * from dc.DC_E_CPP_T1PHYSPATHTERM_RAW_03 union all
  select * from dc.DC_E_CPP_T1PHYSPATHTERM_RAW_04 union all
  select * from dc.DC_E_CPP_T1PHYSPATHTERM_RAW_05 union all
  select * from dc.DC_E_CPP_T1PHYSPATHTERM_RAW_06;

create view dcpublic.DC_E_CPP_T1PHYSPATHTERM_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    Cbu,
    ExchangeTerminal,
    T1PhysPathTerm,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmEs,
    pmSes,
    pmUas from
    dc.DC_E_CPP_T1PHYSPATHTERM_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_T1PHYSPATHTERM_DAY as
  select * from dc.DC_E_CPP_T1PHYSPATHTERM_DAY_01 union all
  select * from dc.DC_E_CPP_T1PHYSPATHTERM_DAY_02 union all
  select * from dc.DC_E_CPP_T1PHYSPATHTERM_DAY_03 union all
  select * from dc.DC_E_CPP_T1PHYSPATHTERM_DAY_04 union all
  select * from dc.DC_E_CPP_T1PHYSPATHTERM_DAY_05 union all
  select * from dc.DC_E_CPP_T1PHYSPATHTERM_DAY_06;

create view dcpublic.DC_E_CPP_T1PHYSPATHTERM_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    Cbu,
    ExchangeTerminal,
    T1PhysPathTerm,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmEs,
    pmSes,
    pmUas from
    dc.DC_E_CPP_T1PHYSPATHTERM_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_T1TTP_RAW as
  select * from dc.DC_E_CPP_T1TTP_RAW_01 union all
  select * from dc.DC_E_CPP_T1TTP_RAW_02 union all
  select * from dc.DC_E_CPP_T1TTP_RAW_03 union all
  select * from dc.DC_E_CPP_T1TTP_RAW_04 union all
  select * from dc.DC_E_CPP_T1TTP_RAW_05 union all
  select * from dc.DC_E_CPP_T1TTP_RAW_06;

create view dcpublic.DC_E_CPP_T1TTP_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    Cbu,
    ExchangeTerminal,
    Os155SpiTtp,
    Sts1SpeTtp,
    Vt15Ttp,
    T1Ttp,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmEs,
    pmSes,
    pmUas from
    dc.DC_E_CPP_T1TTP_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_T1TTP_DAY as
  select * from dc.DC_E_CPP_T1TTP_DAY_01 union all
  select * from dc.DC_E_CPP_T1TTP_DAY_02 union all
  select * from dc.DC_E_CPP_T1TTP_DAY_03 union all
  select * from dc.DC_E_CPP_T1TTP_DAY_04 union all
  select * from dc.DC_E_CPP_T1TTP_DAY_05 union all
  select * from dc.DC_E_CPP_T1TTP_DAY_06;

create view dcpublic.DC_E_CPP_T1TTP_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    Cbu,
    ExchangeTerminal,
    Os155SpiTtp,
    Sts1SpeTtp,
    Vt15Ttp,
    T1Ttp,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmEs,
    pmSes,
    pmUas from
    dc.DC_E_CPP_T1TTP_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_T3PHYSPATHTERM_RAW as
  select * from dc.DC_E_CPP_T3PHYSPATHTERM_RAW_01 union all
  select * from dc.DC_E_CPP_T3PHYSPATHTERM_RAW_02 union all
  select * from dc.DC_E_CPP_T3PHYSPATHTERM_RAW_03 union all
  select * from dc.DC_E_CPP_T3PHYSPATHTERM_RAW_04 union all
  select * from dc.DC_E_CPP_T3PHYSPATHTERM_RAW_05 union all
  select * from dc.DC_E_CPP_T3PHYSPATHTERM_RAW_06;

create view dcpublic.DC_E_CPP_T3PHYSPATHTERM_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    Cbu,
    ExchangeTerminal,
    T3PhysPathTerm,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmEsCpp,
    pmSesCpp,
    pmUas from
    dc.DC_E_CPP_T3PHYSPATHTERM_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_T3PHYSPATHTERM_DAY as
  select * from dc.DC_E_CPP_T3PHYSPATHTERM_DAY_01 union all
  select * from dc.DC_E_CPP_T3PHYSPATHTERM_DAY_02 union all
  select * from dc.DC_E_CPP_T3PHYSPATHTERM_DAY_03 union all
  select * from dc.DC_E_CPP_T3PHYSPATHTERM_DAY_04 union all
  select * from dc.DC_E_CPP_T3PHYSPATHTERM_DAY_05 union all
  select * from dc.DC_E_CPP_T3PHYSPATHTERM_DAY_06;

create view dcpublic.DC_E_CPP_T3PHYSPATHTERM_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    Cbu,
    ExchangeTerminal,
    T3PhysPathTerm,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmEsCpp,
    pmSesCpp,
    pmUas from
    dc.DC_E_CPP_T3PHYSPATHTERM_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_UNISAALTP_RAW as
  select * from dc.DC_E_CPP_UNISAALTP_RAW_01 union all
  select * from dc.DC_E_CPP_UNISAALTP_RAW_02 union all
  select * from dc.DC_E_CPP_UNISAALTP_RAW_03 union all
  select * from dc.DC_E_CPP_UNISAALTP_RAW_04 union all
  select * from dc.DC_E_CPP_UNISAALTP_RAW_05 union all
  select * from dc.DC_E_CPP_UNISAALTP_RAW_06;

create view dcpublic.DC_E_CPP_UNISAALTP_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    UniSaalTp,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmLinkInServiceTime,
    pmNoOfAllSLFailures,
    pmNoOfLocalCongestions,
    pmNoOfNoResponses,
    pmNoOfOtherErrors,
    pmNoOfProtocolErrors,
    pmNoOfReceivedSDUs,
    pmNoOfRemoteCongestions,
    pmNoOfSentSDUs,
    pmNoOfSequenceDataLosses,
    pmNoOfUnsuccReTransmissions from
    dc.DC_E_CPP_UNISAALTP_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_UNISAALTP_DAY as
  select * from dc.DC_E_CPP_UNISAALTP_DAY_01 union all
  select * from dc.DC_E_CPP_UNISAALTP_DAY_02 union all
  select * from dc.DC_E_CPP_UNISAALTP_DAY_03 union all
  select * from dc.DC_E_CPP_UNISAALTP_DAY_04 union all
  select * from dc.DC_E_CPP_UNISAALTP_DAY_05 union all
  select * from dc.DC_E_CPP_UNISAALTP_DAY_06;

create view dcpublic.DC_E_CPP_UNISAALTP_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    UniSaalTp,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmLinkInServiceTime,
    pmNoOfAllSLFailures,
    pmNoOfLocalCongestions,
    pmNoOfNoResponses,
    pmNoOfOtherErrors,
    pmNoOfProtocolErrors,
    pmNoOfReceivedSDUs,
    pmNoOfRemoteCongestions,
    pmNoOfSentSDUs,
    pmNoOfSequenceDataLosses,
    pmNoOfUnsuccReTransmissions from
    dc.DC_E_CPP_UNISAALTP_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_UNISAALTP_COUNT as
  select * from dc.DC_E_CPP_UNISAALTP_COUNT_01 union all
  select * from dc.DC_E_CPP_UNISAALTP_COUNT_02 union all
  select * from dc.DC_E_CPP_UNISAALTP_COUNT_03 union all
  select * from dc.DC_E_CPP_UNISAALTP_COUNT_04 union all
  select * from dc.DC_E_CPP_UNISAALTP_COUNT_05 union all
  select * from dc.DC_E_CPP_UNISAALTP_COUNT_06;

create view dcpublic.DC_E_CPP_UNISAALTP_COUNT
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    UniSaalTp,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmLinkInServiceTime,
    pmNoOfAllSLFailures,
    pmNoOfLocalCongestions,
    pmNoOfNoResponses,
    pmNoOfOtherErrors,
    pmNoOfProtocolErrors,
    pmNoOfReceivedSDUs,
    pmNoOfRemoteCongestions,
    pmNoOfSentSDUs,
    pmNoOfSequenceDataLosses,
    pmNoOfUnsuccReTransmissions from
    dc.DC_E_CPP_UNISAALTP_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_VC11TTP_RAW as
  select * from dc.DC_E_CPP_VC11TTP_RAW_01 union all
  select * from dc.DC_E_CPP_VC11TTP_RAW_02 union all
  select * from dc.DC_E_CPP_VC11TTP_RAW_03 union all
  select * from dc.DC_E_CPP_VC11TTP_RAW_04 union all
  select * from dc.DC_E_CPP_VC11TTP_RAW_05 union all
  select * from dc.DC_E_CPP_VC11TTP_RAW_06;

create view dcpublic.DC_E_CPP_VC11TTP_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    Cbu,
    ExchangeTerminal,
    Os155SpiTtp,
    Vc3Ttp,
    Vc11Ttp,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmVcBbe,
    pmVcEs,
    pmVcSes,
    pmVcUas from
    dc.DC_E_CPP_VC11TTP_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_VC11TTP_DAY as
  select * from dc.DC_E_CPP_VC11TTP_DAY_01 union all
  select * from dc.DC_E_CPP_VC11TTP_DAY_02 union all
  select * from dc.DC_E_CPP_VC11TTP_DAY_03 union all
  select * from dc.DC_E_CPP_VC11TTP_DAY_04 union all
  select * from dc.DC_E_CPP_VC11TTP_DAY_05 union all
  select * from dc.DC_E_CPP_VC11TTP_DAY_06;

create view dcpublic.DC_E_CPP_VC11TTP_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    Cbu,
    ExchangeTerminal,
    Os155SpiTtp,
    Vc3Ttp,
    Vc11Ttp,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmVcBbe,
    pmVcEs,
    pmVcSes,
    pmVcUas from
    dc.DC_E_CPP_VC11TTP_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_VC11TTP_COUNT as
  select * from dc.DC_E_CPP_VC11TTP_COUNT_01 union all
  select * from dc.DC_E_CPP_VC11TTP_COUNT_02 union all
  select * from dc.DC_E_CPP_VC11TTP_COUNT_03 union all
  select * from dc.DC_E_CPP_VC11TTP_COUNT_04 union all
  select * from dc.DC_E_CPP_VC11TTP_COUNT_05 union all
  select * from dc.DC_E_CPP_VC11TTP_COUNT_06;

create view dcpublic.DC_E_CPP_VC11TTP_COUNT
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    Cbu,
    ExchangeTerminal,
    Os155SpiTtp,
    Vc3Ttp,
    Vc11Ttp,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmVcBbe,
    pmVcEs,
    pmVcSes,
    pmVcUas from
    dc.DC_E_CPP_VC11TTP_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_VC3TTP_RAW as
  select * from dc.DC_E_CPP_VC3TTP_RAW_01 union all
  select * from dc.DC_E_CPP_VC3TTP_RAW_02 union all
  select * from dc.DC_E_CPP_VC3TTP_RAW_03 union all
  select * from dc.DC_E_CPP_VC3TTP_RAW_04 union all
  select * from dc.DC_E_CPP_VC3TTP_RAW_05 union all
  select * from dc.DC_E_CPP_VC3TTP_RAW_06;

create view dcpublic.DC_E_CPP_VC3TTP_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    Cbu,
    ExchangeTerminal,
    Os155SpiTtp,
    Vc3Ttp,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmVcBbe,
    pmVcEs,
    pmVcSes,
    pmVcUas from
    dc.DC_E_CPP_VC3TTP_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_VC3TTP_DAY as
  select * from dc.DC_E_CPP_VC3TTP_DAY_01 union all
  select * from dc.DC_E_CPP_VC3TTP_DAY_02 union all
  select * from dc.DC_E_CPP_VC3TTP_DAY_03 union all
  select * from dc.DC_E_CPP_VC3TTP_DAY_04 union all
  select * from dc.DC_E_CPP_VC3TTP_DAY_05 union all
  select * from dc.DC_E_CPP_VC3TTP_DAY_06;

create view dcpublic.DC_E_CPP_VC3TTP_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    Cbu,
    ExchangeTerminal,
    Os155SpiTtp,
    Vc3Ttp,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmVcBbe,
    pmVcEs,
    pmVcSes,
    pmVcUas from
    dc.DC_E_CPP_VC3TTP_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_VC3TTP_COUNT as
  select * from dc.DC_E_CPP_VC3TTP_COUNT_01 union all
  select * from dc.DC_E_CPP_VC3TTP_COUNT_02 union all
  select * from dc.DC_E_CPP_VC3TTP_COUNT_03 union all
  select * from dc.DC_E_CPP_VC3TTP_COUNT_04 union all
  select * from dc.DC_E_CPP_VC3TTP_COUNT_05 union all
  select * from dc.DC_E_CPP_VC3TTP_COUNT_06;

create view dcpublic.DC_E_CPP_VC3TTP_COUNT
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    Cbu,
    ExchangeTerminal,
    Os155SpiTtp,
    Vc3Ttp,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmVcBbe,
    pmVcEs,
    pmVcSes,
    pmVcUas from
    dc.DC_E_CPP_VC3TTP_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_VC4TTP_RAW as
  select * from dc.DC_E_CPP_VC4TTP_RAW_01 union all
  select * from dc.DC_E_CPP_VC4TTP_RAW_02 union all
  select * from dc.DC_E_CPP_VC4TTP_RAW_03 union all
  select * from dc.DC_E_CPP_VC4TTP_RAW_04 union all
  select * from dc.DC_E_CPP_VC4TTP_RAW_05 union all
  select * from dc.DC_E_CPP_VC4TTP_RAW_06;

create view dcpublic.DC_E_CPP_VC4TTP_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    Cbu,
    ExchangeTerminal,
    Os155SpiTtp,
    Vc4Ttp,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmVcBbe,
    pmVcEs,
    pmVcSes,
    pmVcUas from
    dc.DC_E_CPP_VC4TTP_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_VC4TTP_DAY as
  select * from dc.DC_E_CPP_VC4TTP_DAY_01 union all
  select * from dc.DC_E_CPP_VC4TTP_DAY_02 union all
  select * from dc.DC_E_CPP_VC4TTP_DAY_03 union all
  select * from dc.DC_E_CPP_VC4TTP_DAY_04 union all
  select * from dc.DC_E_CPP_VC4TTP_DAY_05 union all
  select * from dc.DC_E_CPP_VC4TTP_DAY_06;

create view dcpublic.DC_E_CPP_VC4TTP_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    Cbu,
    ExchangeTerminal,
    Os155SpiTtp,
    Vc4Ttp,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmVcBbe,
    pmVcEs,
    pmVcSes,
    pmVcUas from
    dc.DC_E_CPP_VC4TTP_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_VT15TTP_RAW as
  select * from dc.DC_E_CPP_VT15TTP_RAW_01 union all
  select * from dc.DC_E_CPP_VT15TTP_RAW_02 union all
  select * from dc.DC_E_CPP_VT15TTP_RAW_03 union all
  select * from dc.DC_E_CPP_VT15TTP_RAW_04 union all
  select * from dc.DC_E_CPP_VT15TTP_RAW_05 union all
  select * from dc.DC_E_CPP_VT15TTP_RAW_06;

create view dcpublic.DC_E_CPP_VT15TTP_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    Cbu,
    ExchangeTerminal,
    Os155SpiTtp,
    Sts1SpeTtp,
    Vt15Ttp,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmEs,
    pmSes,
    pmUas from
    dc.DC_E_CPP_VT15TTP_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_VT15TTP_DAY as
  select * from dc.DC_E_CPP_VT15TTP_DAY_01 union all
  select * from dc.DC_E_CPP_VT15TTP_DAY_02 union all
  select * from dc.DC_E_CPP_VT15TTP_DAY_03 union all
  select * from dc.DC_E_CPP_VT15TTP_DAY_04 union all
  select * from dc.DC_E_CPP_VT15TTP_DAY_05 union all
  select * from dc.DC_E_CPP_VT15TTP_DAY_06;

create view dcpublic.DC_E_CPP_VT15TTP_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    Cbu,
    ExchangeTerminal,
    Os155SpiTtp,
    Sts1SpeTtp,
    Vt15Ttp,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmEs,
    pmSes,
    pmUas from
    dc.DC_E_CPP_VT15TTP_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_SYNCHRONIZATION_RAW as
  select * from dc.DC_E_CPP_SYNCHRONIZATION_RAW_01 union all
  select * from dc.DC_E_CPP_SYNCHRONIZATION_RAW_02 union all
  select * from dc.DC_E_CPP_SYNCHRONIZATION_RAW_03;

create view dcpublic.DC_E_CPP_SYNCHRONIZATION_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Synchronization,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmMaxDelayVariation,
    pmHDelayVarBest10Pct,
    pmHDelayVarBest1Pct,
    pmHDelayVarBest50Pct from
    dc.DC_E_CPP_SYNCHRONIZATION_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_SYNCHRONIZATION_DAY as
  select * from dc.DC_E_CPP_SYNCHRONIZATION_DAY_01 union all
  select * from dc.DC_E_CPP_SYNCHRONIZATION_DAY_02 union all
  select * from dc.DC_E_CPP_SYNCHRONIZATION_DAY_03;

create view dcpublic.DC_E_CPP_SYNCHRONIZATION_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Synchronization,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmMaxDelayVariation,
    pmHDelayVarBest10Pct,
    pmHDelayVarBest1Pct,
    pmHDelayVarBest50Pct from
    dc.DC_E_CPP_SYNCHRONIZATION_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_INTETHERPORT_RAW as
  select * from dc.DC_E_CPP_INTETHERPORT_RAW_01 union all
  select * from dc.DC_E_CPP_INTETHERPORT_RAW_02 union all
  select * from dc.DC_E_CPP_INTETHERPORT_RAW_03;

create view dcpublic.DC_E_CPP_INTETHERPORT_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    ExchangeTerminalIp,
    InternalEthernetPort,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmDot1qTpVlanPortInDiscardsLink,
    pmIfInBroadcastPkts,
    pmIfInDiscards,
    pmIfInErrors,
    pmIfInMulticastPkts,
    pmIfInOctetsHi,
    pmIfInOctetsLo,
    pmIfInUcastPkts,
    pmIfInUnknownProtos,
    pmIfOutBroadcastPkts,
    pmIfOutDiscards,
    pmIfOutErrors,
    pmIfOutMulticastPkts,
    pmIfOutOctetsHi,
    pmIfOutOctetsLo,
    pmIfOutUcastPkts from
    dc.DC_E_CPP_INTETHERPORT_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_INTETHERPORT_DAY as
  select * from dc.DC_E_CPP_INTETHERPORT_DAY_01 union all
  select * from dc.DC_E_CPP_INTETHERPORT_DAY_02 union all
  select * from dc.DC_E_CPP_INTETHERPORT_DAY_03;

create view dcpublic.DC_E_CPP_INTETHERPORT_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    ExchangeTerminalIp,
    InternalEthernetPort,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmDot1qTpVlanPortInDiscardsLink,
    pmIfInBroadcastPkts,
    pmIfInDiscards,
    pmIfInErrors,
    pmIfInMulticastPkts,
    pmIfInOctetsHi,
    pmIfInOctetsLo,
    pmIfInUcastPkts,
    pmIfInUnknownProtos,
    pmIfOutBroadcastPkts,
    pmIfOutDiscards,
    pmIfOutErrors,
    pmIfOutMulticastPkts,
    pmIfOutOctetsHi,
    pmIfOutOctetsLo,
    pmIfOutUcastPkts from
    dc.DC_E_CPP_INTETHERPORT_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_ETHERNETSP_RAW as
  select * from dc.DC_E_CPP_ETHERNETSP_RAW_01 union all
  select * from dc.DC_E_CPP_ETHERNETSP_RAW_02 union all
  select * from dc.DC_E_CPP_ETHERNETSP_RAW_03 union all
  select * from dc.DC_E_CPP_ETHERNETSP_RAW_04 union all
  select * from dc.DC_E_CPP_ETHERNETSP_RAW_05 union all
  select * from dc.DC_E_CPP_ETHERNETSP_RAW_06;

create view dcpublic.DC_E_CPP_ETHERNETSP_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    ExchangeTerminalIp,
    EthernetSwitch,
    EthernetSwitchPort,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmIfInBroadcastPkts,
    pmIfInDiscards,
    pmIfInErrors,
    pmIfInMulticastPkts,
    pmIfInOctetsHi,
    pmIfInOctetsLo,
    pmIfInUcastPkts,
    pmIfOutBroadcastPkts,
    pmIfOutDiscards,
    pmIfOutErrors,
    pmIfOutMulticastPkts,
    pmIfOutOctetsHi,
    pmIfOutOctetsLo,
    pmIfOutUcastPkts from
    dc.DC_E_CPP_ETHERNETSP_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_ETHERNETSP_DAY as
  select * from dc.DC_E_CPP_ETHERNETSP_DAY_01 union all
  select * from dc.DC_E_CPP_ETHERNETSP_DAY_02 union all
  select * from dc.DC_E_CPP_ETHERNETSP_DAY_03 union all
  select * from dc.DC_E_CPP_ETHERNETSP_DAY_04 union all
  select * from dc.DC_E_CPP_ETHERNETSP_DAY_05 union all
  select * from dc.DC_E_CPP_ETHERNETSP_DAY_06;

create view dcpublic.DC_E_CPP_ETHERNETSP_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    ExchangeTerminalIp,
    EthernetSwitch,
    EthernetSwitchPort,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmIfInBroadcastPkts,
    pmIfInDiscards,
    pmIfInErrors,
    pmIfInMulticastPkts,
    pmIfInOctetsHi,
    pmIfInOctetsLo,
    pmIfInUcastPkts,
    pmIfOutBroadcastPkts,
    pmIfOutDiscards,
    pmIfOutErrors,
    pmIfOutMulticastPkts,
    pmIfOutOctetsHi,
    pmIfOutOctetsLo,
    pmIfOutUcastPkts from
    dc.DC_E_CPP_ETHERNETSP_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_IPACCESSHOSTET_RAW as
  select * from dc.DC_E_CPP_IPACCESSHOSTET_RAW_01 union all
  select * from dc.DC_E_CPP_IPACCESSHOSTET_RAW_02 union all
  select * from dc.DC_E_CPP_IPACCESSHOSTET_RAW_03;

create view dcpublic.DC_E_CPP_IPACCESSHOSTET_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    IpSystem,
    IpAccessHostEt,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmIcmpInDestUnreachs,
    pmIcmpInEchoReps,
    pmIcmpInEchos,
    pmIcmpInErrors,
    pmIcmpInMsgs,
    pmIcmpInParamProbs,
    pmIcmpInRedirects,
    pmIcmpInSrcQuenchs,
    pmIcmpInTimeExcds,
    pmIcmpOutDestUnreachs,
    pmIcmpOutEchoReps,
    pmIcmpOutEchos,
    pmIcmpOutErrors,
    pmIcmpOutMsgs,
    pmIcmpOutParamProbs,
    pmIpInAddrErrors,
    pmIpInDelivers,
    pmIpInDiscards,
    pmIpInHdrErrors,
    pmIpInReceives,
    pmIpInUnknownProtos,
    pmIpOutDiscards,
    pmIpOutRequests,
    pmUdpInDatagrams,
    pmUdpInErrors,
    pmUdpNoPorts,
    pmUdpOutDatagrams,
    pmIpFragCreates,
    pmIpFragFails,
    pmIpFragOks,
    pmIpPortUnreachable,
    pmIpReasmFails,
    pmIpReasmOks,
    pmIpReasmReqds from
    dc.DC_E_CPP_IPACCESSHOSTET_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_IPACCESSHOSTET_DAY as
  select * from dc.DC_E_CPP_IPACCESSHOSTET_DAY_01 union all
  select * from dc.DC_E_CPP_IPACCESSHOSTET_DAY_02 union all
  select * from dc.DC_E_CPP_IPACCESSHOSTET_DAY_03;

create view dcpublic.DC_E_CPP_IPACCESSHOSTET_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    IpSystem,
    IpAccessHostEt,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmIcmpInDestUnreachs,
    pmIcmpInEchoReps,
    pmIcmpInEchos,
    pmIcmpInErrors,
    pmIcmpInMsgs,
    pmIcmpInParamProbs,
    pmIcmpInRedirects,
    pmIcmpInSrcQuenchs,
    pmIcmpInTimeExcds,
    pmIcmpOutDestUnreachs,
    pmIcmpOutEchoReps,
    pmIcmpOutEchos,
    pmIcmpOutErrors,
    pmIcmpOutMsgs,
    pmIcmpOutParamProbs,
    pmIpInAddrErrors,
    pmIpInDelivers,
    pmIpInDiscards,
    pmIpInHdrErrors,
    pmIpInReceives,
    pmIpInUnknownProtos,
    pmIpOutDiscards,
    pmIpOutRequests,
    pmUdpInDatagrams,
    pmUdpInErrors,
    pmUdpNoPorts,
    pmUdpOutDatagrams,
    pmIpFragCreates,
    pmIpFragFails,
    pmIpFragOks,
    pmIpPortUnreachable,
    pmIpReasmFails,
    pmIpReasmOks,
    pmIpReasmReqds from
    dc.DC_E_CPP_IPACCESSHOSTET_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MTP2HSLTPCHINA_RAW as
  select * from dc.DC_E_CPP_MTP2HSLTPCHINA_RAW_01 union all
  select * from dc.DC_E_CPP_MTP2HSLTPCHINA_RAW_02 union all
  select * from dc.DC_E_CPP_MTP2HSLTPCHINA_RAW_03;

create view dcpublic.DC_E_CPP_MTP2HSLTPCHINA_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp2Hsl,
    Mtp2HslTpChina,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmLocalSIBTime,
    pmNoOfMSUReceived,
    pmNoOfMSUTransmitted,
    pmNoOfNacks,
    pmNoOfReTransmittedOctets,
    pmNoOfSIOSIFReceived,
    pmNoOfSIOSIFTransmitted,
    pmNoOfStartedRBCongestion,
    pmNoOfSuReceivedInError,
    pmRemoteSIBTime from
    dc.DC_E_CPP_MTP2HSLTPCHINA_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MTP2HSLTPCHINA_DAY as
  select * from dc.DC_E_CPP_MTP2HSLTPCHINA_DAY_01 union all
  select * from dc.DC_E_CPP_MTP2HSLTPCHINA_DAY_02 union all
  select * from dc.DC_E_CPP_MTP2HSLTPCHINA_DAY_03;

create view dcpublic.DC_E_CPP_MTP2HSLTPCHINA_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp2Hsl,
    Mtp2HslTpChina,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmLocalSIBTime,
    pmNoOfMSUReceived,
    pmNoOfMSUTransmitted,
    pmNoOfNacks,
    pmNoOfReTransmittedOctets,
    pmNoOfSIOSIFReceived,
    pmNoOfSIOSIFTransmitted,
    pmNoOfStartedRBCongestion,
    pmNoOfSuReceivedInError,
    pmRemoteSIBTime from
    dc.DC_E_CPP_MTP2HSLTPCHINA_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MTP2HSLTPITU_RAW as
  select * from dc.DC_E_CPP_MTP2HSLTPITU_RAW_01 union all
  select * from dc.DC_E_CPP_MTP2HSLTPITU_RAW_02 union all
  select * from dc.DC_E_CPP_MTP2HSLTPITU_RAW_03;

create view dcpublic.DC_E_CPP_MTP2HSLTPITU_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp2Hsl,
    Mtp2HslTpItu,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmLocalSIBTime,
    pmNoOfMSUReceived,
    pmNoOfMSUTransmitted,
    pmNoOfNacks,
    pmNoOfReTransmittedOctets,
    pmNoOfSIOSIFReceived,
    pmNoOfSIOSIFTransmitted,
    pmNoOfStartedRBCongestion,
    pmNoOfSuReceivedInError,
    pmRemoteSIBTime from
    dc.DC_E_CPP_MTP2HSLTPITU_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_MTP2HSLTPITU_DAY as
  select * from dc.DC_E_CPP_MTP2HSLTPITU_DAY_01 union all
  select * from dc.DC_E_CPP_MTP2HSLTPITU_DAY_02 union all
  select * from dc.DC_E_CPP_MTP2HSLTPITU_DAY_03;

create view dcpublic.DC_E_CPP_MTP2HSLTPITU_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Mtp2Hsl,
    Mtp2HslTpItu,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmLocalSIBTime,
    pmNoOfMSUReceived,
    pmNoOfMSUTransmitted,
    pmNoOfNacks,
    pmNoOfReTransmittedOctets,
    pmNoOfSIOSIFReceived,
    pmNoOfSIOSIFTransmitted,
    pmNoOfStartedRBCongestion,
    pmNoOfSuReceivedInError,
    pmRemoteSIBTime from
    dc.DC_E_CPP_MTP2HSLTPITU_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_SWITCHPORTSTP_RAW as
  select * from dc.DC_E_CPP_SWITCHPORTSTP_RAW_01 union all
  select * from dc.DC_E_CPP_SWITCHPORTSTP_RAW_02 union all
  select * from dc.DC_E_CPP_SWITCHPORTSTP_RAW_03 union all
  select * from dc.DC_E_CPP_SWITCHPORTSTP_RAW_04 union all
  select * from dc.DC_E_CPP_SWITCHPORTSTP_RAW_05 union all
  select * from dc.DC_E_CPP_SWITCHPORTSTP_RAW_06;

create view dcpublic.DC_E_CPP_SWITCHPORTSTP_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    ExchangeTerminalIp,
    EthernetSwitch,
    EthernetSwitchPort,
    SwitchPortStp,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmReceivedBpdu,
    pmTransmittedBpdu from
    dc.DC_E_CPP_SWITCHPORTSTP_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_SWITCHPORTSTP_DAY as
  select * from dc.DC_E_CPP_SWITCHPORTSTP_DAY_01 union all
  select * from dc.DC_E_CPP_SWITCHPORTSTP_DAY_02 union all
  select * from dc.DC_E_CPP_SWITCHPORTSTP_DAY_03 union all
  select * from dc.DC_E_CPP_SWITCHPORTSTP_DAY_04 union all
  select * from dc.DC_E_CPP_SWITCHPORTSTP_DAY_05 union all
  select * from dc.DC_E_CPP_SWITCHPORTSTP_DAY_06;

create view dcpublic.DC_E_CPP_SWITCHPORTSTP_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    ExchangeTerminalIp,
    EthernetSwitch,
    EthernetSwitchPort,
    SwitchPortStp,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmReceivedBpdu,
    pmTransmittedBpdu from
    dc.DC_E_CPP_SWITCHPORTSTP_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_SWITCHSTP_RAW as
  select * from dc.DC_E_CPP_SWITCHSTP_RAW_01 union all
  select * from dc.DC_E_CPP_SWITCHSTP_RAW_02 union all
  select * from dc.DC_E_CPP_SWITCHSTP_RAW_03;

create view dcpublic.DC_E_CPP_SWITCHSTP_RAW
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    ExchangeTerminalIp,
    EthernetSwitch,
    SwitchStp,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmTopologyChanges from
    dc.DC_E_CPP_SWITCHSTP_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_SWITCHSTP_DAY as
  select * from dc.DC_E_CPP_SWITCHSTP_DAY_01 union all
  select * from dc.DC_E_CPP_SWITCHSTP_DAY_02 union all
  select * from dc.DC_E_CPP_SWITCHSTP_DAY_03;

create view dcpublic.DC_E_CPP_SWITCHSTP_DAY
  as select OSS_ID,
    SN,
    NESW,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    Equipment,
    Subrack,
    Slot,
    PlugInUnit,
    ExchangeTerminalIp,
    EthernetSwitch,
    SwitchStp,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmTopologyChanges from
    dc.DC_E_CPP_SWITCHSTP_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_VCLTPBH_RANKBH as
  select * from dc.DC_E_CPP_VCLTPBH_RANKBH_01 union all
  select * from dc.DC_E_CPP_VCLTPBH_RANKBH_02 union all
  select * from dc.DC_E_CPP_VCLTPBH_RANKBH_03 union all
  select * from dc.DC_E_CPP_VCLTPBH_RANKBH_04 union all
  select * from dc.DC_E_CPP_VCLTPBH_RANKBH_05 union all
  select * from dc.DC_E_CPP_VCLTPBH_RANKBH_06;

create view dcpublic.DC_E_CPP_VCLTPBH_RANKBH
  as select OSS_ID,
    SN,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    AtmPort,
    VplTp,
    VpcTp,
    VclTp,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG from
    dc.DC_E_CPP_VCLTPBH_RANKBH where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_CPP_AAL2APBH_RANKBH as
  select * from dc.DC_E_CPP_AAL2APBH_RANKBH_01 union all
  select * from dc.DC_E_CPP_AAL2APBH_RANKBH_02 union all
  select * from dc.DC_E_CPP_AAL2APBH_RANKBH_03 union all
  select * from dc.DC_E_CPP_AAL2APBH_RANKBH_04 union all
  select * from dc.DC_E_CPP_AAL2APBH_RANKBH_05 union all
  select * from dc.DC_E_CPP_AAL2APBH_RANKBH_06;

create view dcpublic.DC_E_CPP_AAL2APBH_RANKBH
  as select OSS_ID,
    SN,
    RNC,
    RXI,
    RBS,
    ERBS,
    MOID,
    TransportNetwork,
    Aal2Sp,
    Aal2Ap,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG from
    dc.DC_E_CPP_AAL2APBH_RANKBH where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view
  dc.SELECT_E_CPP_AGGLEVEL( FIRSTDATE_ID,AGGLEVEL,DATE_ID,YEAR_ID,MONTH_ID,DAY_ID,WEEK_ID,WEEKDAY_ID,BUSINESSDAY,
  TIMELEVEL,TIMELEVELBH,DAYS) 
  as select FIRSTDATE_ID,AGGLEVEL,DATE_ID,YEAR_ID,MONTH_ID,DAY_ID,WEEK_ID,WEEKDAY_ID,BUSINESSDAY,
    TIMELEVEL,TIMELEVELBH,DAYS from dc.SELECT_AGGLEVEL_TABLE where
    TIMELEVELBH = 'DAYBH';

create view dcpublic.DIM_E_CN_VLR
  as select OSS_ID,
    NODE_FDN,
    SUBNETWORK,
    NE_ID,
    VLRADDR,
    NSUB,
    NSUBA,
    VENDOR,
    STATUS,
    CREATED,
    MODIFIED,
    MODIFIER from
    dc.DIM_E_CN_VLR;

create view dcpublic.DIM_E_CN_MGW_DEVICETYPE
  as select SERVICE_NAME,
    SERVICE_NUMBER from
    dc.DIM_E_CN_MGW_DEVICETYPE;

create view dcpublic.DIM_E_CN_SITEDST
  as select OSS_ID,
    SITE_FDN,
    USERLABEL,
    SITE_ID,
    DST,
    VENDOR,
    STATUS,
    CREATED,
    MODIFIED,
    MODIFIER from
    dc.DIM_E_CN_SITEDST;

create view dcpublic.DIM_E_CN_HLR
  as select OSS_ID,
    NODE_FDN,
    SUBNETWORK,
    NE_ID,
    HLRADDR,
    NSUB,
    OWN_INT,
    OWN_RINT,
    VENDOR,
    STATUS,
    CREATED,
    MODIFIED,
    MODIFIER from
    dc.DIM_E_CN_HLR;

create view dcpublic.DIM_E_CN_MGW
  as select OSS_ID,
    NE_FDN,
    SUBNETWORK,
    NE_ID,
    NE_NAME,
    NE_VERSION,
    NE_TYPE,
    SITE_FDN,
    SITE,
    VENDOR,
    STATUS,
    CREATED,
    MODIFIED,
    MODIFIER from
    dc.DIM_E_CN_MGW;

create view dcpublic.DIM_E_CN_AXE
  as select OSS_ID,
    NE_FDN,
    SUBNETWORK,
    NE_ID,
    NE_NAME,
    NE_VERSION,
    NE_TYPE,
    SITE_FDN,
    SITE,
    VENDOR,
    STATUS,
    CREATED,
    MODIFIED,
    MODIFIER from
    dc.DIM_E_CN_AXE;

create view dcpublic.DIM_E_CN_CGSN
  as select OSS_ID,
    NE_FDN,
    SUBNETWORK,
    NE_ID,
    NE_NAME,
    NE_VERSION,
    NE_TYPE,
    SITE_FDN,
    SITE,
    VENDOR,
    STATUS,
    CREATED,
    MODIFIED,
    MODIFIER from
    dc.DIM_E_CN_CGSN;

create view dcpublic.DIM_E_CN_IMS
  as select OSS_ID,
    NE_FDN,
    SUBNETWORK,
    NE_ID,
    NE_NAME,
    NE_VERSION,
    NE_TYPE,
    SITE_FDN,
    SITE,
    VENDOR,
    STATUS,
    CREATED,
    MODIFIED,
    MODIFIER from
    dc.DIM_E_CN_IMS;

create view dcpublic.DIM_E_CN_SASN
  as select OSS_ID,
    NE_FDN,
    SUBNETWORK,
    NE_ID,
    NE_NAME,
    NE_VERSION,
    NE_TYPE,
    SITE_FDN,
    SITE,
    VENDOR,
    STATUS,
    CREATED,
    MODIFIED,
    MODIFIER from
    dc.DIM_E_CN_SASN;

create view dcpublic.DIM_E_CN_GGSN
  as select OSS_ID,
    NE_FDN,
    SUBNETWORK,
    NE_ID,
    NE_NAME,
    NE_VERSION,
    NE_TYPE,
    SITE_FDN,
    SITE,
    VENDOR,
    STATUS,
    CREATED,
    MODIFIED,
    MODIFIER from
    dc.DIM_E_CN_GGSN;

create view dcpublic.DIM_E_CN_SGSN
  as select OSS_ID,
    NE_FDN,
    SUBNETWORK,
    NE_ID,
    NE_NAME,
    NE_VERSION,
    NE_TYPE,
    SITE_FDN,
    SITE,
    VENDOR,
    STATUS,
    CREATED,
    MODIFIED,
    MODIFIER from
    dc.DIM_E_CN_SGSN;

create view dcpublic.DIM_E_CN_CN
  as select OSS_ID,
    NE_FDN,
    SUBNETWORK,
    NE_ID,
    NE_NAME,
    NE_VERSION,
    NE_TYPE,
    SITE_FDN,
    SITE,
    MF_TYPE,
    MF_ID,
    SYSTEM_ID,
    VENDOR,
    STATUS,
    CREATED,
    MODIFIED,
    MODIFIER from
    dc.DIM_E_CN_CN;

create view dcpublic.DIM_E_CN_SITE
  as select OSS_ID,
    SITE_FDN,
    SUBNETWORK,
    SITE,
    SITE_ID,
    SITE_NAME,
    TIMEZONE,
    TIMEZONE_VALUE,
    DST,
    VENDOR,
    STATUS,
    CREATED,
    MODIFIED,
    MODIFIER from
    dc.DIM_E_CN_SITE;

create view dcpublic.DIM_E_CN_POOL
  as select OSS_ID,
    POOL_FDN,
    GROUP_NAME,
    GROUP_TYPE,
    USERLABEL,
    GROUP_ID,
    POOL_CREATE_TIME,
    POOL_DELETE_TIME,
    POOL_MEMBER_FDN,
    POOL_MEMBER,
    MEMBER_ADD_TIME,
    MEMBER_REMOVETIME,
    VENDOR,
    STATUS,
    CREATED,
    MODIFIED,
    MODIFIER from
    dc.DIM_E_CN_POOL;

create view dcpublic.DIM_E_CN_IMS_IPW
  as select OSS_ID,
    NE_FDN,
    SUBNETWORK,
    NE_ID,
    NE_NAME,
    NE_VERSION,
    NE_TYPE,
    SITE_FDN,
    SITE,
    VENDOR,
    STATUS,
    CREATED,
    MODIFIED,
    MODIFIER from
    dc.DIM_E_CN_IMS_IPW;

create view dcpublic.DIM_E_CN_MSCCL
  as select OSS_ID,
    NE_FDN,
    SUBNETWORK1,
    SUBNETWORK2,
    NE_ID,
    NE_NAME,
    NE_VERSION,
    NE_TYPE,
    SITE_FDN,
    SITE,
    VENDOR,
    STATUS,
    CREATED,
    MODIFIED,
    MODIFIER from
    dc.DIM_E_CN_MSCCL;

create view dcpublic.DIM_E_CN_MSCCLMF_AS
  as select OSS_ID,
    SOURCE_TYPE,
    SOURCE_FDN,
    SOURCE_SN1,
    SOURCE_SN2,
    SOURCE_NE,
    TARGET_TYPE,
    TARGET_FDN,
    TARGET_SN1,
    TARGET_SN2,
    TARGET_NE,
    VENDOR,
    STATUS,
    CREATED,
    MODIFIED,
    MODIFIER from
    dc.DIM_E_CN_MSCCLMF_AS;

create view dcpublic.DIM_E_CN_IMSGW
  as select OSS_ID,
    NE_FDN,
    SUBNETWORK,
    NE_ID,
    NE_NAME,
    NE_VERSION,
    NE_TYPE,
    SITE_FDN,
    SITE,
    VENDOR,
    STATUS,
    CREATED,
    MODIFIED,
    MODIFIER from
    dc.DIM_E_CN_IMSGW;

create view dcpublic.DIM_E_CN_IMSGWMF
  as select OSS_ID,
    NE_FDN,
    SUBNETWORK,
    NE_ID,
    MF_NAME,
    MF_VERSION,
    MF_TYPE,
    MF_ID,
    BLADESYSTEMID,
    SITE_FDN,
    SITE,
    VENDOR,
    STATUS,
    CREATED,
    MODIFIED,
    MODIFIER from
    dc.DIM_E_CN_IMSGWMF;

create view dcpublic.DIM_E_CN_IMSGWMF_AS
  as select OSS_ID,
    SOURCE_TYPE,
    SOURCE_FDN,
    SOURCE_SN1,
    SOURCE_SN2,
    SOURCE_NE,
    TARGET_TYPE,
    TARGET_FDN,
    TARGET_SN1,
    TARGET_SN2,
    TARGET_NE,
    VENDOR,
    STATUS,
    CREATED,
    MODIFIED,
    MODIFIER from
    dc.DIM_E_CN_IMSGWMF_AS;

create view dcpublic.DIM_E_CN_MTAS
  as select OSS_ID,
    NE_FDN,
    SUBNETWORK,
    NE_ID,
    NE_NAME,
    NE_VERSION,
    NE_TYPE,
    SITE_FDN,
    SITE,
    VENDOR,
    STATUS,
    CREATED,
    MODIFIED,
    MODIFIER from
    dc.DIM_E_CN_MTAS;

create view dc.DIM_E_CN_SITEDST_CURRENT as select * from dc.DIM_E_CN_SITEDST_CURRENT_DC;

create view dc.DIM_E_CN_VLR_CURRENT as select * from dc.DIM_E_CN_VLR_CURRENT_DC;

create view dc.DIM_E_CN_HLR_CURRENT as select * from dc.DIM_E_CN_HLR_CURRENT_DC;

create view dc.DIM_E_CN_MGW_CURRENT as select * from dc.DIM_E_CN_MGW_CURRENT_DC;

create view dc.DIM_E_CN_IMS_CURRENT( OSS_ID,NE_FDN,SUBNETWORK,NE_ID,NE_NAME,NE_VERSION,NE_TYPE,SITE_FDN,SITE,VENDOR,STATUS,CREATED,MODIFIED,MODIFIER) as select distinct OSS_ID,NE_FDN,SUBNETWORK,NE_ID,NE_NAME,NE_VERSION,NE_TYPE,SITE_FDN,SITE,VENDOR,STATUS,CREATED,MODIFIED,MODIFIER from dc.DIM_E_CN_CN where(NE_TYPE like 'CSCF%' or NE_TYPE like 'HSS%' or NE_TYPE like 'I-CSCF%' or NE_TYPE like 'IMS%' or NE_TYPE like 'MRF-PTT%' or NE_TYPE like 'P-CSCF%' or NE_TYPE like 'PGM%' or NE_TYPE like 'PTT-AS%' or NE_TYPE like 'S-CSCF%' or NE_TYPE like 'SLF%' or NE_TYPE like 'HSS%' or NE_TYPE like 'E-CSCF%' or NE_TYPE like 'MRFC%');

create view dc.DIM_E_CN_GGSN_CURRENT( OSS_ID,NE_FDN,SUBNETWORK,NE_ID,NE_NAME,NE_VERSION,NE_TYPE,SITE_FDN,SITE,VENDOR,STATUS,CREATED,MODIFIED,MODIFIER) as select distinct OSS_ID,NE_FDN,SUBNETWORK,NE_ID,NE_NAME,NE_VERSION,NE_TYPE,SITE_FDN,SITE,VENDOR,STATUS,CREATED,MODIFIED,MODIFIER from dc.DIM_E_CN_CN where NE_TYPE = 'GGSN';

create view dc.DIM_E_CN_SGSN_CURRENT( OSS_ID,NE_FDN,SUBNETWORK,NE_ID,NE_NAME,NE_VERSION,NE_TYPE,SITE_FDN,SITE,VENDOR,STATUS,CREATED,MODIFIED,MODIFIER) as select distinct OSS_ID,NE_FDN,SUBNETWORK,NE_ID,NE_NAME,NE_VERSION,NE_TYPE,SITE_FDN,SITE,VENDOR,STATUS,CREATED,MODIFIED,MODIFIER from dc.DIM_E_CN_CN where NE_TYPE = 'SGSN';

create view dc.DIM_E_CN_CN_CURRENT as select * from dc.DIM_E_CN_CN_CURRENT_DC;

create view dc.DIM_E_CN_SITE_CURRENT as select * from dc.DIM_E_CN_SITE_CURRENT_DC;

create view dc.DIM_E_CN_POOL_CURRENT as select * from dc.DIM_E_CN_POOL_CURRENT_DC;

create view dc.DIM_E_CN_IMSGW_CURRENT( OSS_ID,NE_FDN,SUBNETWORK,NE_ID,NE_NAME,NE_VERSION,NE_TYPE,SITE_FDN,SITE,VENDOR,STATUS,CREATED,MODIFIED,MODIFIER) as select distinct OSS_ID,NE_FDN,SUBNETWORK,NE_ID,NE_NAME,NE_VERSION,NE_TYPE,SITE_FDN,SITE,VENDOR,STATUS,CREATED,MODIFIED,MODIFIER from dc.DIM_E_CN_CN where NE_TYPE = 'Isite';

create view dc.DIM_E_CN_MSCCL_CURRENT as select * from dc.DIM_E_CN_MSCCL_CURRENT_DC;

create view dc.DIM_E_CN_MSCCLMF_AS_CURRENT as select * from dc.DIM_E_CN_MSCCLMF_AS_CURRENT_DC;

create view dc.DIM_E_CN_MTAS_CURRENT( OSS_ID,NE_FDN,SUBNETWORK,NE_ID,NE_NAME,NE_VERSION,NE_TYPE,SITE_FDN,SITE,VENDOR,STATUS,CREATED,MODIFIED,MODIFIER) as select distinct OSS_ID,NE_FDN,SUBNETWORK,NE_ID,NE_NAME,NE_VERSION,NE_TYPE,SITE_FDN,SITE,VENDOR,STATUS,CREATED,MODIFIED,MODIFIER from dc.DIM_E_CN_CN where NE_TYPE = 'MTAS';

create view dc.DIM_E_CN_IMSGWMF_AS_CURRENT as select * from dc.DIM_E_CN_IMSGWMF_AS_CURRENT_DC;

create view dc.DIM_E_CN_CGSN_CURRENT( OSS_ID,NE_FDN,SUBNETWORK,NE_ID,NE_NAME,NE_VERSION,NE_TYPE,SITE_FDN,SITE,VENDOR,STATUS,CREATED,MODIFIED,MODIFIER) as select distinct OSS_ID,NE_FDN,SUBNETWORK,NE_ID,NE_NAME,NE_VERSION,NE_TYPE,SITE_FDN,SITE,VENDOR,STATUS,CREATED,MODIFIED,MODIFIER from dc.DIM_E_CN_CN where NE_TYPE = 'CGSN';

create view dc.DIM_E_CN_SASN_CURRENT( OSS_ID,NE_FDN,SUBNETWORK,NE_ID,NE_NAME,NE_VERSION,NE_TYPE,SITE_FDN,SITE,VENDOR,STATUS,CREATED,MODIFIED,MODIFIER) as select distinct OSS_ID,NE_FDN,SUBNETWORK,NE_ID,NE_NAME,NE_VERSION,NE_TYPE,SITE_FDN,SITE,VENDOR,STATUS,CREATED,MODIFIED,MODIFIER from dc.DIM_E_CN_CN where NE_TYPE = 'SASN';

create view dc.DIM_E_CN_IMS_IPW_CURRENT( OSS_ID,NE_FDN,SUBNETWORK,NE_ID,NE_NAME,NE_VERSION,NE_TYPE,SITE_FDN,SITE,VENDOR,STATUS,CREATED,MODIFIED,MODIFIER) as select distinct OSS_ID,NE_FDN,SUBNETWORK,NE_ID,NE_NAME,NE_VERSION,NE_TYPE,SITE_FDN,SITE,VENDOR,STATUS,CREATED,MODIFIED,MODIFIER from dc.DIM_E_CN_CN where(NE_TYPE like 'IPWorks%' or NE_TYPE like 'DNSServer%');

create view dc.DIM_E_CN_AXE_CURRENT( OSS_ID,NE_FDN,SUBNETWORK,NE_ID,NE_NAME,NE_VERSION,NE_TYPE,SITE_FDN,SITE,VENDOR,STATUS,CREATED,MODIFIED,MODIFIER) as select OSS_ID,NE_FDN,SUBNETWORK,NE_ID,NE_NAME,NE_VERSION,NE_TYPE,SITE_FDN,SITE,VENDOR,STATUS,CREATED,MODIFIED,MODIFIER from dc.DIM_E_CN_AXE_CURRENT_DC union select a.OSS_ID,TARGET_FDN,TARGET_SN1,NE_ID || ',' || TARGET_NE,TARGET_NE,NE_VERSION,NE_TYPE,SITE_FDN,SITE,b.VENDOR,b.STATUS,b.CREATED,b.MODIFIED,b.MODIFIER from dc.DIM_E_CN_MSCCL as a,dc.DIM_E_CN_MSCCLMF_AS as b where a.OSS_ID = b.OSS_ID and a.NE_FDN = b.SOURCE_FDN;

create view dc.DIM_E_CN_IMSGWMF_CURRENT( OSS_ID,NE_FDN,SUBNETWORK,NE_ID,MF_NAME,MF_VERSION,MF_TYPE,MF_ID,BLADESYSTEMID,SITE_FDN,SITE,VENDOR,STATUS,CREATED,MODIFIED,MODIFIER) as select distinct OSS_ID,NE_FDN,SUBNETWORK,NE_ID,NE_NAME,NE_VERSION,MF_TYPE,MF_ID,SYSTEM_ID,SITE_FDN,SITE,VENDOR,STATUS,CREATED,MODIFIED,MODIFIER from dc.DIM_E_CN_CN where(MF_TYPE like 'MgwFunction%' or MF_TYPE like 'SgcFunction%' or MF_TYPE like 'MpFunction%');

create view
  dc.SELECT_DIM_E_CN_AGGLEVEL( FIRSTDATE_ID,AGGLEVEL,DATE_ID,YEAR_ID,MONTH_ID,DAY_ID,WEEK_ID,WEEKDAY_ID,BUSINESSDAY,
  TIMELEVEL,TIMELEVELBH,DAYS) 
  as select FIRSTDATE_ID,AGGLEVEL,DATE_ID,YEAR_ID,MONTH_ID,DAY_ID,WEEK_ID,WEEKDAY_ID,BUSINESSDAY,
    TIMELEVEL,TIMELEVELBH,DAYS from dc.SELECT_AGGLEVEL_TABLE where
    TIMELEVELBH = 'DAYBH';

create view dc.DC_E_SASN_SERVER_RAW as
  select * from dc.DC_E_SASN_SERVER_RAW_01 union all
  select * from dc.DC_E_SASN_SERVER_RAW_02 union all
  select * from dc.DC_E_SASN_SERVER_RAW_03;

create view dcpublic.DC_E_SASN_SERVER_RAW
  as select OSS_ID,
    SASN,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    RECOVERY,
    LA01,
    LA05,
    LA15,
    MEM,
    SWP from
    dc.DC_E_SASN_SERVER_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_SASN_SERVER_DAY as
  select * from dc.DC_E_SASN_SERVER_DAY_01 union all
  select * from dc.DC_E_SASN_SERVER_DAY_02 union all
  select * from dc.DC_E_SASN_SERVER_DAY_03;

create view dcpublic.DC_E_SASN_SERVER_DAY
  as select OSS_ID,
    SASN,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    RECOVERY,
    LA01,
    LA05,
    LA15,
    MEM,
    SWP from
    dc.DC_E_SASN_SERVER_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_SASN_PROCESS_RAW as
  select * from dc.DC_E_SASN_PROCESS_RAW_01 union all
  select * from dc.DC_E_SASN_PROCESS_RAW_02 union all
  select * from dc.DC_E_SASN_PROCESS_RAW_03;

create view dcpublic.DC_E_SASN_PROCESS_RAW
  as select OSS_ID,
    SASN,
    PROCESS_ID,
    PROCESS_NAME,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    CPU,
    MEM,
    CPU_TIME,
    VIRTUAL,
    RSS,
    PARTITION,
    PCU from
    dc.DC_E_SASN_PROCESS_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_SASN_PROCESS_DAY as
  select * from dc.DC_E_SASN_PROCESS_DAY_01 union all
  select * from dc.DC_E_SASN_PROCESS_DAY_02 union all
  select * from dc.DC_E_SASN_PROCESS_DAY_03;

create view dcpublic.DC_E_SASN_PROCESS_DAY
  as select OSS_ID,
    SASN,
    PROCESS_ID,
    PROCESS_NAME,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    CPU,
    MEM,
    CPU_TIME,
    VIRTUAL,
    RSS,
    PARTITION,
    PCU from
    dc.DC_E_SASN_PROCESS_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_SASN_PROCESS_COUNT as
  select * from dc.DC_E_SASN_PROCESS_COUNT_01 union all
  select * from dc.DC_E_SASN_PROCESS_COUNT_02 union all
  select * from dc.DC_E_SASN_PROCESS_COUNT_03;

create view dcpublic.DC_E_SASN_PROCESS_COUNT
  as select OSS_ID,
    SASN,
    PROCESS_ID,
    PROCESS_NAME,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    CPU,
    MEM,
    CPU_TIME,
    VIRTUAL,
    RSS,
    PARTITION,
    PCU from
    dc.DC_E_SASN_PROCESS_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_SASN_BEARER_RAW as
  select * from dc.DC_E_SASN_BEARER_RAW_01 union all
  select * from dc.DC_E_SASN_BEARER_RAW_02 union all
  select * from dc.DC_E_SASN_BEARER_RAW_03;

create view dcpublic.DC_E_SASN_BEARER_RAW
  as select OSS_ID,
    SASN,
    PROCESS_ID,
    BEARER,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    LOCAL_ACTIVATIONS,
    LOCAL_DEACTIVATIONS,
    LOCAL_CUR_SESSIONS,
    REMOTE_ACTIVATIONS,
    REMOTE_DEACTIVATIONS,
    REMOTE_CUR_SESSIONS,
    TOTAL_TIME,
    SIG_PKT,
    SIG_PKT_ERR,
    DATA_PKT_DOWN,
    DATA_BYTES_DOWN,
    DATA_PKT_UP,
    DATA_BYTES_UP,
    DATA_PKT_ERR,
    DATA_BYTES_ERR,
    DATA_PKT_DROP,
    DATA_BYTES_DROP from
    dc.DC_E_SASN_BEARER_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_SASN_BEARER_DAY as
  select * from dc.DC_E_SASN_BEARER_DAY_01 union all
  select * from dc.DC_E_SASN_BEARER_DAY_02 union all
  select * from dc.DC_E_SASN_BEARER_DAY_03;

create view dcpublic.DC_E_SASN_BEARER_DAY
  as select OSS_ID,
    SASN,
    PROCESS_ID,
    BEARER,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    LOCAL_ACTIVATIONS,
    LOCAL_DEACTIVATIONS,
    LOCAL_CUR_SESSIONS,
    REMOTE_ACTIVATIONS,
    REMOTE_DEACTIVATIONS,
    REMOTE_CUR_SESSIONS,
    TOTAL_TIME,
    SIG_PKT,
    SIG_PKT_ERR,
    DATA_PKT_DOWN,
    DATA_BYTES_DOWN,
    DATA_PKT_UP,
    DATA_BYTES_UP,
    DATA_PKT_ERR,
    DATA_BYTES_ERR,
    DATA_PKT_DROP,
    DATA_BYTES_DROP from
    dc.DC_E_SASN_BEARER_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dcpublic.DC_E_IMS_PTT_PTTCSS_RAW
  as select OSS_ID,
    SN,
    IMS_DC,
    g3SubNetwork,
    g3ManagedElement,
    MOID,
    PTT_CSS,
    Source,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pocusAutoAvailable,
    pocusManualAvailable,
    cssUsers,
    csServiceAdhoc,
    csServiceChatGroup,
    csServicePrearranged,
    ipaMessages,
    pocusPublish,
    csServiceSubscribeToParticipantInfo,
    csServiceUndetermined,
    pocusISBUnavailable,
    pttUsers,
    cssAddUserToSessionAttempts,
    cssAdhocAttempts,
    cssChatAttempts,
    cssRejoinAttempts,
    cssMMInviteAttempts,
    cssOneToOneAttempts,
    cssPrearrangedAttempts,
    pocusInitialPublishAttempts,
    gaMessageAttempts,
    ipaMessagesAttempts,
    pocusPublishRefreshAttempts,
    pocusPublishAttempts,
    cssSubscribeToParticipantInfoAttempts,
    cssUndeterminedAttempts from
    dc.DC_E_IMS_PTT_PTTCSS_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_SASN_BEARER_COUNT as
  select * from dc.DC_E_SASN_BEARER_COUNT_01 union all
  select * from dc.DC_E_SASN_BEARER_COUNT_02 union all
  select * from dc.DC_E_SASN_BEARER_COUNT_03;

create view dcpublic.DC_E_SASN_BEARER_COUNT
  as select OSS_ID,
    SASN,
    PROCESS_ID,
    BEARER,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    LOCAL_ACTIVATIONS,
    LOCAL_DEACTIVATIONS,
    LOCAL_CUR_SESSIONS,
    REMOTE_ACTIVATIONS,
    REMOTE_DEACTIVATIONS,
    REMOTE_CUR_SESSIONS,
    TOTAL_TIME,
    SIG_PKT,
    SIG_PKT_ERR,
    DATA_PKT_DOWN,
    DATA_BYTES_DOWN,
    DATA_PKT_UP,
    DATA_BYTES_UP,
    DATA_PKT_ERR,
    DATA_BYTES_ERR,
    DATA_PKT_DROP,
    DATA_BYTES_DROP from
    dc.DC_E_SASN_BEARER_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_SASN_SERVICE_RAW as
  select * from dc.DC_E_SASN_SERVICE_RAW_01 union all
  select * from dc.DC_E_SASN_SERVICE_RAW_02 union all
  select * from dc.DC_E_SASN_SERVICE_RAW_03;

create view dcpublic.DC_E_SASN_SERVICE_RAW
  as select OSS_ID,
    SASN,
    PROCESS_ID,
    SERVICE_ID,
    SERVICE_NAME,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    LOCAL_ACTIVATIONS,
    LOCAL_DEACTIVATIONS,
    LOCAL_CUR_SESSIONS,
    TOTAL_TIME,
    DATA_PKT_DOWN,
    DATA_BYTES_DOWN,
    DATA_PKT_UP,
    DATA_BYTES_UP,
    DATA_PKT_ERR,
    DATA_PKT_QUEUED,
    DATA_PKT_DROPPED,
    DATA_PKT_INJECTED,
    DATA_PKT_MODIFIED,
    EVENTS_GENERATED,
    EDR_GENERATED,
    BLOCKED_SESSIONS,
    UNUSED,
    SESSIONS_WITH_UDR from
    dc.DC_E_SASN_SERVICE_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_SASN_SERVICE_DAY as
  select * from dc.DC_E_SASN_SERVICE_DAY_01 union all
  select * from dc.DC_E_SASN_SERVICE_DAY_02 union all
  select * from dc.DC_E_SASN_SERVICE_DAY_03;

create view dcpublic.DC_E_SASN_SERVICE_DAY
  as select OSS_ID,
    SASN,
    PROCESS_ID,
    SERVICE_ID,
    SERVICE_NAME,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    LOCAL_ACTIVATIONS,
    LOCAL_DEACTIVATIONS,
    LOCAL_CUR_SESSIONS,
    TOTAL_TIME,
    DATA_PKT_DOWN,
    DATA_BYTES_DOWN,
    DATA_PKT_UP,
    DATA_BYTES_UP,
    DATA_PKT_ERR,
    DATA_PKT_QUEUED,
    DATA_PKT_DROPPED,
    DATA_PKT_INJECTED,
    DATA_PKT_MODIFIED,
    EVENTS_GENERATED,
    EDR_GENERATED,
    BLOCKED_SESSIONS,
    UNUSED,
    SESSIONS_WITH_UDR from
    dc.DC_E_SASN_SERVICE_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_SASN_SERVICE_COUNT as
  select * from dc.DC_E_SASN_SERVICE_COUNT_01 union all
  select * from dc.DC_E_SASN_SERVICE_COUNT_02 union all
  select * from dc.DC_E_SASN_SERVICE_COUNT_03;

create view dcpublic.DC_E_SASN_SERVICE_COUNT
  as select OSS_ID,
    SASN,
    PROCESS_ID,
    SERVICE_ID,
    SERVICE_NAME,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    LOCAL_ACTIVATIONS,
    LOCAL_DEACTIVATIONS,
    LOCAL_CUR_SESSIONS,
    TOTAL_TIME,
    DATA_PKT_DOWN,
    DATA_BYTES_DOWN,
    DATA_PKT_UP,
    DATA_BYTES_UP,
    DATA_PKT_ERR,
    DATA_PKT_QUEUED,
    DATA_PKT_DROPPED,
    DATA_PKT_INJECTED,
    DATA_PKT_MODIFIED,
    EVENTS_GENERATED,
    EDR_GENERATED,
    BLOCKED_SESSIONS,
    UNUSED,
    SESSIONS_WITH_UDR from
    dc.DC_E_SASN_SERVICE_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_SASN_CONTROL_RAW as
  select * from dc.DC_E_SASN_CONTROL_RAW_01 union all
  select * from dc.DC_E_SASN_CONTROL_RAW_02 union all
  select * from dc.DC_E_SASN_CONTROL_RAW_03;

create view dcpublic.DC_E_SASN_CONTROL_RAW
  as select OSS_ID,
    SASN,
    CONTROLTYPE,
    PROCESS_ID,
    CONTROL_NAME,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    ACTIV_CTRL_SES,
    DEACT_CTRL_SES,
    BLOCKED_SES,
    ERROR_TERM_SES,
    CTRL_BYTES_SENT,
    CTRL_BYTES_RCVD,
    CTRL_MESG_SENT,
    CTRL_MESG_RCVD,
    CTRL_MESG_ERROR,
    ZERO_QUOTA,
    VOL_GRANTED,
    VOL_USED,
    TIME_GRANTED,
    TIME_USED,
    EVENTS_GRANTED,
    EVENTS_USED,
    TIMEOUT_SES,
    CONNECT_ERRORS,
    RESP_TIME_MAX,
    RESP_TIME_MIN,
    RESP_TIME_AVG from
    dc.DC_E_SASN_CONTROL_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_SASN_CONTROL_DAY as
  select * from dc.DC_E_SASN_CONTROL_DAY_01 union all
  select * from dc.DC_E_SASN_CONTROL_DAY_02 union all
  select * from dc.DC_E_SASN_CONTROL_DAY_03;

create view dcpublic.DC_E_SASN_CONTROL_DAY
  as select OSS_ID,
    SASN,
    CONTROLTYPE,
    PROCESS_ID,
    CONTROL_NAME,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    ACTIV_CTRL_SES,
    DEACT_CTRL_SES,
    BLOCKED_SES,
    ERROR_TERM_SES,
    CTRL_BYTES_SENT,
    CTRL_BYTES_RCVD,
    CTRL_MESG_SENT,
    CTRL_MESG_RCVD,
    CTRL_MESG_ERROR,
    ZERO_QUOTA,
    VOL_GRANTED,
    VOL_USED,
    TIME_GRANTED,
    TIME_USED,
    EVENTS_GRANTED,
    EVENTS_USED,
    TIMEOUT_SES,
    CONNECT_ERRORS,
    RESP_TIME_MAX,
    RESP_TIME_MIN,
    RESP_TIME_AVG from
    dc.DC_E_SASN_CONTROL_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dcpublic.DIM_E_TDRBS_ELEMBH_BHTYPE
  as select BHTYPE,
    DESCRIPTION from
    dc.DIM_E_TDRBS_ELEMBH_BHTYPE;

create view dc.DC_E_SASN_CONTROL_COUNT as
  select * from dc.DC_E_SASN_CONTROL_COUNT_01 union all
  select * from dc.DC_E_SASN_CONTROL_COUNT_02 union all
  select * from dc.DC_E_SASN_CONTROL_COUNT_03;

create view dcpublic.DC_E_SASN_CONTROL_COUNT
  as select OSS_ID,
    SASN,
    CONTROLTYPE,
    PROCESS_ID,
    CONTROL_NAME,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    ACTIV_CTRL_SES,
    DEACT_CTRL_SES,
    BLOCKED_SES,
    ERROR_TERM_SES,
    CTRL_BYTES_SENT,
    CTRL_BYTES_RCVD,
    CTRL_MESG_SENT,
    CTRL_MESG_RCVD,
    CTRL_MESG_ERROR,
    ZERO_QUOTA,
    VOL_GRANTED,
    VOL_USED,
    TIME_GRANTED,
    TIME_USED,
    EVENTS_GRANTED,
    EVENTS_USED,
    TIMEOUT_SES,
    CONNECT_ERRORS,
    RESP_TIME_MAX,
    RESP_TIME_MIN,
    RESP_TIME_AVG from
    dc.DC_E_SASN_CONTROL_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_SASN_CONTENTFILTER_RAW as
  select * from dc.DC_E_SASN_CONTENTFILTER_RAW_01 union all
  select * from dc.DC_E_SASN_CONTENTFILTER_RAW_02 union all
  select * from dc.DC_E_SASN_CONTENTFILTER_RAW_03;

create view dcpublic.DC_E_SASN_CONTENTFILTER_RAW
  as select OSS_ID,
    SASN,
    PROCESS_ID,
    FUNCTION_NAME,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    BEARER_SES,
    MAX_BEARER_SES,
    ACTIVE_SES,
    MAX_CFE_SES,
    ACTIVE_CFE_FLOW,
    MAX_CFE_FLOW,
    CAT_REQ,
    CAT_OK_RSP,
    CAT_UNKNWN_RSP,
    CAT_ERR_RSP,
    CACHE_HIT,
    CACHE_SIZE from
    dc.DC_E_SASN_CONTENTFILTER_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_SASN_CONTENTFILTER_DAY as
  select * from dc.DC_E_SASN_CONTENTFILTER_DAY_01 union all
  select * from dc.DC_E_SASN_CONTENTFILTER_DAY_02 union all
  select * from dc.DC_E_SASN_CONTENTFILTER_DAY_03;

create view dcpublic.DC_E_SASN_CONTENTFILTER_DAY
  as select OSS_ID,
    SASN,
    PROCESS_ID,
    FUNCTION_NAME,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    BEARER_SES,
    MAX_BEARER_SES,
    ACTIVE_SES,
    MAX_CFE_SES,
    ACTIVE_CFE_FLOW,
    MAX_CFE_FLOW,
    CAT_REQ,
    CAT_OK_RSP,
    CAT_UNKNWN_RSP,
    CAT_ERR_RSP,
    CACHE_HIT,
    CACHE_SIZE from
    dc.DC_E_SASN_CONTENTFILTER_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_SASN_CONTENTFILTER_COUNT as
  select * from dc.DC_E_SASN_CONTENTFILTER_COUNT_01 union all
  select * from dc.DC_E_SASN_CONTENTFILTER_COUNT_02 union all
  select * from dc.DC_E_SASN_CONTENTFILTER_COUNT_03;

create view dcpublic.DC_E_SASN_CONTENTFILTER_COUNT
  as select OSS_ID,
    SASN,
    PROCESS_ID,
    FUNCTION_NAME,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    BEARER_SES,
    MAX_BEARER_SES,
    ACTIVE_SES,
    MAX_CFE_SES,
    ACTIVE_CFE_FLOW,
    MAX_CFE_FLOW,
    CAT_REQ,
    CAT_OK_RSP,
    CAT_UNKNWN_RSP,
    CAT_ERR_RSP,
    CACHE_HIT,
    CACHE_SIZE from
    dc.DC_E_SASN_CONTENTFILTER_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_SASN_CONTENTEDITOR_RAW as
  select * from dc.DC_E_SASN_CONTENTEDITOR_RAW_01 union all
  select * from dc.DC_E_SASN_CONTENTEDITOR_RAW_02 union all
  select * from dc.DC_E_SASN_CONTENTEDITOR_RAW_03;

create view dcpublic.DC_E_SASN_CONTENTEDITOR_RAW
  as select OSS_ID,
    SASN,
    PROCESS_ID,
    FUNCTION_NAME,
    EDITOR_LABEL,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    EDITOR_INVOCATIONS from
    dc.DC_E_SASN_CONTENTEDITOR_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_SASN_CONTENTEDITOR_DAY as
  select * from dc.DC_E_SASN_CONTENTEDITOR_DAY_01 union all
  select * from dc.DC_E_SASN_CONTENTEDITOR_DAY_02 union all
  select * from dc.DC_E_SASN_CONTENTEDITOR_DAY_03;

create view dcpublic.DC_E_SASN_CONTENTEDITOR_DAY
  as select OSS_ID,
    SASN,
    PROCESS_ID,
    FUNCTION_NAME,
    EDITOR_LABEL,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    EDITOR_INVOCATIONS from
    dc.DC_E_SASN_CONTENTEDITOR_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_SASN_CONTENTEDITOR_COUNT as
  select * from dc.DC_E_SASN_CONTENTEDITOR_COUNT_01 union all
  select * from dc.DC_E_SASN_CONTENTEDITOR_COUNT_02 union all
  select * from dc.DC_E_SASN_CONTENTEDITOR_COUNT_03;

create view dcpublic.DC_E_SASN_CONTENTEDITOR_COUNT
  as select OSS_ID,
    SASN,
    PROCESS_ID,
    FUNCTION_NAME,
    EDITOR_LABEL,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    EDITOR_INVOCATIONS from
    dc.DC_E_SASN_CONTENTEDITOR_COUNT where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view
  dc.SELECT_E_SASN_AGGLEVEL( FIRSTDATE_ID,AGGLEVEL,DATE_ID,YEAR_ID,MONTH_ID,DAY_ID,WEEK_ID,WEEKDAY_ID,BUSINESSDAY,
  TIMELEVEL,TIMELEVELBH,DAYS) 
  as select FIRSTDATE_ID,AGGLEVEL,DATE_ID,YEAR_ID,MONTH_ID,DAY_ID,WEEK_ID,WEEKDAY_ID,BUSINESSDAY,
    TIMELEVEL,TIMELEVELBH,DAYS from dc.SELECT_AGGLEVEL_TABLE where
    TIMELEVELBH = 'DAYBH';

create view dc.DC_E_SASN_SARA_SS_RAW as
  select * from dc.DC_E_SASN_SARA_SS_RAW_01 union all
  select * from dc.DC_E_SASN_SARA_SS_RAW_02 union all
  select * from dc.DC_E_SASN_SARA_SS_RAW_03 union all
  select * from dc.DC_E_SASN_SARA_SS_RAW_04 union all
  select * from dc.DC_E_SASN_SARA_SS_RAW_05 union all
  select * from dc.DC_E_SASN_SARA_SS_RAW_06;

create view dcpublic.DC_E_SASN_SARA_SS_RAW
  as select OSS_ID,
    NE_ID,
    MOID,
    ReportSelection,
    ContentType,
    reportSelectionLabel,
    contentTypeLabel,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    dataPktDown,
    dataBytesDown,
    dataPktUp,
    dataBytesUp,
    localActivations,
    localDeactivations from
    dc.DC_E_SASN_SARA_SS_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_SASN_SARA_SS_DAY as
  select * from dc.DC_E_SASN_SARA_SS_DAY_01 union all
  select * from dc.DC_E_SASN_SARA_SS_DAY_02 union all
  select * from dc.DC_E_SASN_SARA_SS_DAY_03 union all
  select * from dc.DC_E_SASN_SARA_SS_DAY_04 union all
  select * from dc.DC_E_SASN_SARA_SS_DAY_05 union all
  select * from dc.DC_E_SASN_SARA_SS_DAY_06;

create view dcpublic.DC_E_SASN_SARA_SS_DAY
  as select OSS_ID,
    NE_ID,
    MOID,
    ReportSelection,
    ContentType,
    reportSelectionLabel,
    contentTypeLabel,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    dataPktDown,
    dataBytesDown,
    dataPktUp,
    dataBytesUp,
    localActivations,
    localDeactivations from
    dc.DC_E_SASN_SARA_SS_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_SASN_SARA_PC_RAW as
  select * from dc.DC_E_SASN_SARA_PC_RAW_01 union all
  select * from dc.DC_E_SASN_SARA_PC_RAW_02 union all
  select * from dc.DC_E_SASN_SARA_PC_RAW_03 union all
  select * from dc.DC_E_SASN_SARA_PC_RAW_04 union all
  select * from dc.DC_E_SASN_SARA_PC_RAW_05 union all
  select * from dc.DC_E_SASN_SARA_PC_RAW_06;

create view dcpublic.DC_E_SASN_SARA_PC_RAW
  as select OSS_ID,
    NE_ID,
    MOID,
    ReportSelection,
    Prefix,
    Container1,
    Container2,
    Suffix,
    reportSelectionLabel,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    numberOfAccess from
    dc.DC_E_SASN_SARA_PC_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_SASN_SARA_PC_DAY as
  select * from dc.DC_E_SASN_SARA_PC_DAY_01 union all
  select * from dc.DC_E_SASN_SARA_PC_DAY_02 union all
  select * from dc.DC_E_SASN_SARA_PC_DAY_03 union all
  select * from dc.DC_E_SASN_SARA_PC_DAY_04 union all
  select * from dc.DC_E_SASN_SARA_PC_DAY_05 union all
  select * from dc.DC_E_SASN_SARA_PC_DAY_06;

create view dcpublic.DC_E_SASN_SARA_PC_DAY
  as select OSS_ID,
    NE_ID,
    MOID,
    ReportSelection,
    Prefix,
    Container1,
    Container2,
    Suffix,
    reportSelectionLabel,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    numberOfAccess from
    dc.DC_E_SASN_SARA_PC_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view
  dc.SELECT_E_SASN_SARA_AGGLEVEL( FIRSTDATE_ID,AGGLEVEL,DATE_ID,YEAR_ID,MONTH_ID,DAY_ID,WEEK_ID,WEEKDAY_ID,BUSINESSDAY,
  TIMELEVEL,TIMELEVELBH,DAYS) 
  as select FIRSTDATE_ID,AGGLEVEL,DATE_ID,YEAR_ID,MONTH_ID,DAY_ID,WEEK_ID,WEEKDAY_ID,BUSINESSDAY,
    TIMELEVEL,TIMELEVELBH,DAYS from dc.SELECT_AGGLEVEL_TABLE where
    TIMELEVELBH = 'DAYBH';

create view dc.DC_E_IMS_PTT_PTTPOCXDMS_RAW as
  select * from dc.DC_E_IMS_PTT_PTTPOCXDMS_RAW_01 union all
  select * from dc.DC_E_IMS_PTT_PTTPOCXDMS_RAW_03 union all
  select * from dc.DC_E_IMS_PTT_PTTPOCXDMS_RAW_05 union all
  select * from dc.DC_E_IMS_PTT_PTTPOCXDMS_RAW_02 union all
  select * from dc.DC_E_IMS_PTT_PTTPOCXDMS_RAW_04 union all
  select * from dc.DC_E_IMS_PTT_PTTPOCXDMS_RAW_06;

create view dc.DC_E_IMS_PTT_PTTCSS_RAW as
  select * from dc.DC_E_IMS_PTT_PTTCSS_RAW_01 union all
  select * from dc.DC_E_IMS_PTT_PTTCSS_RAW_03 union all
  select * from dc.DC_E_IMS_PTT_PTTCSS_RAW_05 union all
  select * from dc.DC_E_IMS_PTT_PTTCSS_RAW_02 union all
  select * from dc.DC_E_IMS_PTT_PTTCSS_RAW_04 union all
  select * from dc.DC_E_IMS_PTT_PTTCSS_RAW_06;

create view dc.DC_E_TDRBS_NBAPCOMMON_RAW as
  select * from dc.DC_E_TDRBS_NBAPCOMMON_RAW_01 union all
  select * from dc.DC_E_TDRBS_NBAPCOMMON_RAW_02 union all
  select * from dc.DC_E_TDRBS_NBAPCOMMON_RAW_03;

create view dcpublic.DC_E_TDRBS_NBAPCOMMON_RAW
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    NodeBFunction,
    Iub,
    NbapCommon,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfDiscardedMsg from
    dc.DC_E_TDRBS_NBAPCOMMON_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_NBAPCOMMON_DAY as
  select * from dc.DC_E_TDRBS_NBAPCOMMON_DAY_01 union all
  select * from dc.DC_E_TDRBS_NBAPCOMMON_DAY_02 union all
  select * from dc.DC_E_TDRBS_NBAPCOMMON_DAY_03;

create view dcpublic.DC_E_TDRBS_NBAPCOMMON_DAY
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    NodeBFunction,
    Iub,
    NbapCommon,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmNoOfDiscardedMsg from
    dc.DC_E_TDRBS_NBAPCOMMON_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_ANTENNABRANCH_RAW as
  select * from dc.DC_E_TDRBS_ANTENNABRANCH_RAW_01 union all
  select * from dc.DC_E_TDRBS_ANTENNABRANCH_RAW_02 union all
  select * from dc.DC_E_TDRBS_ANTENNABRANCH_RAW_03 union all
  select * from dc.DC_E_TDRBS_ANTENNABRANCH_RAW_04 union all
  select * from dc.DC_E_TDRBS_ANTENNABRANCH_RAW_05 union all
  select * from dc.DC_E_TDRBS_ANTENNABRANCH_RAW_06;

create view dcpublic.DC_E_TDRBS_ANTENNABRANCH_RAW
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    Equipment,
    SectorAntenna,
    AntennaBranch,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfPowLimSlots from
    dc.DC_E_TDRBS_ANTENNABRANCH_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_ANTENNABRANCH_DAY as
  select * from dc.DC_E_TDRBS_ANTENNABRANCH_DAY_01 union all
  select * from dc.DC_E_TDRBS_ANTENNABRANCH_DAY_02 union all
  select * from dc.DC_E_TDRBS_ANTENNABRANCH_DAY_03 union all
  select * from dc.DC_E_TDRBS_ANTENNABRANCH_DAY_04 union all
  select * from dc.DC_E_TDRBS_ANTENNABRANCH_DAY_05 union all
  select * from dc.DC_E_TDRBS_ANTENNABRANCH_DAY_06;

create view dcpublic.DC_E_TDRBS_ANTENNABRANCH_DAY
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    Equipment,
    SectorAntenna,
    AntennaBranch,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmNoOfPowLimSlots from
    dc.DC_E_TDRBS_ANTENNABRANCH_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_EDCHRESOURCES_RAW as
  select * from dc.DC_E_TDRBS_EDCHRESOURCES_RAW_01 union all
  select * from dc.DC_E_TDRBS_EDCHRESOURCES_RAW_02 union all
  select * from dc.DC_E_TDRBS_EDCHRESOURCES_RAW_03 union all
  select * from dc.DC_E_TDRBS_EDCHRESOURCES_RAW_04 union all
  select * from dc.DC_E_TDRBS_EDCHRESOURCES_RAW_05 union all
  select * from dc.DC_E_TDRBS_EDCHRESOURCES_RAW_06;

create view dcpublic.DC_E_TDRBS_EDCHRESOURCES_RAW
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    EDchResources,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmSumNackedBitsCellEul,
    pmSumAckedBitsCellEul,
    pmNoActive10msFramesEul,
    pmNoUlUuLoadLimitEul,
    pmNoAllowedEul from
    dc.DC_E_TDRBS_EDCHRESOURCES_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_EDCHRESOURCES_DAYBH as
  select * from dc.DC_E_TDRBS_EDCHRESOURCES_DAYBH_01 union all
  select * from dc.DC_E_TDRBS_EDCHRESOURCES_DAYBH_02 union all
  select * from dc.DC_E_TDRBS_EDCHRESOURCES_DAYBH_03 union all
  select * from dc.DC_E_TDRBS_EDCHRESOURCES_DAYBH_04 union all
  select * from dc.DC_E_TDRBS_EDCHRESOURCES_DAYBH_05 union all
  select * from dc.DC_E_TDRBS_EDCHRESOURCES_DAYBH_06;

create view dcpublic.DC_E_TDRBS_EDCHRESOURCES_DAYBH
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    EDchResources,
    DATE_ID,
    MIN_ID,
    BHTYPE,
    BUSYHOUR,
    BHCLASS,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmSumNackedBitsCellEul,
    pmSumAckedBitsCellEul,
    pmNoActive10msFramesEul,
    pmNoUlUuLoadLimitEul,
    pmNoAllowedEul from
    dc.DC_E_TDRBS_EDCHRESOURCES_DAYBH where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_EDCHRESOURCES_DAY as
  select * from dc.DC_E_TDRBS_EDCHRESOURCES_DAY_01 union all
  select * from dc.DC_E_TDRBS_EDCHRESOURCES_DAY_02 union all
  select * from dc.DC_E_TDRBS_EDCHRESOURCES_DAY_03 union all
  select * from dc.DC_E_TDRBS_EDCHRESOURCES_DAY_04 union all
  select * from dc.DC_E_TDRBS_EDCHRESOURCES_DAY_05 union all
  select * from dc.DC_E_TDRBS_EDCHRESOURCES_DAY_06;

create view dcpublic.DC_E_TDRBS_EDCHRESOURCES_DAY
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    EDchResources,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmSumNackedBitsCellEul,
    pmSumAckedBitsCellEul,
    pmNoActive10msFramesEul,
    pmNoUlUuLoadLimitEul,
    pmNoAllowedEul from
    dc.DC_E_TDRBS_EDCHRESOURCES_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_IUBDATASTREAMS_RAW as
  select * from dc.DC_E_TDRBS_IUBDATASTREAMS_RAW_01 union all
  select * from dc.DC_E_TDRBS_IUBDATASTREAMS_RAW_02 union all
  select * from dc.DC_E_TDRBS_IUBDATASTREAMS_RAW_03;

create view dcpublic.DC_E_TDRBS_IUBDATASTREAMS_RAW
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    NodeBFunction,
    Iub,
    IubDataStreams,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmCapAllocIubHsLimitingRatio,
    pmHsDataFramesLost,
    pmHsDataFramesReceived,
    pmRbsHsPdschCodePrio,
    pmDchFramesCrcMismatch,
    pmDchFramesLate,
    pmDchFramesTooLate,
    pmDchFramesReceived,
    pmNoUlIubLimitEul,
    pmEdchIubLimitingRatio from
    dc.DC_E_TDRBS_IUBDATASTREAMS_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_IUBDATASTREAMS_DAY as
  select * from dc.DC_E_TDRBS_IUBDATASTREAMS_DAY_01 union all
  select * from dc.DC_E_TDRBS_IUBDATASTREAMS_DAY_02 union all
  select * from dc.DC_E_TDRBS_IUBDATASTREAMS_DAY_03;

create view dcpublic.DC_E_TDRBS_IUBDATASTREAMS_DAY
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    NodeBFunction,
    Iub,
    IubDataStreams,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmCapAllocIubHsLimitingRatio,
    pmHsDataFramesLost,
    pmHsDataFramesReceived,
    pmRbsHsPdschCodePrio,
    pmDchFramesCrcMismatch,
    pmDchFramesLate,
    pmDchFramesTooLate,
    pmDchFramesReceived,
    pmNoUlIubLimitEul,
    pmEdchIubLimitingRatio from
    dc.DC_E_TDRBS_IUBDATASTREAMS_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_PRACH_RAW as
  select * from dc.DC_E_TDRBS_PRACH_RAW_01 union all
  select * from dc.DC_E_TDRBS_PRACH_RAW_02 union all
  select * from dc.DC_E_TDRBS_PRACH_RAW_03 union all
  select * from dc.DC_E_TDRBS_PRACH_RAW_04 union all
  select * from dc.DC_E_TDRBS_PRACH_RAW_05 union all
  select * from dc.DC_E_TDRBS_PRACH_RAW_06;

create view dcpublic.DC_E_TDRBS_PRACH_RAW
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    Prach,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoPreambleFalseDetection,
    pmSuccReceivedBlocks,
    pmUnsuccReceivedBlocks from
    dc.DC_E_TDRBS_PRACH_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_PRACH_DAYBH as
  select * from dc.DC_E_TDRBS_PRACH_DAYBH_01 union all
  select * from dc.DC_E_TDRBS_PRACH_DAYBH_02 union all
  select * from dc.DC_E_TDRBS_PRACH_DAYBH_03 union all
  select * from dc.DC_E_TDRBS_PRACH_DAYBH_04 union all
  select * from dc.DC_E_TDRBS_PRACH_DAYBH_05 union all
  select * from dc.DC_E_TDRBS_PRACH_DAYBH_06;

create view dcpublic.DC_E_TDRBS_PRACH_DAYBH
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    Prach,
    DATE_ID,
    MIN_ID,
    BHTYPE,
    BUSYHOUR,
    BHCLASS,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmNoPreambleFalseDetection,
    pmSuccReceivedBlocks,
    pmUnsuccReceivedBlocks from
    dc.DC_E_TDRBS_PRACH_DAYBH where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_PRACH_DAY as
  select * from dc.DC_E_TDRBS_PRACH_DAY_01 union all
  select * from dc.DC_E_TDRBS_PRACH_DAY_02 union all
  select * from dc.DC_E_TDRBS_PRACH_DAY_03 union all
  select * from dc.DC_E_TDRBS_PRACH_DAY_04 union all
  select * from dc.DC_E_TDRBS_PRACH_DAY_05 union all
  select * from dc.DC_E_TDRBS_PRACH_DAY_06;

create view dcpublic.DC_E_TDRBS_PRACH_DAY
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    Prach,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmNoPreambleFalseDetection,
    pmSuccReceivedBlocks,
    pmUnsuccReceivedBlocks from
    dc.DC_E_TDRBS_PRACH_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_ULBASEBANDPOOL_DAY as
  select * from dc.DC_E_TDRBS_ULBASEBANDPOOL_DAY_01 union all
  select * from dc.DC_E_TDRBS_ULBASEBANDPOOL_DAY_02 union all
  select * from dc.DC_E_TDRBS_ULBASEBANDPOOL_DAY_03;

create view dcpublic.DC_E_TDRBS_ULBASEBANDPOOL_DAY
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    Equipment,
    Subrack,
    UplinkBaseBandPool,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmApomcOfRakeRecUsed,
    pmApomcOfUlLinkCap,
    pmApomcOfUlRachCap,
    pmNoOfIbho,
    pmNoUlHwLimitEul,
    pmSetupAttemptsSf128,
    pmSetupAttemptsSf16,
    pmSetupAttemptsSf256,
    pmSetupAttemptsSf32,
    pmSetupAttemptsSf4,
    pmSetupAttemptsSf64,
    pmSetupAttemptsSf8,
    pmSetupFailuresSf128,
    pmSetupFailuresSf16,
    pmSetupFailuresSf256,
    pmSetupFailuresSf32,
    pmSetupFailuresSf4,
    pmSetupFailuresSf64,
    pmSetupFailuresSf8 from
    dc.DC_E_TDRBS_ULBASEBANDPOOL_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_ULBASEBANDPOOL_RAW as
  select * from dc.DC_E_TDRBS_ULBASEBANDPOOL_RAW_01 union all
  select * from dc.DC_E_TDRBS_ULBASEBANDPOOL_RAW_02 union all
  select * from dc.DC_E_TDRBS_ULBASEBANDPOOL_RAW_03;

create view dcpublic.DC_E_TDRBS_ULBASEBANDPOOL_RAW
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    Equipment,
    Subrack,
    UplinkBaseBandPool,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmApomcOfRakeRecUsed,
    pmApomcOfUlLinkCap,
    pmApomcOfUlRachCap,
    pmNoOfIbho,
    pmNoUlHwLimitEul,
    pmSetupAttemptsSf128,
    pmSetupAttemptsSf16,
    pmSetupAttemptsSf256,
    pmSetupAttemptsSf32,
    pmSetupAttemptsSf4,
    pmSetupAttemptsSf64,
    pmSetupAttemptsSf8,
    pmSetupFailuresSf128,
    pmSetupFailuresSf16,
    pmSetupFailuresSf256,
    pmSetupFailuresSf32,
    pmSetupFailuresSf4,
    pmSetupFailuresSf64,
    pmSetupFailuresSf8 from
    dc.DC_E_TDRBS_ULBASEBANDPOOL_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_AICH_DAY as
  select * from dc.DC_E_TDRBS_AICH_DAY_01 union all
  select * from dc.DC_E_TDRBS_AICH_DAY_02 union all
  select * from dc.DC_E_TDRBS_AICH_DAY_03 union all
  select * from dc.DC_E_TDRBS_AICH_DAY_04 union all
  select * from dc.DC_E_TDRBS_AICH_DAY_05 union all
  select * from dc.DC_E_TDRBS_AICH_DAY_06;

create view dcpublic.DC_E_TDRBS_AICH_DAY
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    Aich,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmPositiveMessages,
    pmNegativeMessages from
    dc.DC_E_TDRBS_AICH_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_AICH_RAW as
  select * from dc.DC_E_TDRBS_AICH_RAW_01 union all
  select * from dc.DC_E_TDRBS_AICH_RAW_02 union all
  select * from dc.DC_E_TDRBS_AICH_RAW_03 union all
  select * from dc.DC_E_TDRBS_AICH_RAW_04 union all
  select * from dc.DC_E_TDRBS_AICH_RAW_05 union all
  select * from dc.DC_E_TDRBS_AICH_RAW_06;

create view dcpublic.DC_E_TDRBS_AICH_RAW
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    Aich,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmPositiveMessages,
    pmNegativeMessages from
    dc.DC_E_TDRBS_AICH_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_AICH_DAYBH as
  select * from dc.DC_E_TDRBS_AICH_DAYBH_01 union all
  select * from dc.DC_E_TDRBS_AICH_DAYBH_02 union all
  select * from dc.DC_E_TDRBS_AICH_DAYBH_03 union all
  select * from dc.DC_E_TDRBS_AICH_DAYBH_04 union all
  select * from dc.DC_E_TDRBS_AICH_DAYBH_05 union all
  select * from dc.DC_E_TDRBS_AICH_DAYBH_06;

create view dcpublic.DC_E_TDRBS_AICH_DAYBH
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    Aich,
    DATE_ID,
    MIN_ID,
    BHTYPE,
    BUSYHOUR,
    BHCLASS,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmPositiveMessages,
    pmNegativeMessages from
    dc.DC_E_TDRBS_AICH_DAYBH where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_DLBASEBANDPOOL_RAW as
  select * from dc.DC_E_TDRBS_DLBASEBANDPOOL_RAW_01 union all
  select * from dc.DC_E_TDRBS_DLBASEBANDPOOL_RAW_02 union all
  select * from dc.DC_E_TDRBS_DLBASEBANDPOOL_RAW_03;

create view dcpublic.DC_E_TDRBS_DLBASEBANDPOOL_RAW
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    Equipment,
    Subrack,
    DownlinkBaseBandPool,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmApomcOfMdlr,
    pmApomcOfMdsr,
    pmApomcOfSpreadersUsed,
    pmNoOfRlAdditionFailuresSf128,
    pmNoOfRlAdditionFailuresSf16,
    pmNoOfRlAdditionFailuresSf256,
    pmNoOfRlAdditionFailuresSf32,
    pmNoOfRlAdditionFailuresSf4,
    pmNoOfRlAdditionFailuresSf64,
    pmNoOfRlAdditionFailuresSf8,
    pmSetupAttemptsSf128,
    pmSetupAttemptsSf16,
    pmSetupAttemptsSf256,
    pmSetupAttemptsSf32,
    pmSetupAttemptsSf4,
    pmSetupAttemptsSf64,
    pmSetupAttemptsSf8,
    pmSetupFailuresSf128,
    pmSetupFailuresSf16,
    pmSetupFailuresSf256,
    pmSetupFailuresSf32,
    pmSetupFailuresSf4,
    pmSetupFailuresSf64,
    pmSetupFailuresSf8 from
    dc.DC_E_TDRBS_DLBASEBANDPOOL_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_DLBASEBANDPOOL_DAY as
  select * from dc.DC_E_TDRBS_DLBASEBANDPOOL_DAY_01 union all
  select * from dc.DC_E_TDRBS_DLBASEBANDPOOL_DAY_02 union all
  select * from dc.DC_E_TDRBS_DLBASEBANDPOOL_DAY_03;

create view dcpublic.DC_E_TDRBS_DLBASEBANDPOOL_DAY
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    Equipment,
    Subrack,
    DownlinkBaseBandPool,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmApomcOfMdlr,
    pmApomcOfMdsr,
    pmApomcOfSpreadersUsed,
    pmNoOfRlAdditionFailuresSf128,
    pmNoOfRlAdditionFailuresSf16,
    pmNoOfRlAdditionFailuresSf256,
    pmNoOfRlAdditionFailuresSf32,
    pmNoOfRlAdditionFailuresSf4,
    pmNoOfRlAdditionFailuresSf64,
    pmNoOfRlAdditionFailuresSf8,
    pmSetupAttemptsSf128,
    pmSetupAttemptsSf16,
    pmSetupAttemptsSf256,
    pmSetupAttemptsSf32,
    pmSetupAttemptsSf4,
    pmSetupAttemptsSf64,
    pmSetupAttemptsSf8,
    pmSetupFailuresSf128,
    pmSetupFailuresSf16,
    pmSetupFailuresSf256,
    pmSetupFailuresSf32,
    pmSetupFailuresSf4,
    pmSetupFailuresSf64,
    pmSetupFailuresSf8 from
    dc.DC_E_TDRBS_DLBASEBANDPOOL_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_HSDSCHRES_DAY as
  select * from dc.DC_E_TDRBS_HSDSCHRES_DAY_01 union all
  select * from dc.DC_E_TDRBS_HSDSCHRES_DAY_02 union all
  select * from dc.DC_E_TDRBS_HSDSCHRES_DAY_03 union all
  select * from dc.DC_E_TDRBS_HSDSCHRES_DAY_04 union all
  select * from dc.DC_E_TDRBS_HSDSCHRES_DAY_05 union all
  select * from dc.DC_E_TDRBS_HSDSCHRES_DAY_06;

create view dcpublic.DC_E_TDRBS_HSDSCHRES_DAY
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    HsDschResources,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmAckReceived,
    pmIubMacdPduCellReceivedBits,
    pmNackReceived,
    pmNoActiveSubFrames,
    pmNoInactiveRequiredSubFrames,
    pmSumAckedBits,
    pmSumNonEmptyUserBuffers,
    pmSumTransmittedBits,
    pmSumNumHsPdschCodesAdded,
    pmSampleNumHsPdschCodesAdded from
    dc.DC_E_TDRBS_HSDSCHRES_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_HSDSCHRES_RAW as
  select * from dc.DC_E_TDRBS_HSDSCHRES_RAW_01 union all
  select * from dc.DC_E_TDRBS_HSDSCHRES_RAW_02 union all
  select * from dc.DC_E_TDRBS_HSDSCHRES_RAW_03 union all
  select * from dc.DC_E_TDRBS_HSDSCHRES_RAW_04 union all
  select * from dc.DC_E_TDRBS_HSDSCHRES_RAW_05 union all
  select * from dc.DC_E_TDRBS_HSDSCHRES_RAW_06;

create view dcpublic.DC_E_TDRBS_HSDSCHRES_RAW
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    HsDschResources,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmAckReceived,
    pmIubMacdPduCellReceivedBits,
    pmNackReceived,
    pmNoActiveSubFrames,
    pmNoInactiveRequiredSubFrames,
    pmSumAckedBits,
    pmSumNonEmptyUserBuffers,
    pmSumTransmittedBits,
    pmSumNumHsPdschCodesAdded,
    pmSampleNumHsPdschCodesAdded from
    dc.DC_E_TDRBS_HSDSCHRES_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_HSDSCHRES_DAYBH as
  select * from dc.DC_E_TDRBS_HSDSCHRES_DAYBH_01 union all
  select * from dc.DC_E_TDRBS_HSDSCHRES_DAYBH_02 union all
  select * from dc.DC_E_TDRBS_HSDSCHRES_DAYBH_03 union all
  select * from dc.DC_E_TDRBS_HSDSCHRES_DAYBH_04 union all
  select * from dc.DC_E_TDRBS_HSDSCHRES_DAYBH_05 union all
  select * from dc.DC_E_TDRBS_HSDSCHRES_DAYBH_06;

create view dcpublic.DC_E_TDRBS_HSDSCHRES_DAYBH
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    HsDschResources,
    DATE_ID,
    MIN_ID,
    BHTYPE,
    BUSYHOUR,
    BHCLASS,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmAckReceived,
    pmIubMacdPduCellReceivedBits,
    pmNackReceived,
    pmNoActiveSubFrames,
    pmNoInactiveRequiredSubFrames,
    pmSumAckedBits,
    pmSumNonEmptyUserBuffers,
    pmSumTransmittedBits,
    pmSumNumHsPdschCodesAdded,
    pmSampleNumHsPdschCodesAdded from
    dc.DC_E_TDRBS_HSDSCHRES_DAYBH where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_RADIOLINKS_DAY as
  select * from dc.DC_E_TDRBS_RADIOLINKS_DAY_01 union all
  select * from dc.DC_E_TDRBS_RADIOLINKS_DAY_02 union all
  select * from dc.DC_E_TDRBS_RADIOLINKS_DAY_03 union all
  select * from dc.DC_E_TDRBS_RADIOLINKS_DAY_04 union all
  select * from dc.DC_E_TDRBS_RADIOLINKS_DAY_05 union all
  select * from dc.DC_E_TDRBS_RADIOLINKS_DAY_06;

create view dcpublic.DC_E_TDRBS_RADIOLINKS_DAY
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    RadioLinks,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmRLSSupSynchToUnsynch,
    pmRLSSupWaitToOutOfSynch from
    dc.DC_E_TDRBS_RADIOLINKS_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_RADIOLINKS_RAW as
  select * from dc.DC_E_TDRBS_RADIOLINKS_RAW_01 union all
  select * from dc.DC_E_TDRBS_RADIOLINKS_RAW_02 union all
  select * from dc.DC_E_TDRBS_RADIOLINKS_RAW_03 union all
  select * from dc.DC_E_TDRBS_RADIOLINKS_RAW_04 union all
  select * from dc.DC_E_TDRBS_RADIOLINKS_RAW_05 union all
  select * from dc.DC_E_TDRBS_RADIOLINKS_RAW_06;

create view dcpublic.DC_E_TDRBS_RADIOLINKS_RAW
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    RadioLinks,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmRLSSupSynchToUnsynch,
    pmRLSSupWaitToOutOfSynch from
    dc.DC_E_TDRBS_RADIOLINKS_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_RADIOLINKS_DAYBH as
  select * from dc.DC_E_TDRBS_RADIOLINKS_DAYBH_01 union all
  select * from dc.DC_E_TDRBS_RADIOLINKS_DAYBH_02 union all
  select * from dc.DC_E_TDRBS_RADIOLINKS_DAYBH_03 union all
  select * from dc.DC_E_TDRBS_RADIOLINKS_DAYBH_04 union all
  select * from dc.DC_E_TDRBS_RADIOLINKS_DAYBH_05 union all
  select * from dc.DC_E_TDRBS_RADIOLINKS_DAYBH_06;

create view dcpublic.DC_E_TDRBS_RADIOLINKS_DAYBH
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    RadioLinks,
    DATE_ID,
    MIN_ID,
    BHTYPE,
    BUSYHOUR,
    BHCLASS,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmRLSSupSynchToUnsynch,
    pmRLSSupWaitToOutOfSynch from
    dc.DC_E_TDRBS_RADIOLINKS_DAYBH where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_SCCPCH_RAW as
  select * from dc.DC_E_TDRBS_SCCPCH_RAW_01 union all
  select * from dc.DC_E_TDRBS_SCCPCH_RAW_02 union all
  select * from dc.DC_E_TDRBS_SCCPCH_RAW_03 union all
  select * from dc.DC_E_TDRBS_SCCPCH_RAW_04 union all
  select * from dc.DC_E_TDRBS_SCCPCH_RAW_05 union all
  select * from dc.DC_E_TDRBS_SCCPCH_RAW_06;

create view dcpublic.DC_E_TDRBS_SCCPCH_RAW
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    Sccpch,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfTfc1OnFach1,
    pmNoOfTfc2OnFach1,
    pmNoOfTfc3OnFach2 from
    dc.DC_E_TDRBS_SCCPCH_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_SCCPCH_DAYBH as
  select * from dc.DC_E_TDRBS_SCCPCH_DAYBH_01 union all
  select * from dc.DC_E_TDRBS_SCCPCH_DAYBH_02 union all
  select * from dc.DC_E_TDRBS_SCCPCH_DAYBH_03 union all
  select * from dc.DC_E_TDRBS_SCCPCH_DAYBH_04 union all
  select * from dc.DC_E_TDRBS_SCCPCH_DAYBH_05 union all
  select * from dc.DC_E_TDRBS_SCCPCH_DAYBH_06;

create view dcpublic.DC_E_TDRBS_SCCPCH_DAYBH
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    Sccpch,
    DATE_ID,
    MIN_ID,
    BHTYPE,
    BUSYHOUR,
    BHCLASS,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmNoOfTfc1OnFach1,
    pmNoOfTfc2OnFach1,
    pmNoOfTfc3OnFach2 from
    dc.DC_E_TDRBS_SCCPCH_DAYBH where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_SCCPCH_DAY as
  select * from dc.DC_E_TDRBS_SCCPCH_DAY_01 union all
  select * from dc.DC_E_TDRBS_SCCPCH_DAY_02 union all
  select * from dc.DC_E_TDRBS_SCCPCH_DAY_03 union all
  select * from dc.DC_E_TDRBS_SCCPCH_DAY_04 union all
  select * from dc.DC_E_TDRBS_SCCPCH_DAY_05 union all
  select * from dc.DC_E_TDRBS_SCCPCH_DAY_06;

create view dcpublic.DC_E_TDRBS_SCCPCH_DAY
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    Sccpch,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmNoOfTfc1OnFach1,
    pmNoOfTfc2OnFach1,
    pmNoOfTfc3OnFach2 from
    dc.DC_E_TDRBS_SCCPCH_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_CARRIER_V_DAY as
  select * from dc.DC_E_TDRBS_CARRIER_V_DAY_01 union all
  select * from dc.DC_E_TDRBS_CARRIER_V_DAY_02 union all
  select * from dc.DC_E_TDRBS_CARRIER_V_DAY_03 union all
  select * from dc.DC_E_TDRBS_CARRIER_V_DAY_04;

create view dcpublic.DC_E_TDRBS_CARRIER_V_DAY
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    DCVECTOR_INDEX,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmAverageRssi,
    pmTransmittedCarrierPower from
    dc.DC_E_TDRBS_CARRIER_V_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_CARRIER_V_RAW as
  select * from dc.DC_E_TDRBS_CARRIER_V_RAW_01 union all
  select * from dc.DC_E_TDRBS_CARRIER_V_RAW_02 union all
  select * from dc.DC_E_TDRBS_CARRIER_V_RAW_03 union all
  select * from dc.DC_E_TDRBS_CARRIER_V_RAW_04 union all
  select * from dc.DC_E_TDRBS_CARRIER_V_RAW_05 union all
  select * from dc.DC_E_TDRBS_CARRIER_V_RAW_06 union all
  select * from dc.DC_E_TDRBS_CARRIER_V_RAW_07 union all
  select * from dc.DC_E_TDRBS_CARRIER_V_RAW_08 union all
  select * from dc.DC_E_TDRBS_CARRIER_V_RAW_09;

create view dcpublic.DC_E_TDRBS_CARRIER_V_RAW
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    DCVECTOR_INDEX,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmAverageRssi,
    pmTransmittedCarrierPower from
    dc.DC_E_TDRBS_CARRIER_V_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_CARRIER_V_DAYBH as
  select * from dc.DC_E_TDRBS_CARRIER_V_DAYBH_01 union all
  select * from dc.DC_E_TDRBS_CARRIER_V_DAYBH_02 union all
  select * from dc.DC_E_TDRBS_CARRIER_V_DAYBH_03 union all
  select * from dc.DC_E_TDRBS_CARRIER_V_DAYBH_04;

create view dcpublic.DC_E_TDRBS_CARRIER_V_DAYBH
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    DCVECTOR_INDEX,
    DATE_ID,
    MIN_ID,
    BHTYPE,
    BUSYHOUR,
    BHCLASS,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmAverageRssi,
    pmTransmittedCarrierPower from
    dc.DC_E_TDRBS_CARRIER_V_DAYBH where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_DLBASEBANDPOOL_V_RAW as
  select * from dc.DC_E_TDRBS_DLBASEBANDPOOL_V_RAW_01 union all
  select * from dc.DC_E_TDRBS_DLBASEBANDPOOL_V_RAW_02 union all
  select * from dc.DC_E_TDRBS_DLBASEBANDPOOL_V_RAW_03 union all
  select * from dc.DC_E_TDRBS_DLBASEBANDPOOL_V_RAW_04 union all
  select * from dc.DC_E_TDRBS_DLBASEBANDPOOL_V_RAW_05 union all
  select * from dc.DC_E_TDRBS_DLBASEBANDPOOL_V_RAW_06 union all
  select * from dc.DC_E_TDRBS_DLBASEBANDPOOL_V_RAW_07 union all
  select * from dc.DC_E_TDRBS_DLBASEBANDPOOL_V_RAW_08 union all
  select * from dc.DC_E_TDRBS_DLBASEBANDPOOL_V_RAW_09;

create view dcpublic.DC_E_TDRBS_DLBASEBANDPOOL_V_RAW
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    Equipment,
    Subrack,
    DownlinkBaseBandPool,
    DCVECTOR_INDEX,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfRadioLinksSf128,
    pmNoOfRadioLinksSf16,
    pmNoOfRadioLinksSf256,
    pmNoOfRadioLinksSf32,
    pmNoOfRadioLinksSf4,
    pmNoOfRadioLinksSf64,
    pmNoOfRadioLinksSf8 from
    dc.DC_E_TDRBS_DLBASEBANDPOOL_V_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_DLBASEBANDPOOL_V_DAY as
  select * from dc.DC_E_TDRBS_DLBASEBANDPOOL_V_DAY_01 union all
  select * from dc.DC_E_TDRBS_DLBASEBANDPOOL_V_DAY_02 union all
  select * from dc.DC_E_TDRBS_DLBASEBANDPOOL_V_DAY_03 union all
  select * from dc.DC_E_TDRBS_DLBASEBANDPOOL_V_DAY_04;

create view dcpublic.DC_E_TDRBS_DLBASEBANDPOOL_V_DAY
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    Equipment,
    Subrack,
    DownlinkBaseBandPool,
    DCVECTOR_INDEX,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmNoOfRadioLinksSf128,
    pmNoOfRadioLinksSf16,
    pmNoOfRadioLinksSf256,
    pmNoOfRadioLinksSf32,
    pmNoOfRadioLinksSf4,
    pmNoOfRadioLinksSf64,
    pmNoOfRadioLinksSf8 from
    dc.DC_E_TDRBS_DLBASEBANDPOOL_V_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_EDCHRESOURCES_V_RAW as
  select * from dc.DC_E_TDRBS_EDCHRESOURCES_V_RAW_01 union all
  select * from dc.DC_E_TDRBS_EDCHRESOURCES_V_RAW_02 union all
  select * from dc.DC_E_TDRBS_EDCHRESOURCES_V_RAW_03 union all
  select * from dc.DC_E_TDRBS_EDCHRESOURCES_V_RAW_04 union all
  select * from dc.DC_E_TDRBS_EDCHRESOURCES_V_RAW_05 union all
  select * from dc.DC_E_TDRBS_EDCHRESOURCES_V_RAW_06 union all
  select * from dc.DC_E_TDRBS_EDCHRESOURCES_V_RAW_07 union all
  select * from dc.DC_E_TDRBS_EDCHRESOURCES_V_RAW_08 union all
  select * from dc.DC_E_TDRBS_EDCHRESOURCES_V_RAW_09;

create view dcpublic.DC_E_TDRBS_EDCHRESOURCES_V_RAW
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    EDchResources,
    DCVECTOR_INDEX,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmTotRateGrantedEul,
    pmWaitingTimeEul,
    pmTotalRotCoverage,
    pmOwnUuLoad,
    pmNoiseFloor,
    pmCommonChPowerEul,
    pmNoSchEdchEul from
    dc.DC_E_TDRBS_EDCHRESOURCES_V_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_EDCHRESOURCES_V_DAY as
  select * from dc.DC_E_TDRBS_EDCHRESOURCES_V_DAY_01 union all
  select * from dc.DC_E_TDRBS_EDCHRESOURCES_V_DAY_02 union all
  select * from dc.DC_E_TDRBS_EDCHRESOURCES_V_DAY_03 union all
  select * from dc.DC_E_TDRBS_EDCHRESOURCES_V_DAY_04;

create view dcpublic.DC_E_TDRBS_EDCHRESOURCES_V_DAY
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    EDchResources,
    DCVECTOR_INDEX,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmTotRateGrantedEul,
    pmWaitingTimeEul,
    pmTotalRotCoverage,
    pmOwnUuLoad,
    pmNoiseFloor,
    pmCommonChPowerEul,
    pmNoSchEdchEul from
    dc.DC_E_TDRBS_EDCHRESOURCES_V_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_EDCHRESOURCES_V_DAYBH as
  select * from dc.DC_E_TDRBS_EDCHRESOURCES_V_DAYBH_01 union all
  select * from dc.DC_E_TDRBS_EDCHRESOURCES_V_DAYBH_02 union all
  select * from dc.DC_E_TDRBS_EDCHRESOURCES_V_DAYBH_03 union all
  select * from dc.DC_E_TDRBS_EDCHRESOURCES_V_DAYBH_04;

create view dcpublic.DC_E_TDRBS_EDCHRESOURCES_V_DAYBH
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    EDchResources,
    DCVECTOR_INDEX,
    DATE_ID,
    MIN_ID,
    BHTYPE,
    BUSYHOUR,
    BHCLASS,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmTotRateGrantedEul,
    pmWaitingTimeEul,
    pmTotalRotCoverage,
    pmOwnUuLoad,
    pmNoiseFloor,
    pmCommonChPowerEul,
    pmNoSchEdchEul from
    dc.DC_E_TDRBS_EDCHRESOURCES_V_DAYBH where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_HSDSCHRES_V_RAW as
  select * from dc.DC_E_TDRBS_HSDSCHRES_V_RAW_01 union all
  select * from dc.DC_E_TDRBS_HSDSCHRES_V_RAW_02 union all
  select * from dc.DC_E_TDRBS_HSDSCHRES_V_RAW_03 union all
  select * from dc.DC_E_TDRBS_HSDSCHRES_V_RAW_04 union all
  select * from dc.DC_E_TDRBS_HSDSCHRES_V_RAW_05 union all
  select * from dc.DC_E_TDRBS_HSDSCHRES_V_RAW_06 union all
  select * from dc.DC_E_TDRBS_HSDSCHRES_V_RAW_07 union all
  select * from dc.DC_E_TDRBS_HSDSCHRES_V_RAW_08 union all
  select * from dc.DC_E_TDRBS_HSDSCHRES_V_RAW_09;

create view dcpublic.DC_E_TDRBS_HSDSCHRES_V_RAW
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    HsDschResources,
    DCVECTOR_INDEX,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmAverageUserRate,
    pmReportedCqi,
    pmTransmittedCarrierPowerNonHs,
    pmUsedCqi,
    pmSumOfHsScchUsedPwr,
    pmNoOfHsUsersPerTti,
    pmRemainingResourceCheck from
    dc.DC_E_TDRBS_HSDSCHRES_V_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_HSDSCHRES_V_DAY as
  select * from dc.DC_E_TDRBS_HSDSCHRES_V_DAY_01 union all
  select * from dc.DC_E_TDRBS_HSDSCHRES_V_DAY_02 union all
  select * from dc.DC_E_TDRBS_HSDSCHRES_V_DAY_03 union all
  select * from dc.DC_E_TDRBS_HSDSCHRES_V_DAY_04;

create view dcpublic.DC_E_TDRBS_HSDSCHRES_V_DAY
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    HsDschResources,
    DCVECTOR_INDEX,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmAverageUserRate,
    pmReportedCqi,
    pmTransmittedCarrierPowerNonHs,
    pmUsedCqi,
    pmSumOfHsScchUsedPwr,
    pmNoOfHsUsersPerTti,
    pmRemainingResourceCheck from
    dc.DC_E_TDRBS_HSDSCHRES_V_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_HSDSCHRES_V_DAYBH as
  select * from dc.DC_E_TDRBS_HSDSCHRES_V_DAYBH_01 union all
  select * from dc.DC_E_TDRBS_HSDSCHRES_V_DAYBH_02 union all
  select * from dc.DC_E_TDRBS_HSDSCHRES_V_DAYBH_03 union all
  select * from dc.DC_E_TDRBS_HSDSCHRES_V_DAYBH_04;

create view dcpublic.DC_E_TDRBS_HSDSCHRES_V_DAYBH
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    HsDschResources,
    DCVECTOR_INDEX,
    DATE_ID,
    MIN_ID,
    BHTYPE,
    BUSYHOUR,
    BHCLASS,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmAverageUserRate,
    pmReportedCqi,
    pmTransmittedCarrierPowerNonHs,
    pmUsedCqi,
    pmSumOfHsScchUsedPwr,
    pmNoOfHsUsersPerTti,
    pmRemainingResourceCheck from
    dc.DC_E_TDRBS_HSDSCHRES_V_DAYBH where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_IUBDATASTREAMS_V_RAW as
  select * from dc.DC_E_TDRBS_IUBDATASTREAMS_V_RAW_01 union all
  select * from dc.DC_E_TDRBS_IUBDATASTREAMS_V_RAW_02 union all
  select * from dc.DC_E_TDRBS_IUBDATASTREAMS_V_RAW_03 union all
  select * from dc.DC_E_TDRBS_IUBDATASTREAMS_V_RAW_04 union all
  select * from dc.DC_E_TDRBS_IUBDATASTREAMS_V_RAW_05 union all
  select * from dc.DC_E_TDRBS_IUBDATASTREAMS_V_RAW_06 union all
  select * from dc.DC_E_TDRBS_IUBDATASTREAMS_V_RAW_07 union all
  select * from dc.DC_E_TDRBS_IUBDATASTREAMS_V_RAW_08 union all
  select * from dc.DC_E_TDRBS_IUBDATASTREAMS_V_RAW_09;

create view dcpublic.DC_E_TDRBS_IUBDATASTREAMS_V_RAW
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    NodeBFunction,
    Iub,
    IubDataStreams,
    DCVECTOR_INDEX,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmHsDataFrameDelayIub,
    pmIubMacdPduRbsReceivedBits,
    pmTargetHsRate from
    dc.DC_E_TDRBS_IUBDATASTREAMS_V_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_IUBDATASTREAMS_V_DAY as
  select * from dc.DC_E_TDRBS_IUBDATASTREAMS_V_DAY_01 union all
  select * from dc.DC_E_TDRBS_IUBDATASTREAMS_V_DAY_02 union all
  select * from dc.DC_E_TDRBS_IUBDATASTREAMS_V_DAY_03 union all
  select * from dc.DC_E_TDRBS_IUBDATASTREAMS_V_DAY_04;

create view dcpublic.DC_E_TDRBS_IUBDATASTREAMS_V_DAY
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    NodeBFunction,
    Iub,
    IubDataStreams,
    DCVECTOR_INDEX,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmHsDataFrameDelayIub,
    pmIubMacdPduRbsReceivedBits,
    pmTargetHsRate from
    dc.DC_E_TDRBS_IUBDATASTREAMS_V_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_PRACH_V_RAW as
  select * from dc.DC_E_TDRBS_PRACH_V_RAW_01 union all
  select * from dc.DC_E_TDRBS_PRACH_V_RAW_02 union all
  select * from dc.DC_E_TDRBS_PRACH_V_RAW_03 union all
  select * from dc.DC_E_TDRBS_PRACH_V_RAW_04 union all
  select * from dc.DC_E_TDRBS_PRACH_V_RAW_05 union all
  select * from dc.DC_E_TDRBS_PRACH_V_RAW_06 union all
  select * from dc.DC_E_TDRBS_PRACH_V_RAW_07 union all
  select * from dc.DC_E_TDRBS_PRACH_V_RAW_08 union all
  select * from dc.DC_E_TDRBS_PRACH_V_RAW_09;

create view dcpublic.DC_E_TDRBS_PRACH_V_RAW
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    Prach,
    DCVECTOR_INDEX,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmReceivedPreambleSir,
    pmPropagationDelay from
    dc.DC_E_TDRBS_PRACH_V_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_PRACH_V_DAY as
  select * from dc.DC_E_TDRBS_PRACH_V_DAY_01 union all
  select * from dc.DC_E_TDRBS_PRACH_V_DAY_02 union all
  select * from dc.DC_E_TDRBS_PRACH_V_DAY_03 union all
  select * from dc.DC_E_TDRBS_PRACH_V_DAY_04;

create view dcpublic.DC_E_TDRBS_PRACH_V_DAY
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    Prach,
    DCVECTOR_INDEX,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmReceivedPreambleSir,
    pmPropagationDelay from
    dc.DC_E_TDRBS_PRACH_V_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_PRACH_V_DAYBH as
  select * from dc.DC_E_TDRBS_PRACH_V_DAYBH_01 union all
  select * from dc.DC_E_TDRBS_PRACH_V_DAYBH_02 union all
  select * from dc.DC_E_TDRBS_PRACH_V_DAYBH_03 union all
  select * from dc.DC_E_TDRBS_PRACH_V_DAYBH_04;

create view dcpublic.DC_E_TDRBS_PRACH_V_DAYBH
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    Prach,
    DCVECTOR_INDEX,
    DATE_ID,
    MIN_ID,
    BHTYPE,
    BUSYHOUR,
    BHCLASS,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmReceivedPreambleSir,
    pmPropagationDelay from
    dc.DC_E_TDRBS_PRACH_V_DAYBH where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_RADIOLINKS_V_RAW as
  select * from dc.DC_E_TDRBS_RADIOLINKS_V_RAW_01 union all
  select * from dc.DC_E_TDRBS_RADIOLINKS_V_RAW_02 union all
  select * from dc.DC_E_TDRBS_RADIOLINKS_V_RAW_03 union all
  select * from dc.DC_E_TDRBS_RADIOLINKS_V_RAW_04 union all
  select * from dc.DC_E_TDRBS_RADIOLINKS_V_RAW_05 union all
  select * from dc.DC_E_TDRBS_RADIOLINKS_V_RAW_06 union all
  select * from dc.DC_E_TDRBS_RADIOLINKS_V_RAW_07 union all
  select * from dc.DC_E_TDRBS_RADIOLINKS_V_RAW_08 union all
  select * from dc.DC_E_TDRBS_RADIOLINKS_V_RAW_09;

create view dcpublic.DC_E_TDRBS_RADIOLINKS_V_RAW
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    RadioLinks,
    DCVECTOR_INDEX,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmAverageSir,
    pmAverageSirError,
    pmDpcchBer,
    pmDpchCodePowerSf128,
    pmDpchCodePowerSf16,
    pmDpchCodePowerSf256,
    pmDpchCodePowerSf32,
    pmDpchCodePowerSf4,
    pmDpchCodePowerSf64,
    pmDpchCodePowerSf8,
    pmDpdchBer,
    pmOutOfSynch,
    pmUlSynchTime,
    pmUlSynchTimeSHO from
    dc.DC_E_TDRBS_RADIOLINKS_V_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_RADIOLINKS_V_DAY as
  select * from dc.DC_E_TDRBS_RADIOLINKS_V_DAY_01 union all
  select * from dc.DC_E_TDRBS_RADIOLINKS_V_DAY_02 union all
  select * from dc.DC_E_TDRBS_RADIOLINKS_V_DAY_03 union all
  select * from dc.DC_E_TDRBS_RADIOLINKS_V_DAY_04;

create view dcpublic.DC_E_TDRBS_RADIOLINKS_V_DAY
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    RadioLinks,
    DCVECTOR_INDEX,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmAverageSir,
    pmAverageSirError,
    pmDpcchBer,
    pmDpchCodePowerSf128,
    pmDpchCodePowerSf16,
    pmDpchCodePowerSf256,
    pmDpchCodePowerSf32,
    pmDpchCodePowerSf4,
    pmDpchCodePowerSf64,
    pmDpchCodePowerSf8,
    pmDpdchBer,
    pmOutOfSynch,
    pmUlSynchTime,
    pmUlSynchTimeSHO from
    dc.DC_E_TDRBS_RADIOLINKS_V_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_RADIOLINKS_V_DAYBH as
  select * from dc.DC_E_TDRBS_RADIOLINKS_V_DAYBH_01 union all
  select * from dc.DC_E_TDRBS_RADIOLINKS_V_DAYBH_02 union all
  select * from dc.DC_E_TDRBS_RADIOLINKS_V_DAYBH_03 union all
  select * from dc.DC_E_TDRBS_RADIOLINKS_V_DAYBH_04;

create view dcpublic.DC_E_TDRBS_RADIOLINKS_V_DAYBH
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    NodeBFunction,
    Sector,
    Carrier,
    RadioLinks,
    DCVECTOR_INDEX,
    DATE_ID,
    MIN_ID,
    BHTYPE,
    BUSYHOUR,
    BHCLASS,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmAverageSir,
    pmAverageSirError,
    pmDpcchBer,
    pmDpchCodePowerSf128,
    pmDpchCodePowerSf16,
    pmDpchCodePowerSf256,
    pmDpchCodePowerSf32,
    pmDpchCodePowerSf4,
    pmDpchCodePowerSf64,
    pmDpchCodePowerSf8,
    pmDpdchBer,
    pmOutOfSynch,
    pmUlSynchTime,
    pmUlSynchTimeSHO from
    dc.DC_E_TDRBS_RADIOLINKS_V_DAYBH where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_ULBASEBANDPOOL_V_RAW as
  select * from dc.DC_E_TDRBS_ULBASEBANDPOOL_V_RAW_01 union all
  select * from dc.DC_E_TDRBS_ULBASEBANDPOOL_V_RAW_02 union all
  select * from dc.DC_E_TDRBS_ULBASEBANDPOOL_V_RAW_03 union all
  select * from dc.DC_E_TDRBS_ULBASEBANDPOOL_V_RAW_04 union all
  select * from dc.DC_E_TDRBS_ULBASEBANDPOOL_V_RAW_05 union all
  select * from dc.DC_E_TDRBS_ULBASEBANDPOOL_V_RAW_06 union all
  select * from dc.DC_E_TDRBS_ULBASEBANDPOOL_V_RAW_07 union all
  select * from dc.DC_E_TDRBS_ULBASEBANDPOOL_V_RAW_08 union all
  select * from dc.DC_E_TDRBS_ULBASEBANDPOOL_V_RAW_09;

create view dcpublic.DC_E_TDRBS_ULBASEBANDPOOL_V_RAW
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    Equipment,
    Subrack,
    UplinkBaseBandPool,
    DCVECTOR_INDEX,
    DATETIME_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    UTC_DATETIME_ID,
    pmNoOfRadioLinksSf128,
    pmNoOfRadioLinksSf16,
    pmNoOfRadioLinksSf256,
    pmNoOfRadioLinksSf32,
    pmNoOfRadioLinksSf4,
    pmNoOfRadioLinksSf64,
    pmNoOfRadioLinksSf8,
    pmHwCePoolEul from
    dc.DC_E_TDRBS_ULBASEBANDPOOL_V_RAW where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_ULBASEBANDPOOL_V_DAY as
  select * from dc.DC_E_TDRBS_ULBASEBANDPOOL_V_DAY_01 union all
  select * from dc.DC_E_TDRBS_ULBASEBANDPOOL_V_DAY_02 union all
  select * from dc.DC_E_TDRBS_ULBASEBANDPOOL_V_DAY_03 union all
  select * from dc.DC_E_TDRBS_ULBASEBANDPOOL_V_DAY_04;

create view dcpublic.DC_E_TDRBS_ULBASEBANDPOOL_V_DAY
  as select OSS_ID,
    SN,
    NESW,
    TDRNC,
    TDRBS,
    MOID,
    Equipment,
    Subrack,
    UplinkBaseBandPool,
    DCVECTOR_INDEX,
    DATE_ID,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG,
    pmNoOfRadioLinksSf128,
    pmNoOfRadioLinksSf16,
    pmNoOfRadioLinksSf256,
    pmNoOfRadioLinksSf32,
    pmNoOfRadioLinksSf4,
    pmNoOfRadioLinksSf64,
    pmNoOfRadioLinksSf8,
    pmHwCePoolEul from
    dc.DC_E_TDRBS_ULBASEBANDPOOL_V_DAY where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_ELEMBH_RANKBH as
  select * from dc.DC_E_TDRBS_ELEMBH_RANKBH_01 union all
  select * from dc.DC_E_TDRBS_ELEMBH_RANKBH_02 union all
  select * from dc.DC_E_TDRBS_ELEMBH_RANKBH_03 union all
  select * from dc.DC_E_TDRBS_ELEMBH_RANKBH_04 union all
  select * from dc.DC_E_TDRBS_ELEMBH_RANKBH_05 union all
  select * from dc.DC_E_TDRBS_ELEMBH_RANKBH_06;

create view dcpublic.DC_E_TDRBS_ELEMBH_RANKBH
  as select OSS_ID,
    TDRBS,
    ELEMENT_NAME,
    ELEMENT_TYPE,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG from
    dc.DC_E_TDRBS_ELEMBH_RANKBH where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view dc.DC_E_TDRBS_CARRIERBH_RANKBH as
  select * from dc.DC_E_TDRBS_CARRIERBH_RANKBH_01 union all
  select * from dc.DC_E_TDRBS_CARRIERBH_RANKBH_02 union all
  select * from dc.DC_E_TDRBS_CARRIERBH_RANKBH_03 union all
  select * from dc.DC_E_TDRBS_CARRIERBH_RANKBH_04 union all
  select * from dc.DC_E_TDRBS_CARRIERBH_RANKBH_05 union all
  select * from dc.DC_E_TDRBS_CARRIERBH_RANKBH_06;

create view dcpublic.DC_E_TDRBS_CARRIERBH_RANKBH
  as select OSS_ID,
    TDRNC,
    TDRBS,
    Sector,
    Carrier,
    PERIOD_DURATION,
    ROWSTATUS,
    DC_RELEASE,
    DC_SOURCE,
    DC_TIMEZONE,
    DC_SUSPECTFLAG from
    dc.DC_E_TDRBS_CARRIERBH_RANKBH where
    ROWSTATUS <> 'DUPLICATE' and ROWSTATUS <> 'SUSPECTED';

create view
  dc.SELECT_E_TDRBS_AGGLEVEL( FIRSTDATE_ID,AGGLEVEL,DATE_ID,YEAR_ID,MONTH_ID,DAY_ID,WEEK_ID,WEEKDAY_ID,BUSINESSDAY,
  TIMELEVEL,TIMELEVELBH,DAYS) 
  as select FIRSTDATE_ID,AGGLEVEL,DATE_ID,YEAR_ID,MONTH_ID,DAY_ID,WEEK_ID,WEEKDAY_ID,BUSINESSDAY,
    TIMELEVEL,TIMELEVELBH,DAYS from dc.SELECT_AGGLEVEL_TABLE where
    TIMELEVELBH = 'DAYBH';
