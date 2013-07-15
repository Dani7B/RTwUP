<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="storage.URLMap, view.SortedHashMap, java.util.Map.Entry, java.util.Map, java.util.Set"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>RTwUP - Realtime Twitter Url Popularity</title>
	<meta name="author" content="Daniele Morgantini"/> 
	<link rel="stylesheet" type="text/css" href="elenchi.css"
</head>
<body>
<h1>RTwUP - Realtime Twitter Url Popularity</h1>
<% response.setIntHeader("Refresh", 10);
	Set<Entry<String, Map<String, Integer>>> entries = URLMap.getInstance().entrySet();%>
	<ul>
	   <% for(Entry<String, Map<String, Integer>> domain : entries) { %>
	   <h3><%= domain.getKey() %></h3>
	   <!-- <% SortedHashMap ranking = new SortedHashMap(domain.getValue());%>
	   <li>
       	<%=  ranking.toString() %>
        </li> -->
    <% } %>
    </ul>
	<div> 
	<strong>Refresh</strong> </div>
</body>
</html>