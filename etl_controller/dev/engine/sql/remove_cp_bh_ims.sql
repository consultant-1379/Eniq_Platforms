delete from  BusyhourRankkeys where  bhtype  like 'CP%' and (versionid like 'DC_E_IMS:%' or  versionid like 'DC_E_MTAS%')
delete from BusyhourSource where bhtype  like 'CP%' and (versionid like 'DC_E_IMS:%' or versionid like 'DC_E_MTAS%')
update Busyhour set bhcriteria=' ',  whereclause=' ', description=' ', enable ='0'  where enable='1' and bhtype like 'CP%' and (versionid like 'DC_E_IMS:%' or  versionid like 'DC_E_MTAS%')
update AggregationRule set enable='0' where  bhtype like '%CP%' and enable ='1' and (versionid like 'DC_E_IMS:%' or  versionid like 'DC_E_MTAS%')