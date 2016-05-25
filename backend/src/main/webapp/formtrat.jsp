<%@page contentType="text/html; charset=iso-8859-1" session="true" language="java" import="java.text.*" import="java.util.*" %>

<%
String in1 = request.getParameter("in1");
String ti1 = request.getParameter("ti1");
String ni1 = request.getParameter("ni1");
String pi1 = request.getParameter("pi1");

String in2 = request.getParameter("in2");
String ti2 = request.getParameter("ti2");
String ni2 = request.getParameter("ni2");
String pi2 = request.getParameter("pi2");

String data=in1+"_"+ti1+"_"+ni1+"_"+pi1+"-"+in2+"_"+ti2+"_"+ni2+"_"+pi2;
String url="http://zxing.org/w/chart?cht=qr&chs=350x350&chld=L&choe=UTF-8&chl="+data;
out.print(url);
%>