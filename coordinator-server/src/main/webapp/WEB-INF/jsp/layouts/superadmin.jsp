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
<%--@elvariable id="event" type="cz.clovekvtisni.coordinator.server.domain.EventEntity"--%>

<jsp:include page="inc_head.jsp"/>

<body class="<tiles:getAsString name="extraClass"/>">

<tags:navbar inverse="false">
    <tags:loggedNavbar/>
</tags:navbar>

<div class="container-fluid">
    <div class="row-fluid">

        <div class="span3">
            <div class="well">
                <h2><s:message code="title.currentEvents"/></h2>
                <c:choose>
                    <c:when test="${empty currentEvents}">
                        <p><s:message code="msg.noCurrentEvents"/></p>
                    </c:when>
                    <c:otherwise>
                        <div class="btn-group btn-group-vertical">
                            <c:forEach items="${currentEvents}" var="event">
                                <button class="btn"><c:out value="${event.name}"/></button>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
        <!--/span-->

        <div class="span9">
            <ul class="nav nav-pills">
                <c:if test="${!empty breadcrumbs}">
                    <c:forEach items="${breadcrumbs}" var="breadcrumb">
                        <c:if test="${can:viewBreadcrumb(breadcrumb)}">
                            <li class="${requestScope['javax.servlet.forward.request_uri'] == (fn:indexOf(breadcrumb.linkUrl, '?') == -1 ? breadcrumb.linkUrl : fn:substring(breadcrumb.linkUrl, 0, fn:indexOf(breadcrumb.linkUrl, '?'))) ? 'active' : ''}">
                                <a href="<s:url value="${breadcrumb.linkUrl}"/>"><s:message
                                        code="${breadcrumb.labelCode}"/></a></li>
                        </c:if>
                    </c:forEach>
                </c:if>
            </ul>

            <div class="tabContent">
                <tiles:insertAttribute name="content"/>
            </div>

        </div>
    </div>
</div>
</body>
</html>
