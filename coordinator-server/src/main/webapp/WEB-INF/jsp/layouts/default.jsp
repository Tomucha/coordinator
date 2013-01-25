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

    <jsp:include page="inc_head.jsp"/>

    <body class="<tiles:getAsString name="extraClass"/>">
    
        <tags:navbar inverse="true">
            <tags:loggedNavbar/>
        </tags:navbar>


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
