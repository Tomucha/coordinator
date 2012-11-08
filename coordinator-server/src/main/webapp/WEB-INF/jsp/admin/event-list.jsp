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
            </tr>
        </thead>
        <tbody>
            <c:forEach items="${model.events}" var="event">
                <tr>
                    <td><c:out value="${event.name}"/></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>

<div class="mapPanel">
    map
</div>