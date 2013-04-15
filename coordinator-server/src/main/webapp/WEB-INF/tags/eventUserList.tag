<%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@
        taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@     taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ attribute name="user" type="cz.clovekvtisni.coordinator.server.domain.UserInEventEntity" %>
<%@ attribute name="renderHeader" type="java.lang.Boolean" %>

<%--

Tag renders user list header or a line with detail.

--%>

<c:choose>
    <c:when test="${renderHeader}">
        <th><s:message code="label.name"/></th>
        <th><s:message code="label.phone"/></th>
        <th><s:message code="label.lastTask"/></th>
        <th><s:message code="label.roles"/></th>
        <th><s:message code="label.userGroups"/></th>
    </c:when>
    <c:otherwise>
        <td><img src="${root}/images/icons/male-2.png" class="pull-left"/><strong><c:out value="${user.userEntity.fullName}"/></strong><br/>
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
        <td><c:out value="${user.userEntity.phone}"/></td>
        <td>
            <c:if test="${!empty user.lastPoiEntity}">
                <a href="<s:url value="${root}/admin/event/poi/edit?eventId=${user.lastPoiEntity.eventId}&poiId=${user.lastPoiEntity.id}"/>"><c:out value="${user.lastPoiEntity.name}"/></a>
                <tags:poiStatusIcon poi="${user.lastPoiEntity}"/>
                <br/>
                <small>
                    <fmt:formatDate type="both" value="${user.lastPoiDate}" dateStyle="short" timeStyle="short"/>
                </small>
            </c:if>
        </td>
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
