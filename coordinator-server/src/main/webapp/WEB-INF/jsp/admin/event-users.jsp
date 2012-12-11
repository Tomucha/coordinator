<%@
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
    taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
    taglib prefix="sf" uri="http://www.springframework.org/tags/form" %><%@
    taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %><%@
    taglib prefix="can" uri="/WEB-INF/permissions.tld" %><%@
    taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %><%@
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
            <sf:form action="${root}/admin/event/users" method="post" modelAttribute="selectionForm">
                <div class="eventListTable">
                    <sf:hidden path="eventId"/>

                    <table>
                        <thead>
                        <tr>
                            <th></th>
                            <th><s:message code="label.name"/></th>
                            <th><s:message code="label.phone"/></th>
                            <th><s:message code="label.status"/></th>
                            <th><s:message code="label.address"/></th>
                            <th><s:message code="label.action"/></th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${userInEvents}" var="userInEvent" begin="0" step="1" varStatus="i">
                            <tr>
                                <td><input type="checkbox" name="selectedUsers[${i.index}]" value="${userInEvent.userId}"/></td>
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

                <div>
                    <sf:select path="selectedAction" onchange="$('#selectedTaskIdSelect').toggle(this.value=='registerToTask')">
                        <sf:option value="delete"><s:message code="label.delete"/></sf:option>
                        <sf:option value="suspend"><s:message code="label.suspend"/></sf:option>
                        <sf:option value="registerToTask"><s:message code="label.registerToTask"/></sf:option>
                    </sf:select>

                    <c:choose>
                        <c:when test="${empty tasks or fn:length(tasks) == 0}">
                            <span id="selectedTaskIdSelect" style="display: none"><s:message code="msg.noTasksFound"/></span>
                        </c:when>
                        <c:otherwise>
                            <sf:select path="selectedTaskId" id="selectedTaskIdSelect" cssStyle="display: none">
                                <c:forEach items="${tasks}" var="task">
                                    <sf:option value="${task.id}"><c:out value="${task.poiCategory.name}"/> - <c:out value="${task.createdDate}"/></sf:option>
                                </c:forEach>
                            </sf:select>
                        </c:otherwise>
                    </c:choose>

                    <button type="submit"><s:message code="button.submit"/></button>
                </div>
            </sf:form>

        </c:when>
        <c:otherwise>
            <p><s:message code="msg.noUsersFound"/></p>
        </c:otherwise>

    </c:choose>
</div>
