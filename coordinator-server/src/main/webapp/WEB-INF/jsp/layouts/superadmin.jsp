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
            <div class="activityPanel well sidebar-nav">
                <h2><s:message code="title.currentEvents"/></h2>
                <c:choose>
                    <c:when test="${empty currentEvents}">
                        <p><s:message code="msg.noCurrentEvents"/></p>
                    </c:when>
                    <c:otherwise>
                        <ul>
                            <c:forEach items="${currentEvents}" var="event">
                                <li><a href="${root}/admin/event/poi/list?eventId=${event.id}"><c:out value="${event.name}"/></a><br/>
                                   <c:out value="${event.description}"/></li>
                            </c:forEach>
                         </ul>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
        <!--/span-->

        <div class="span9">
            <ul class="nav nav-pills">
                <tags:breadcrumb url="/superadmin/user/list" labelCode="breadcrumb.userList" visible="${can:read('userEntity')}"/>
                <tags:breadcrumb url="/superadmin/event/list" labelCode="breadcrumb.eventList" visible="${can:read('eventEntity')}"/>
                <tags:breadcrumb url="/superadmin/mail" labelCode="breadcrumb.mail" visible="${can:read('userEntity')}"/>
            </ul>

            <div class="tabContent">

                <c:if test="${not empty globalMessage}">
                    <p class="well"><span class="icon-warning-sign"></span><c:out value="${globalMessage}"/></p>
                </c:if>

                <tiles:insertAttribute name="content"/>
            </div>

        </div>
    </div>
</div>
</body>
</html>
