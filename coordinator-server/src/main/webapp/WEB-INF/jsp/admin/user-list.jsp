<%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags"
%><h2><s:message code="header.userList"/></h2>

<div class="buttonPanel">
    <a href="${root}/admin/user/edit"><s:message code="button.createUser"/></a>
</div>

<div class="eventListTable">
    <table>
        <c:forEach items="${userResult.result}" var="user">
            <tr>
                <td><c:out value="${user.fullName}"/></td>
                <td><c:out value="${user.email}"/></td>
                <td><c:out value="${user.fullAddress}"/></td>
                <td>
                    <a href="${root}/admin/user/edit?userId=<c:out value="${user.id}"/>"><s:message code="button.edit"/></a>
                </td>
            </tr>
        </c:forEach>
    </table>

    <div>
        <a href="${root}/admin/user/list?bookmark=${userResult.bookmark}">&gt;&gt;</a>
    </div>
</div>