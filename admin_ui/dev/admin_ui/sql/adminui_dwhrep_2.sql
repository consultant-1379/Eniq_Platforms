IF (SELECT count(*) FROM ENIQ_EVENTS_ADMIN_PROPERTIES WHERE PARAM_NAME='ENIQ_EVENTS_LAST_LOGIN_THRESHOLD') = 0
BEGIN
	INSERT INTO ENIQ_EVENTS_ADMIN_PROPERTIES (PARAM_NAME, PARAM_VALUE, MODIFIED_BY) VALUES ('ENIQ_EVENTS_LAST_LOGIN_THRESHOLD', '90', 'dcuser')
END
