<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>

<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.Comparator"%>
<%@page import="com.opera.link.webapp.LinkModel"%>
<%@page import="com.opera.link.webapp.SpeedDial"%>
<%@page import="com.opera.link.webapp.LinkServlet"%>

<html>

<head>
<title>Opera Link - Speed Dial example</title>
<link rel="stylesheet" href="css/index.css" type="text/css">
</head>

<body>
<div id="wrap">
<div>
<h2>Opera Link - Speeddials</h2>

<%
out.println("<a href=\"" + LinkServlet.AUTH_SERVLET_UPDATE + "\">[Update]</a>");
%>

</div>
<div>
<ol>
	<%
		//retrieve the user's speeddials from the session
		LinkModel model = (LinkModel) session.getAttribute(LinkModel.MODEL);
		
		if (model == null) {
			// if there is no model, then we do not have the credentials that
			// we need to receive speeddial, so lets go get those first
			response.sendRedirect(LinkServlet.AUTH_SERVLET);
			return;
		} 
		
		ArrayList<SpeedDial> speeddials = model.getSpeeddials();

		if (speeddials == null) {
			// the model did not contain any speeddials, so we had better
			// try to refresh them first
			response.sendRedirect(LinkServlet.AUTH_SERVLET_UPDATE);
			return;
		}
		
		// sort the speedials
		final Comparator<SpeedDial> SD_ORDER =
                                 new Comparator<SpeedDial>() {
        	
			public int compare(SpeedDial s1, SpeedDial s2) {
        		return (s1.getId() < s2.getId() ? -1 :
                    (s1.getId() == s2.getId() ? 0 : 1));
        	}
		};
		
		Collections.sort(speeddials, SD_ORDER);
		
		// print the speeddial data
		Iterator<SpeedDial> it = speeddials.iterator();
		while (it.hasNext()) {
			SpeedDial sd = (SpeedDial) it.next();
			out.println("<li>");
			out.println("<a href=\"" + sd.getUri() + "\">");
				out.println("<img class=\"logo\" " + 
				"src=\"http://media.opera.com/media/images/icon/opera_icon.svgz\" />");	
			out.println("<div>");
			out.println(sd.getId() + " " + sd.getTitle());
			out.println("</div>");
			out.println("</a>");
			out.println("</li>");
		}
	%>
</ol>
</div>
</div>
</body>
</html>