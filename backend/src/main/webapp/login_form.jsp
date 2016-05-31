<%@page contentType="text/html; charset=iso-8859-1" session="true" language="java" import="java.text.*" import="java.util.*" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.epsl.peritos.backend.Admin" %>
<%@ page import="com.googlecode.objectify.ObjectifyService" %>

<%
String formtype = request.getParameter("type");

if(formtype.equals("login")){
	String fuser = request.getParameter("username");
	String fpass = request.getParameter("password");

	//Comprobar en la base de datos si la pareja user-pass existe e iniciar sesión
	List<Admin> usrs = ObjectifyService.ofy().load().type(Admin.class).list();
    for (Admin usr : usrs) {
        String r;
        String user = usr.getUser();
        String pass = usr.getPass();

        //Si obtenemos un objetod con usuario y contraseña que coinciden se procede a iniciar sesión
        if (fuser.equals(user)==true && fpass.equals(pass)==true) {

                //Variable de sesión de usuario
            	HttpSession objSesion = request.getSession(true);
                objSesion.setAttribute("user", user);
                objSesion.setAttribute("log", "true");
                %>
                    <form id="frmadmn" action="admin.jsp" method="post" >
                        <input type="hidden" name="aut" value="true" />
                    </form>
                    <script type="text/javascript">
                        document.getElementById('frmadmn').submit(); // SUBMIT FORM
                    </script>
                <%
            return;
        }
    }

    //Variable de sesión de usuario
    HttpSession objSesion = request.getSession(true);
    objSesion.setAttribute("log", "false");
    %>
    <form id="index" action="index.html" method="post" >
            <input type="hidden" name="r" value="authfail"/>
    </form>
    <script type="text/javascript">
           document.getElementById('index').submit(); // SUBMIT FORM
    </script>
    <%


}

if(formtype.equals("newpass")){
	
}

if(formtype.equals("register")){
	
}

%>