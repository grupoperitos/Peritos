<%@page contentType="text/html; charset=iso-8859-1" session="true" language="java" import="java.text.*" import="java.util.*" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.epsl.peritos.backend.Admin" %>
<%@ page import="com.googlecode.objectify.ObjectifyService" %>

<%
String u = request.getParameter("u");
String p = request.getParameter("p");
String n = request.getParameter("n");
String e = request.getParameter("e");

if(u!=null && p!=null){
    Admin a = new Admin();
    a.setUser(u);
    a.setPass(p);
    a.setName(n);
    a.setEmail(e);
    ObjectifyService.ofy().save().entity(a).now();
}
%>