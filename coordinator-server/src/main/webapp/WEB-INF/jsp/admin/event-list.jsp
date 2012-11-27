<%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
        taglib prefix="sf" uri="http://www.springframework.org/tags/form" %><%@
        taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %><%@
        taglib prefix="can" uri="/WEB-INF/permissions.tld" %><%@
        taglib prefix="tags" tagdir="/WEB-INF/tags"
%>
<h2><s:message code="header.eventList"/></h2>

<div class="mainPanel">
    <div class="buttonPanel">
        <c:choose>
            <c:when test="${can:create('eventEntity')}">
                <a href="<s:url value="/admin/event/edit"/>"><s:message code="button.createEvent"/></a>
            </c:when>
            <c:when test="${can:hasRole('ADMIN')}">
                <a href="<s:url value="/admin/event/detail"/>"><s:message code="button.registerToEvent"/></a>
            </c:when>
        </c:choose>
    </div>

    <c:choose>
        <c:when test="${!empty events}">
            <div class="eventListTable">
                <table>
                    <thead>
                    <tr>
                        <th><s:message code="label.name"/></th>
                        <th><s:message code="label.locality"/></th>
                        <th><s:message code="label.radius"/></th>
                        <th><s:message code="label.action"/></th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${events}" var="event">
                        <tr>
                            <td>
                                <c:out value="${event.name}"/>
                            </td>
                            <td>
                                <c:if test="${!empty event.eventLocationEntityList}">
                                    <c:forEach items="${event.eventLocationEntityList}" var="location">
                                        <div><tags:gps longitude="${location.longitude}" latitude="${location.latitude}"/></div>
                                    </c:forEach>
                                </c:if>
                            </td>
                            <td>
                                <c:if test="${!empty event.eventLocationEntityList}">
                                    <c:forEach items="${event.eventLocationEntityList}" var="location">
                                        <div><c:out value="${location.radius}"/> km</div>
                                    </c:forEach>
                                </c:if>
                            </td>
                            <td>
                                <c:if test="${can:create('organizationInEventEntity')}">
                                    <a href="/admin/event/detail?id=<c:out value="${event.id}"/>"><s:message code="button.detail"/></a>
                                </c:if>
                                <c:if test="${can:create('eventEntity')}">
                                    <a href="/admin/event/edit?id=<c:out value="${event.id}"/>"><s:message code="button.edit"/></a>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>

        </c:when>
        <c:otherwise>
            <p><s:message code="msg.noEventsFound"/></p>
        </c:otherwise>

    </c:choose>
</div>
