<%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@
        taglib prefix="s" uri="http://www.springframework.org/tags" %>

<%@ attribute name="user" type="cz.clovekvtisni.coordinator.server.domain.UserInEventEntity" %>
<%@ attribute name="renderHeader" type="java.lang.Boolean" %>

<%--

Tag renders user list header or a line with detail.

--%>

<c:choose>
    <c:when test="${renderHeader}">
        <th><s:message code="label.name"/></th>
        <th><s:message code="label.phone"/></th>
        <th><s:message code="label.status"/></th>
        <th><s:message code="label.address"/></th>
        <th><s:message code="label.roles"/></th>
        <th><s:message code="label.userGroups"/></th>
    </c:when>
    <c:otherwise>
        <th><c:out value="${user.userEntity.fullName}"/></th>
        <td><c:out value="${user.userEntity.phone}"/></td>
        <td>
            <c:choose>
                <c:when test="${user.userEntity.suspended}">
                                            <span class="alert alert-error"
                                                  title="<c:out value="${user.userEntity.reasonSuspended}"/>"><s:message
                                                    code="label.suspended"/></span>
                </c:when>
                <c:when test="${!empty user.status}">
                    <s:message code="RegistrationStatus.${user.status}"/>
                </c:when>
            </c:choose>
        </td>
        <td><c:out value="${user.userEntity.fullAddress}"/></td>
        <td>
            <c:if test="${!empty user.roles}">
                <c:forEach items="${user.roles}" var="role">
                    <span class="label"><c:out value="${config.roleMap[role].name}"/></span>
                </c:forEach>
            </c:if>
        </td>
        <td>
            <c:if test="${!empty user.groupEntities}">
                <c:forEach items="${user.groupEntities}" var="group">
                    <span class="label"><c:out value="${group.name}"/></span>
                </c:forEach>
            </c:if>
        </td>
    </c:otherwise>
</c:choose>
