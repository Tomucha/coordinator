<%--@elvariable id="rootBreadcrumb" type="cz.clovekvtisni.coordinator.server.web.util.Breadcrumb"--%>
<%--@elvariable id="loggedUser" type="cz.clovekvtisni.coordinator.server.domain.UserEntity"--%>
<%--@elvariable id="config" type="cz.clovekvtisni.coordinator.server.domain.CoordinatorConfig"--%>
<%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="app" uri="/WEB-INF/www.tld" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
        taglib prefix="sf" uri="http://www.springframework.org/tags/form"
%>

<a href="${root}${rootBreadcrumb.linkUrl}" class="brand"><s:message code="application.name" />
    <c:if test="${!empty event}"> - <c:out value="${event.name}"/></c:if>
    <c:if test="${!empty organization}"> - <c:out value="${organization.name}"/></c:if>
</a>

<div class="nav-collapse collapse">
    <p class="navbar-text pull-right">
        <c:if test="${!empty loggedUser}">
            <c:out value="${loggedUser.email}" />
            <a href="<s:url value="/logout"/>"><s:message code="logout"/></a>,
            <a href="<s:url value="/superadmin"/>"><s:message code="superadmin"/></a>
        </c:if>
    </p>
</div><!--/.nav-collapse -->
