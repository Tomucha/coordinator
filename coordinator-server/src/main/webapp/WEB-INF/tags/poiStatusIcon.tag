<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="poi" type="cz.clovekvtisni.coordinator.server.domain.PoiEntity" required="true" %>
<%-- Renders icon for this poi:
- important - should be assigned and moved from start state
- in progress - assigned and running
- finished - workflow is finished
--%>
<c:if test="${not empty poi.workflow}">

    <c:choose>
        <c:when test="${poi.workflowState.workflowStateType == 'STARTED'}"><c:set var="icon">icon-exclamation-sign</c:set></c:when>
        <c:when test="${poi.workflowState.workflowStateType == 'FINISHED'}"><c:set var="icon">icon-ok-sign</c:set></c:when>
        <c:otherwise><c:set var="icon">icon-circle-arrow-right</c:set></c:otherwise>
    </c:choose>
    <a href="${root}/admin/event/poi/workflow?poiId=<c:out value='${poi.id}'/>&eventId=${poi.eventId}">
        <span class="${icon}" title="<c:out value="${poi.workflowState.name}"/>">&nbsp;</span>
    </a>
</c:if>