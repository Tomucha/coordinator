<%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="can" uri="/WEB-INF/permissions.tld" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags"

%>

<%--@elvariable id="config" type="cz.clovekvtisni.coordinator.server.domain.CoordinatorConfig"--%>


<h2><s:message code="header.userList"/></h2>

<div class="buttonPanel">
    <c:if test="${can:create('userEntity')}">
        <a class="btn" href="${root}/superadmin/user/edit"><s:message code="button.createUser"/></a>
    </c:if>
</div>

<div class="eventListTable">
    <table class="table table-striped">
        <tr>
            <th><s:message code="UserEntity.organization"/></th>
            <th><s:message code="UserEntity.fullName"/></th>
            <th><s:message code="UserEntity.email"/></th>
            <th><s:message code="UserEntity.fullAddress"/></th>
            <th><s:message code="UserEntity.roles"/></th>
            <th><s:message code="header.equipmentList"/></th>
            <th><s:message code="header.skillList"/></th>
            <th></th>
        </tr>
        <c:forEach items="${userResult.result}" var="user">
            <%--@elvariable id="user" type="cz.clovekvtisni.coordinator.server.domain.UserEntity"--%>
            <tr>
                <th>${config.organizationMap[user.organizationId]}</th>
                <th><c:out value="${user.fullName}"/></th>
                <td><c:out value="${user.email}"/></td>
                <td><c:out value="${user.fullAddress}"/></td>
                <td><c:forEach items="${user.roleIdList}" var="roleId">
                    <span class="label label-info">${config.roleMap[roleId].name}</span>
                </c:forEach>
                </td>
                <td></td>
                <td></td>
                <td>
                    <c:if test="${can:create('userEntity')}">
                        <a class="btn" href="${root}/superadmin/user/edit?id=<c:out value="${user.id}"/>"><s:message code="button.edit"/></a>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
    </table>

    <c:if test="${!empty userResult.bookmark}">
        <div class="pagesNavPanel">
            <a class="btn btn-success" href="${root}/superadmin/user/list?bookmark=${userResult.bookmark}">&gt;&gt;</a>
        </div>
    </c:if>
</div>