<%@ page import="java.io.*" %>
<%@ page import="java.lang.*" %>
<%

String path = request.getContextPath();

%>

<html>
<head>
<title>Ericsson Network IQ</title>
<script>
function redirect(){
	window.location.href = "<% out.print(path); %>/servlet/LoaderStatusServlet";
}
</script>
</head>
 
<body onLoad="redirect();"> 
</body>
 
</html>
 