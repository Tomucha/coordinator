<%--@elvariable id="rootBreadcrumb" type="cz.clovekvtisni.coordinator.server.web.util.Breadcrumb"--%>
<%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="app" uri="/WEB-INF/www.tld" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
        taglib prefix="sf" uri="http://www.springframework.org/tags/form"
%>

<a href="${root}${rootBreadcrumb.linkUrl}<c:if test="${!empty event}">?eventId=<c:out value="${event.id}"/></c:if>" class="brand"><s:message code="application.name"/><c:if test="${!empty event}"> - <c:out value="${event.name}"/></c:if></a>

<div class="nav-collapse collapse">
    <p class="navbar-text pull-right">
        <c:if test="${!empty loggedUser}">
            <c:out value="${loggedUser.email}" /> <a href="<s:url value="/logout"/>">(<s:message code="logout"/>)</a>
        </c:if>
    </p>
</div><!--/.nav-collapse -->
