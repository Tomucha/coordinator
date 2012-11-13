<%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags"
%>
<h2><s:message code="header.eventList"/></h2>

<div class="buttonPanel">
    <a href=""><s:message code="button.createUser"/></a>
</div>

<div class="eventListTable">
    <table>
        <c:forEach items="${userResult.result}" var="user">
            <tr>
                <td><c:out value="${user.email}"/></td>
            </tr>
        </c:forEach>
    </table>
</div>