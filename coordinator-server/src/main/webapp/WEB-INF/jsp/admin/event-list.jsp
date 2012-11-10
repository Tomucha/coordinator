<%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
        taglib prefix="sf" uri="http://www.springframework.org/tags/form" %><%@
        taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %><%@
        taglib prefix="tags" tagdir="/WEB-INF/tags"
%>
<h2><s:message code="header.eventList"/></h2>

<div class="buttonPanel">
    <a href="<s:url value="/admin/event/edit"/>"><s:message code="button.createEvent"/></a>
</div>

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
                        <c:if test="${!empty event.eventLocationList}">
                            <c:forEach items="${event.eventLocationList}" var="location">
                                <div><tags:gps longitude="${location.longitude}" latitude="${location.latitude}"/></div>
                            </c:forEach>
                        </c:if>
                    </td>
                    <td>
                        <c:if test="${!empty event.eventLocationList}">
                            <c:forEach items="${event.eventLocationList}" var="location">
                                <div><c:out value="${location.radius}"/> km</div>
                            </c:forEach>
                        </c:if>
                    </td>
                    <td>
                        <a href="/admin/event/edit?eventId=<c:out value="${event.eventId}"/>"><s:message code="button.edit"/></a>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>

<div class="mapPanel">
    map
</div>