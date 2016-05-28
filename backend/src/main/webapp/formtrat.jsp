<%@page contentType="text/html; charset=iso-8859-1" session="true" language="java" import="java.text.*" import="java.util.*" %>

<%
String nom = request.getParameter("nombre");
String t1="N";
String t2="N";
String t3="N";
String t4="N";
String t5="N";
String t6="N";
String t7="N";
String t8="N";
String t9="N";
String t10="N";


String in1 = request.getParameter("in1");
String ti1 = request.getParameter("ti1");
String ni1 = request.getParameter("ni1");
String pi1 = request.getParameter("pi1");

if(in1!=null){
    t1 = in1+"_"+ti1+"_"+ni1+"_"+pi1;
}

String in2 = request.getParameter("in2");
String ti2 = request.getParameter("ti2");
String ni2 = request.getParameter("ni2");
String pi2 = request.getParameter("pi2");

if(in2!=null){
    t2 = in2+"_"+ti2+"_"+ni2+"_"+pi2;
}

String in3 = request.getParameter("in3");
String ti3 = request.getParameter("ti3");
String ni3 = request.getParameter("ni3");
String pi3 = request.getParameter("pi3");

if(in3!=null){
    t3 = in3+"_"+ti3+"_"+ni3+"_"+pi3;
}

String in4 = request.getParameter("in4");
String ti4 = request.getParameter("ti4");
String ni4 = request.getParameter("ni4");
String pi4 = request.getParameter("pi4");

if(in4!=null){
    t4 = in4+"_"+ti4+"_"+ni4+"_"+pi4;
}

String in5 = request.getParameter("in5");
String ti5 = request.getParameter("ti5");
String ni5 = request.getParameter("ni5");
String pi5 = request.getParameter("pi5");

if(in5!=null){
    t5 = in5+"_"+ti5+"_"+ni5+"_"+pi5;
}

String pa1 = request.getParameter("pa1");
String np1 = request.getParameter("np1");
String tt1 = request.getParameter("tt1");

if(pa1!=null){
    t6 = pa1+"_0_"+np1+"_"+tt1;
}

String pa2 = request.getParameter("pa2");
String np2 = request.getParameter("np2");
String tt2 = request.getParameter("tt2");

if(pa2!=null){
    t7 = pa2+"_0_"+np2+"_"+tt2;
}

String pa3 = request.getParameter("pa3");
String np3 = request.getParameter("np3");
String tt3 = request.getParameter("tt3");

if(pa3!=null){
    t8 = pa3+"_0_"+np3+"_"+tt3;
}

String pa4 = request.getParameter("pa4");
String np4 = request.getParameter("np4");
String tt4 = request.getParameter("tt4");

if(pa4!=null){
    t9 = pa4+"_0_"+np4+"_"+tt4;
}

String pa5 = request.getParameter("pa5");
String np5 = request.getParameter("np5");
String tt5 = request.getParameter("tt5");

if(pa5!=null){
    t10 = pa5+"_0_"+np5+"_"+tt5;
}


String data = "";
String text = "";

if(!t1.equals("N")){
    data = data+t1+"-";
    text = text+"<p>"+in1+": "+ni1+" inhalaciones cada "+pi1+" horas"+"</p><br>";
}
if(!t2.equals("N")){
    data = data+t2+"-";
    text = text+"<p>"+in2+": "+ni2+" inhalaciones cada "+pi2+" horas"+"</p><br>";
}
if(!t3.equals("N")){
    data = data+t3+"-";
    text = text+"<p>"+in3+": "+ni3+" inhalaciones cada "+pi3+" horas"+"</p><br>";
}
if(!t4.equals("N")){
    data = data+t4+"-";
    text = text+"<p>"+in4+": "+ni4+" inhalaciones cada "+pi4+" horas"+"</p><br>";
}
if(!t5.equals("N")){
    data = data+t5+"-";
    text = text+"<p>"+in5+": "+ni5+" inhalaciones cada "+pi5+" horas"+"</p><br>";
}
if(!t6.equals("N")){
    data = data+t6+"-";
    text = text+"<p>"+pa1+": "+np1+" pastillas cada "+tt1+" horas"+"</p><br>";
}
if(!t7.equals("N")){
    data = data+t7+"-";
    text = text+"<p>"+pa2+": "+np2+" pastillas cada "+tt2+" horas"+"</p><br>";
}
if(!t8.equals("N")){
    data = data+t8+"-";
    text = text+"<p>"+pa3+": "+np3+" pastillas cada "+tt3+" horas"+"</p><br>";
}
if(!t9.equals("N")){
    data = data+t9+"-";
    text = text+"<p>"+pa4+": "+np4+" pastillas cada "+tt4+" horas"+"</p><br>";
}
if(!t10.equals("N")){
    data = data+t10+"-";
    text = text+"<p>"+pa5+": "+np5+" pastillas cada "+tt5+" horas"+"</p><br>";
}

//String data=in1+"_"+ti1+"_"+ni1+"_"+pi1+"-"+in2+"_"+ti2+"_"+ni2+"_"+pi2;
String datos = data.substring(0, data.length()-1);

String url = "http://zxing.org/w/chart?cht=qr&chs=350x350&chld=L&choe=UTF-8&chl="+datos;

String htm = "<h2>Tratamiento</h2><h3>"+nom+"</h3><br>"+text+"<br><img src='"+url+"'/>";

out.print(url+"#"+htm);


%>