<%--@elvariable id="poi" type="cz.clovekvtisni.coordinator.server.domain.PoiEntity"--%>
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
<h4><img src="${root}${poi.poiCategory.icon}" style="width:1.2em;"/> ${poi.name} <tags:poiStatusIcon poi="${poi}"/></h4>
<p><small>${poi.description}<br/>${poi.poiCategory.name}</small></p>

<c:if test="${not empty poi.workflow}">
<p><a href="${root}/admin/event/poi/workflow?poiId=<c:out value='${poi.id}'/>&eventId=${poi.eventId}">
    <c:out value="${poi.workflowState.name}"/><br/>
    <small><c:out value="${poi.workflowState.description}"/></small>
</a></p>
</c:if>

<c:if test="${not empty assignedUsers}">
<h4><s:message code="label.assignedUsers"/></h4>
<p>
<c:forEach items="${assignedUsers}" var="userInEvent" begin="0" step="1" varStatus="i">
    <span>${userInEvent.userEntity.fullName}</span>
</c:forEach>
</p>
</c:if>

<div class="btn-group">
    <a class="btn btn-small" onclick="$(this).parents('#mapPopupContainer').load('<s:url value='${root}/admin/event/map/popup/poi?edit=true&eventId=${poi.eventId}&poiId=${poi.id}'/>');return false"><i class="icon-pencil"></i> <s:message code="button.edit"/></a>
</div>
