<%@ page contentType="text/html; charset=UTF-8" language="java" %><%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
        taglib prefix="sf" uri="http://www.springframework.org/tags/form" %><%@
        taglib prefix="can" uri="/WEB-INF/permissions.tld" %><%@
        taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %><%@
        taglib prefix="tags" tagdir="/WEB-INF/tags"
%><!DOCTYPE html>
<html lang="en">
<%--@elvariable id="loggedUser" type="cz.clovekvtisni.coordinator.server.domain.UserEntity"--%>
<%--@elvariable id="root" type="java.lang.String"--%>
    <head>
        <meta http-equiv="content-type" content="text/html; charset=utf-8" />
        <title>Coordinator<c:if test="${!empty event}"> - <c:out value="${event.name}"/></c:if></title>

        <link type="text/css" href="${root}/css/coordinator.css" rel="stylesheet"/>
        <link href="${root}/bootstrap/css/bootstrap.min.css" rel="stylesheet" media="screen">

        <!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
        <!--[if lt IE 9]>
        <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
        <![endif]-->

        <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js"></script>
        <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.9.1/jquery-ui.min.js"></script>
        <script src="${root}/bootstrap/js/bootstrap.min.js"></script>
    </head>
    <body class="<tiles:getAsString name="extraClass"/>">

        <div class="navbar navbar-inverse navbar-fixed-top">
            <div class="navbar-inner">
                <div class="container-fluid">
                    <a href="${root}/" class="brand">Coordinator - Člověk v tísni<c:if test="${!empty event}"> - <c:out value="${event.name}"/></c:if></a>
                    <div class="nav-collapse collapse">
                        <p class="navbar-text pull-right">
                            <c:if test="${!empty loggedUser}">
                                <c:out value="${loggedUser.email}" /> <a href="<s:url value="/logout"/>">(<s:message code="logout"/>)</a>
                            </c:if>
                        </p>
                    </div><!--/.nav-collapse -->
                </div>
            </div>
        </div>

        <div class="container-fluid">
            <div class="row-fluid">

                <div class="span3">
                    <div class="well sidebar-nav">
                        <h2><s:message code="title.activityFeed"/></h2>
                        <div class="activityList">
                            <table class="table">
                                <c:forEach items="${lastPoiList}" var="poi">
                                    <tr>
                                        <td><c:out value="${poi.poiCategory.name}"/></td>
                                        <td><tags:gps longitude="${poi.longitude}" latitude="${poi.latitude}"/></td>
                                        <td><tags:humanago time="${poi.createdDate}" now="${now}"/></td>
                                    </tr>
                                </c:forEach>
                            </table>
                        </div>
                    </div><!--/.well -->
                </div><!--/span-->

                <div class="span9">
                    <ul class="nav nav-pills">
                        <c:if test="${!empty breadcrumbs}">
                            <c:forEach items="${breadcrumbs}" var="breadcrumb">
                                <c:if test="${can:viewBreadcrumb(breadcrumb)}">
                                    <li class="${requestScope['javax.servlet.forward.request_uri'] == (fn:indexOf(breadcrumb.linkUrl, '?') == -1 ? breadcrumb.linkUrl : fn:substring(breadcrumb.linkUrl, 0, fn:indexOf(breadcrumb.linkUrl, '?'))) ? 'active' : ''}"><a href="<s:url value="${breadcrumb.linkUrl}"/>"><s:message code="${breadcrumb.labelCode}"/></a></li>
                                </c:if>
                            </c:forEach>
                        </c:if>
                    </ul>

                    <div class="tabContent">
                        <tiles:insertAttribute name="content" />
                    </div>

                </div>
            </div>
        </div>
    </body>
</html>
