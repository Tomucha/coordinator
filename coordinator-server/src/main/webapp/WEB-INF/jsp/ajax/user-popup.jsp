<%--@elvariable id="userInEvent" type="cz.clovekvtisni.coordinator.server.domain.UserInEventEntity"--%>
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
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<h4><img src="${root}/images/icons/male-2.png" style="width: 1.2em;"/><c:out
        value="${userInEvent.userEntity.fullName}"/></h4>

<c:if test="${!empty userInEvent.lastPoiEntity}">
    <p>
        <a href="<s:url value="${root}/admin/event/poi/edit?eventId=${userInEvent.lastPoiEntity.eventId}&poiId=${userInEvent.lastPoiEntity.id}"/>"><c:out
                value="${userInEvent.lastPoiEntity.name}"/></a>
        <tags:poiStatusIcon poi="${userInEvent.lastPoiEntity}"/>
        <br/>
        <small>
            <fmt:formatDate type="both" value="${userInEvent.lastPoiDate}" dateStyle="short" timeStyle="short"/>
        </small>
    </p>
</c:if>


<c:if test="${!empty userInEvent.roles}">
    <p>
        <c:forEach items="${userInEvent.roles}" var="role">
            <span class="label"><c:out value="${config.roleMap[role].name}"/></span>
        </c:forEach>
    </p>
</c:if>
<c:if test="${!empty userInEvent.groupEntities}">
    <p>
    <c:forEach items="${userInEvent.groupEntities}" var="group">
        <span class="label"><c:out value="${group.name}"/></span>
    </c:forEach>
    </p>
</c:if>


<div class="btn-group">
    <a class="btn"
       href="<s:url value="${root}/admin/event/user/edit?userId=${userInEvent.userId}&eventId=${event.id}"/>"><s:message
            code="button.edit"/></a>
</div>
