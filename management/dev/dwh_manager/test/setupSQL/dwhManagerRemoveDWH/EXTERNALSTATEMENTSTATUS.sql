insert into EXTERNALSTATEMENTSTATUS (TECHPACK_NAME, STATEMENTNAME, VERSIONID, STATUS, EXECTIME, EXECSTATEMENT) values ('DC_E_MGW', 'Delete from DIM_E_CN_MGW_DEVICETYPE', 'DC_E_MGW:((801))', 'OK', '2009-12-11 11:53:27.0', 'DELETE DIM_E_CN_MGW_DEVICETYPE;');
insert into EXTERNALSTATEMENTSTATUS (TECHPACK_NAME, STATEMENTNAME, VERSIONID, STATUS, EXECTIME, EXECSTATEMENT) values ('DC_E_MGW', 'create view SELECT_E_MGW_AGGLEVEL', 'DC_E_MGW:((802))', 'OK', '2009-12-14 10:10:04.0', 'IF (SELECT count(*) FROM sys.sysviews where viewname = ''SELECT_E_MGW_AGGLEVEL'') > 0 DROP VIEW SELECT_E_MGW_AGGLEVEL BEGIN create view SELECT_E_MGW_AGGLEVEL(FIRSTDATE_ID,AGGLEVEL,DATE_ID,YEAR_ID,MONTH_ID,DAY_ID,WEEK_ID,WEEKDAY_ID,BUSINESSDAY,TIMELEVEL,TIMELEVELBH,DAYS) as select FIRSTDATE_ID,AGGLEVEL,DATE_ID,YEAR_ID,MONTH_ID,DAY_ID,WEEK_ID,WEEKDAY_ID,BUSINESSDAY,TIMELEVEL,TIMELEVELBH,DAYS from dc.SELECT_AGGLEVEL_TABLE WHERE TIMELEVELBH = ''DAYBH'' commit grant select on SELECT_E_MGW_AGGLEVEL to dcbo END');
insert into EXTERNALSTATEMENTSTATUS (TECHPACK_NAME, STATEMENTNAME, VERSIONID, STATUS, EXECTIME, EXECSTATEMENT) values ('DC_E_MGW', 'Insert into DIM_E_CN_MGW_DEVICETYPE', 'DC_E_MGW:((802))', 'OK', '2009-12-14 10:10:05.0', 'INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''DTMF Sender'',1); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''Tone Sender'',2); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''Continuity Check'',4); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''DTMF Receiver'',8); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''MCC'',16); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''EC'',32); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''NR'',64); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''GTT'',128); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''AMR'',256); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''Inmarsat'',512); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''UP FH'',1024); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''CSD GSM FH'',2048); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''CSD Digital'',4096); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''CSD Modem'',8192); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''IM'',16384); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''MPC'',32768); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''RTP/RTCP'',65536); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''Reserved'',131072); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''Jitter Handling'',262144); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''EFR'',524288); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''TFO'',1048576); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''CSD GSM Fax'',2097152); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''PCM'',4194304); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''IPET'',8388608); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''AMR-WB'',16777216); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''Reserved'',33554432); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''G.729'',67108864);');
insert into EXTERNALSTATEMENTSTATUS (TECHPACK_NAME, STATEMENTNAME, VERSIONID, STATUS, EXECTIME, EXECSTATEMENT) values ('DC_E_MGW', 'create view SELECT_E_MGW_AGGLEVEL', 'DC_E_MGW:((801))', 'OK', '2009-12-11 11:53:27.0', 'IF (SELECT count(*) FROM sys.sysviews where viewname = ''SELECT_E_MGW_AGGLEVEL'') > 0 DROP VIEW SELECT_E_MGW_AGGLEVEL BEGIN create view SELECT_E_MGW_AGGLEVEL(FIRSTDATE_ID,AGGLEVEL,DATE_ID,YEAR_ID,MONTH_ID,DAY_ID,WEEK_ID,WEEKDAY_ID,BUSINESSDAY,TIMELEVEL,TIMELEVELBH,DAYS) as select FIRSTDATE_ID,AGGLEVEL,DATE_ID,YEAR_ID,MONTH_ID,DAY_ID,WEEK_ID,WEEKDAY_ID,BUSINESSDAY,TIMELEVEL,TIMELEVELBH,DAYS from dc.SELECT_AGGLEVEL_TABLE WHERE TIMELEVELBH = ''DAYBH'' commit grant select on SELECT_E_MGW_AGGLEVEL to dcbo END');
insert into EXTERNALSTATEMENTSTATUS (TECHPACK_NAME, STATEMENTNAME, VERSIONID, STATUS, EXECTIME, EXECSTATEMENT) values ('DC_E_MGW', 'Insert into DIM_E_CN_MGW_DEVICETYPE', 'DC_E_MGW:((801))', 'OK', '2009-12-11 11:53:27.0', 'INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''DTMF Sender'',1); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''Tone Sender'',2); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''Continuity Check'',4); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''DTMF Receiver'',8); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''MCC'',16); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''EC'',32); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''NR'',64); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''GTT'',128); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''AMR'',256); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''Inmarsat'',512); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''UP FH'',1024); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''CSD GSM FH'',2048); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''CSD Digital'',4096); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''CSD Modem'',8192); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''IM'',16384); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''MPC'',32768); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''RTP/RTCP'',65536); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''Reserved'',131072); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''Jitter Handling'',262144); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''EFR'',524288); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''TFO'',1048576); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''CSD GSM Fax'',2097152); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''PCM'',4194304); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''IPET'',8388608); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''AMR-WB'',16777216); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''Reserved'',33554432); INSERT INTO DIM_E_CN_MGW_DEVICETYPE (SERVICE_NAME,SERVICE_NUMBER) VALUES (''G.729'',67108864);');
insert into EXTERNALSTATEMENTSTATUS (TECHPACK_NAME, STATEMENTNAME, VERSIONID, STATUS, EXECTIME, EXECSTATEMENT) values ('DC_E_MGW', 'Delete from DIM_E_CN_MGW_DEVICETYPE', 'DC_E_MGW:((802))', 'OK', '2009-12-14 10:10:05.0', 'DELETE DIM_E_CN_MGW_DEVICETYPE;');
