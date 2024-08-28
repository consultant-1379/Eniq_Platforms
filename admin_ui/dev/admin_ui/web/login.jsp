<jsp:useBean id="loginForm"
	class="com.distocraft.dc5000.etl.gui.login.LoginForm" scope="session">
</jsp:useBean>
<jsp:setProperty name="loginForm" property="*" />
<%
	boolean validDetails = false;
	final String contextPath = request.getContextPath();
	if ("true".equals(request.getParameter("process")) && loginForm.process()) {
            validDetails = true;
    }
%>
<html>
	<script language="javascript">
        function submitIfPosted() {
            var submit = <%= validDetails %>;
            if(submit) {
                document.forms["login"].username.disabled=true;
                document.forms["login"].password.disabled=true;
                document.forms["login"].submit.disabled=true;
                document.forms["hiddenLogin"].submit();
                document.getElementById('loginMessage').innerHTML = 'You are now being logged in, please wait...';
            } else {
            	document.getElementById('username').focus();
            }
        }
	  </script>
	<head>
		<title>Ericsson Network IQ</title>
		<link rel="shortcut icon" href="<%=contextPath%>/img/favicon.ico">
		<script language="javascript" src="<%=contextPath%>/javascript/PreventXSS.js"></script>
	</head>
	<body bgcolor="#ffffff"	onload="submitIfPosted();">
		<center>
			<br/>
			<br/>
			<img src="<%=contextPath%>/eniq_blue_banner.jpg" />
			<br/>
			<br/>
			<font face="Verdana, Helvetica, Arial" size=3>Management Interface - Login</font>
			<br/>
			<br/>
	<%
	    if (request.getSession().isNew() == false) {
	        out.print("<font face=\"Verdana, Helvetica, Arial\" size=\"2\" id=\"loginMessage\" color=\"red\"><b>You have logged out or your session has expired. Please login again.</b></font></br></br>");
	    }
	%> 
			<font face="Verdana, Helvetica, Arial" size=2>Please, type your username and password.</font>
			<br/>
			<form id="hiddenLogin" action="<%=contextPath%>/j_security_check" method="POST">
                <input type="HIDDEN" name="j_username" value='<%=loginForm.getUserName()%>'>
                <input type="HIDDEN" name="j_password" value='<%=loginForm.getUserPassword()%>'>
            </form>
			<form id="login" action='<%=request.getRequestURI()%>' method="POST" onSubmit="return filterFields();">
				<table border=1>
					<tr>
						<td>
							<table border=0 bgcolor="#B3B3FF">
									<tr>
										<td>
											<font face="Verdana, Helvetica, Arial" size=2>Username: </font>
										</td>
										<td>
											<input type="TEXT" name="userName" id="username" style="font-size: 12;" value='<%=loginForm.getUserName()%>'>
										</td>
									</tr>
									<tr>
										<td>
											<font face="Verdana, Helvetica, Arial" size=2>Password: </font>
										</td>
										<td>
											<input type="PASSWORD" name="userPassword" style="font-size: 12;" id="password" value='<%=loginForm.getUserPassword()%>'>
										</td>
									</tr>
									<tr>
										<td colspan=2>
											<br />
											<input type=submit value="Login" name="submit" id="submit" style="font-size: 13f;">
											<input type="HIDDEN" name="process" value="true">
										</td>
									</tr>
							</table>
						</td>
					</tr>
				</table>
			</form>
		</center>
	</body>
</html>
