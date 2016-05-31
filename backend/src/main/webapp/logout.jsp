<%@page contentType="text/html; charset=iso-8859-1" session="true" language="java" import="java.text.*" import="java.util.*" %>

<%
HttpSession objSesion = request.getSession(true);
objSesion.setAttribute("log", null);
objSesion.setAttribute("user", null);
%>
<form id="frmadmn" action="login.html" method="post" />
<script type="text/javascript">
    document.getElementById('frmadmn').submit(); // SUBMIT FORM
</script>