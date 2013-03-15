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
    <c:if test="${can:create('eventEntity')}">
        <div class="buttonPanel">
            <a class="btn" href="<s:url value="/superadmin/event/edit"/>"><s:message code="button.createEvent"/></a>
        </div>
    </c:if>

    <c:choose>
        <c:when test="${!empty events}">
            <div class="eventListTable">
                <table class="table table-striped">
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
                            <th><c:out value="${event.name}"/><br/>
                                <small><c:out value="${event.description}"/></small>

                            </th>
                            <td>
                                <c:if test="${!empty event.firstEventLocation}">
                                    <c:set value="${event.firstEventLocation}" var="location"/>
                                    <div>
                                        <tags:gps longitude="${location.longitude}" latitude="${location.latitude}"/>
                                    </div>
                                </c:if>
                            </td>
                            <td>
                                <c:if test="${!empty location.radius}">
                                    <div><c:out value="${location.radius}"/> km</div>
                                </c:if>
                            </td>
                            <td>
                                <c:if test="${can:read('organizationInEventEntity')}">
                                    <a class="btn" href="/admin/event/map?eventId=<c:out value="${event.id}"/>"><s:message code="button.detail"/></a>
                                </c:if>
                                <c:if test="${can:create('eventEntity')}">
                                    <a class="btn" href="/superadmin/event/edit?eventId=<c:out value="${event.id}"/>"><s:message code="button.edit"/></a>
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
