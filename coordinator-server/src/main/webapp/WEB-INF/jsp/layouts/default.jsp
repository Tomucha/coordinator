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

                <div class="span2">
                    <div class="activityPanel well sidebar-nav">
                        <h3><s:message code="title.activityFeed"/></h3>
                        <c:choose>
                            <c:when test="${not empty activity}">
                                <div class="activityList">
                                    <c:forEach items="${activity}" var="activityRow">
                                        <p><tags:renderActivity activity="${activityRow}"/></p>
                                    </c:forEach>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <i><s:message code="msg.noActivitiesInEvent"/></i>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <!--/.well -->
                </div>
                <!--/span-->

                <div class="span5">
                    <ul class="nav nav-pills event-menu">
                        <c:if test="${!empty breadcrumbs}">
                            <c:forEach items="${breadcrumbs}" var="breadcrumb">
                                <c:if test="${can:viewBreadcrumb(breadcrumb)}">
                                    <li class="${requestScope['javax.servlet.forward.request_uri'] == (fn:indexOf(breadcrumb.linkUrl, '?') == -1 ? breadcrumb.linkUrl : fn:substring(breadcrumb.linkUrl, 0, fn:indexOf(breadcrumb.linkUrl, '?'))) ? 'active' : ''}"><a href="<s:url value="${breadcrumb.linkUrl}"/>"><s:message code="${breadcrumb.labelCode}"/></a></li>
                                </c:if>
                            </c:forEach>
                        </c:if>
                    </ul>

                    <div class="tabContent">

                        <c:if test="${not empty globalMessage}">
                            <p class="well"><span class="icon-warning-sign"></span><c:out value="${globalMessage}"/></p>
                        </c:if>

                        <tiles:insertAttribute name="content" />
                    </div>

                </div>

                <c:if test="${empty disableMap or !disableMap}">
                    <div class="span5">
                        <tags:osm
                            width="95%"
                            height="600px"
                            latitude="${event.firstEventLocation.latitude}"
                            longitude="${event.firstEventLocation.longitude}"
                        />
                    </div>
                </c:if>

            </div>
        </div>
    </body>
</html>
