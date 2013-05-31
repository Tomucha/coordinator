<%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@
        taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@
        taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@
        taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@
        taglib prefix="can" uri="/WEB-INF/permissions.tld" %>
<%@
        taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@
        taglib prefix="tags" tagdir="/WEB-INF/tags"
        %>
<h2><s:message code="header.userGroupList"/></h2>

<%--@elvariable id="config" type="cz.clovekvtisni.coordinator.server.domain.CoordinatorConfig"--%>

<div class="mainPanel">
    <div class="buttonPanel btn-toolbar">
        <div class="btn-group">
            <c:choose>
                <c:when test="${can:hasRole('BACKEND')}">
                    <a class="btn" href="<s:url value="/admin/event/user-group/edit?eventId=${params.eventId}&retUrl=/admin/event/user-group/list"/>"><i class="icon-plus"></i> <s:message
                            code="button.addNewUserGroup"/></a>
                </c:when>
            </c:choose>
        </div>
    </div>

    <c:choose>
        <c:when test="${!empty userGroups}">
            <div>
                <table class="table table-striped">
                    <thead>
                    <tr>
                        <th><s:message code="UserGroupEntity.name"/></th>
                        <th><s:message code="UserGroupEntity.role"/></th>
                        <th><s:message code="label.action"/></th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${userGroups}" var="userGroup" varStatus="i">
                        <tr>
                            <td><c:out value="${userGroup.name}"/></td>
                            <td><span class="label"><c:out value="${config.roleMap[userGroup.roleId].name}"/></span></td>
                            <td>
                                <a class="btn"
                                   href="<s:url value="${root}/admin/event/user-group/edit?eventId=${params.eventId}&groupId=${userGroup.id}&retUrl=/admin/event/user-group/list"/>"><span class=" icon-pencil"></span> <s:message
                                        code="button.edit"/></a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>

        </c:when>
        <c:otherwise>
            <p><s:message code="msg.noUserGroupsFound"/></p>
        </c:otherwise>

    </c:choose>
</div>
