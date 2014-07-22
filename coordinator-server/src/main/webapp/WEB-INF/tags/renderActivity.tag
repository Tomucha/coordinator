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

<%--@elvariable id="config" type="cz.clovekvtisni.coordinator.server.domain.CoordinatorConfig"--%>
<c:if test="${activity.type eq 'WORKFLOW_TRANSITION'}">
&#8594; "${config.workflowMap[activity.workflowId].stateMap[activity.workflowStateId].name}@${config.workflowMap[activity.workflowId].name}"
</c:if>

<c:if test="${activity.type eq 'WORKFLOW_START'}">
&#8594; "${config.workflowMap[activity.workflowId].stateMap[activity.workflowStateId].name}@${config.workflowMap[activity.workflowId].name}"
</c:if>

<a href="<s:url value="${root}/admin/event/user/edit?eventId=${activity.poiEntity.eventId}&userId=${activity.userEntity.id}"/>"><c:out value="${activity.userEntity.fullName}"/></a><br/>
<small class="pull-right light">
    <c:if test="${not empty activity.comment}">
        <i><c:out value="${activity.comment}"/></i><br/>
    </c:if>
    <fmt:formatDate value="${activity.changeDate}" pattern="d.M.yy H:mm" timeZone="CET"/>
    <s:message code="activity.author"/>
    <a href="<s:url value="${root}/admin/event/user/edit?eventId=${activity.poiEntity.eventId}&userId=${activity.changedByEntity.id}"/>"><c:out value="${activity.changedByEntity.fullName}"/></a>
</small>
<br style="clear: both;"/>
