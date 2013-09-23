<%@
        attribute name="activity" type="cz.clovekvtisni.coordinator.server.domain.ActivityEntity" %><%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
        taglib prefix="www" uri="/WEB-INF/www.tld" %><%@
        taglib prefix="tags" tagdir="/WEB-INF/tags"
        %>

<img src="${root}${activity.poiEntity.poiCategory.icon}" style="width:1em;"/>
<a href="<s:url value="${root}/admin/event/poi/edit?eventId=${activity.poiEntity.eventId}&poiId=${activity.poiEntity.id}"/>"><c:out value="${activity.poiEntity.name}"/></a>
<tags:poiStatusIcon poi="${activity.poiEntity}"/>

<s:message code="activity.${activity.type}"/>

<c:if test="${activity.type eq 'WORKFLOW_TRANSITION'}">
to "${activity.poiEntity.workflow.stateMap[activity.params[1]].name}"
</c:if>

<a href="<s:url value="${root}/admin/event/user/edit?eventId=${activity.poiEntity.eventId}&userId=${activity.userEntity.id}"/>"><c:out value="${activity.userEntity.fullName}"/></a><br/>
<small class="pull-right light">
    <c:if test="${not empty activity.comment}">
        <i><c:out value="${activity.comment}"/></i><br/>
    </c:if>
    <fmt:formatDate type="both" value="${activity.changeDate}" dateStyle="short" timeStyle="short"/>
    <s:message code="activity.author"/>
    <a href="<s:url value="${root}/admin/event/user/edit?eventId=${activity.poiEntity.eventId}&userId=${activity.changedByEntity.id}"/>"><c:out value="${activity.changedByEntity.fullName}"/></a>
</small>
<br style="clear: both;"/>
