<%@ page contentType="text/html; charset=UTF-8" language="java" %><%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
        taglib prefix="sf" uri="http://www.springframework.org/tags/form" %><%@
        taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %><%@
        taglib prefix="tags" tagdir="/WEB-INF/tags"
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="cs" lang="cs">
<%--@elvariable id="loggedUser" type="cz.clovekvtisni.coordinator.server.domain.UserEntity"--%>
<%--@elvariable id="root" type="java.lang.String"--%>
    <head>
        <meta http-equiv="content-type" content="text/html; charset=utf-8" />
        <title>Coordinator</title>
        <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js"></script>
        <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.9.1/jquery-ui.min.js"></script>
        <link type="text/css" href="${root}/css/coordinator.css" rel="stylesheet"/>
    </head>
    <body class="<tiles:getAsString name="extraClass"/>">
            <div class="pageHeader">
                <h1>Coordinator - Člověk v tísni</h1>
                <div class="controlPanel">
                    <c:if test="${!empty loggedUser}">
                        <c:out value="${loggedUser.email}" /> <a href="<s:url value="/logout"/>">(<s:message code="logout"/>)</a>
                    </c:if>
                </div>
            </div>

            <div class="pageContent">

                <div class="pageContentLeft">
                    <h2><s:message code="title.activityFeed"/></h2>
                    <div class="activityList">
                        <table>
                            <c:forEach items="${lastPoiList}" var="poi">
                                <tr>
                                    <td>
                                        <div>Popis POI.</div>
                                        <div><tags:gps longitude="${poi.longitude}" latitude="${poi.latitude}"/></div>
                                    </td>
                                    <td><tags:humanago time="${poi.createdDate}" now="${now}"/></td>
                                </tr>
                            </c:forEach>
                        </table>
                    </div>
                </div>

                <div class="pageContentMain">
                    <div class="tabPanel">
                        <c:if test="${!empty breadcrumbs}">
                            <c:forEach items="${breadcrumbs}" var="breadcrumb">
                                <a href="<s:url value="${breadcrumb.linkUrl}"/>"><s:message code="${breadcrumb.labelCode}"/></a>
                            </c:forEach>
                        </c:if>
                    </div>
                    <div class="tabContent">
                        <tiles:insertAttribute name="content" />
                    </div>
                </div>

            </div>
    </body>
</html>
