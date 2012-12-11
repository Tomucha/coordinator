<%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="can" uri="/WEB-INF/permissions.tld" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags"

%><h2><s:message code="header.userList"/></h2>

<div class="buttonPanel">
    <c:if test="${can:create('userEntity')}">
        <a href="${root}/admin/user/edit"><s:message code="button.createUser"/></a>
    </c:if>
</div>

<div class="eventListTable">
    <table>
        <c:forEach items="${userResult.result}" var="user">
            <tr>
                <td><c:out value="${user.fullName}"/></td>
                <td><c:out value="${user.email}"/></td>
                <td><c:out value="${user.fullAddress}"/></td>
                <td>
                    <c:if test="${can:create('userEntity')}">
                        <a href="${root}/admin/user/edit?id=<c:out value="${user.id}"/>"><s:message code="button.edit"/></a>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
    </table>

    <div>
        <a href="${root}/admin/user/list?bookmark=${userResult.bookmark}">&gt;&gt;</a>
    </div>
</div>