<%@
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
    taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
    taglib prefix="sf" uri="http://www.springframework.org/tags/form" %><%@
    taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %><%@
    taglib prefix="can" uri="/WEB-INF/permissions.tld" %><%@
    taglib prefix="tags" tagdir="/WEB-INF/tags"
%>
<h2><s:message code="header.userList"/></h2>

<div class="mainPanel">
    <div class="buttonPanel">
        <c:choose>
            <c:when test="${can:hasRole('BACKEND')}">
                <a href="<s:url value="/admin/event/user/edit?eventId=${params.eventId}"/>"><s:message code="button.addNew"/></a>
            </c:when>
        </c:choose>
    </div>

    <c:choose>
        <c:when test="${!empty userInEvents}">
            <div class="eventListTable">
                <table>
                    <thead>
                    <tr>
                        <th><s:message code="label.name"/></th>
                        <th><s:message code="label.phone"/></th>
                        <th><s:message code="label.status"/></th>
                        <th><s:message code="label.address"/></th>
                        <th><s:message code="label.action"/></th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${userInEvents}" var="userInEvent">
                        <tr>
                            <td><c:out value="${userInEvent.userEntity.fullName}"/></td>
                            <td><c:out value="${userInEvent.userEntity.phone}"/></td>
                            <td><c:out value="${userInEvent.status}"/></td>
                            <td><c:out value="${userInEvent.userEntity.fullAddress}"/></td>
                            <td>
                                <a href="<s:url value="${root}/admin/event/user/edit?eventId=${params.eventId}&userId=${userInEvent.userId}"/>"><s:message code="button.detail"/></a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>

        </c:when>
        <c:otherwise>
            <p><s:message code="msg.noUsersFound"/></p>
        </c:otherwise>

    </c:choose>
</div>
