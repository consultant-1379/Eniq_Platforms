
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="javax.servlet.http.HttpSession"%>
<%@ page import="org.apache.velocity.app.Velocity"%>
<%@ page import="java.util.concurrent.Executors"%>
<%@ page import="java.util.concurrent.ExecutorService"%>
<%@ page import="java.util.concurrent.ThreadFactory"%>
<%@ page import="java.util.concurrent.atomic.AtomicInteger;"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<%!private static class AdminUIDefaultThreadFactory implements ThreadFactory {

    static final AtomicInteger poolNumber = new AtomicInteger(1);

    final ThreadGroup group;

    final AtomicInteger threadNumber = new AtomicInteger(1);

    final String namePrefix;

    private AdminUIDefaultThreadFactory() {
      final SecurityManager s = System.getSecurityManager();
      group = (s == null) ? Thread.currentThread().getThreadGroup() : s.getThreadGroup();
      namePrefix = "adminui-pool-" + poolNumber.getAndIncrement() + "-thread-";
    }

    public Thread newThread(final Runnable r) {
      final Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
      if (!t.isDaemon()) {
        t.setDaemon(true);
      }
      if (t.getPriority() != Thread.NORM_PRIORITY) {
        t.setPriority(Thread.NORM_PRIORITY);
      }
      return t;
    }

  }%>

<%
  final String path = getServletContext().getRealPath("/");
  Velocity.setProperty(Velocity.RESOURCE_LOADER, "class");
  Velocity.setProperty("class.resource.loader.class",
      "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
  Velocity.setProperty("runtime.log", path + "velocity.log");
  Velocity.init();

  HttpSession ses = request.getSession(true);

  getServletContext().setAttribute("ExecutorService",
      Executors.newSingleThreadExecutor(new AdminUIDefaultThreadFactory()));
%>
<head>
<link rel="stylesheet" type="text/css" href="css/busyhourcfg.css" />
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta http-equiv="refresh"
	content="2;url=/busyhourcfg/servlet/TechpackView" />
<title>Ericsson Network IQ - Busyhour Configuration Interface</title>
</head>
<body>
<br />
This file is only for development environment, no need to include this
in build...
<br />
<br />
Created new session
<%=ses.getId()%>
</body>
</html>