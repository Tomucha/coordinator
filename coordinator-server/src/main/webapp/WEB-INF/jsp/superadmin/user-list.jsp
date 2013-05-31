<%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="can" uri="/WEB-INF/permissions.tld" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags"

%>

<%--@elvariable id="config" type="cz.clovekvtisni.coordinator.server.domain.CoordinatorConfig"--%>
<%--@elvariable id="user" type="cz.clovekvtisni.coordinator.server.domain.UserEntity"--%>

<h2><s:message code="header.userList"/></h2>

<div class="buttonPanel btn-toolbar">
    <div class="btn-group">
        <c:if test="${can:create('userEntity')}">
            <a class="btn" href="${root}/superadmin/user/edit"><i class="icon-plus"></i> <i class="icon-user"></i> <s:message code="button.createUser"/></a>
        </c:if>
    </div>
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
            <tr>
                <th>${config.organizationMap[user.organizationId].name}</th>
                <th><c:out value="${user.fullName}"/></th>
                <td><c:out value="${user.email}"/></td>
                <td><c:out value="${user.fullAddress}"/></td>
                <td><c:forEach items="${user.roleIdList}" var="roleId">
                    <span class="label label-info">${config.roleMap[roleId].name}</span>
                </c:forEach>
                </td>
                <td><c:forEach items="${user.equipmentEntityList}" var="equip">
                    <span class="label label-success">${config.equipmentMap[equip.equipmentId].name}</span>
                </c:forEach></td>
                <td><c:forEach items="${user.skillEntityList}" var="skill">
                    <span class="label label-important">${config.skillMap[skill.skillId].name}</span>
                </c:forEach></td>
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