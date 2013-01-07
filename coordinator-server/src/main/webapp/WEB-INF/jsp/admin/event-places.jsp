<%@
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
    taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
    taglib prefix="sf" uri="http://www.springframework.org/tags/form" %><%@
    taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %><%@
    taglib prefix="can" uri="/WEB-INF/permissions.tld" %><%@
    taglib prefix="tags" tagdir="/WEB-INF/tags"
%><script type="text/javascript">
    function initialize() {
        <c:if test="${!empty placeList}">
        <c:forEach items="${placeList}" var="place">
        <c:if test="${!empty place.id and !empty place.latitude and !empty place.longitude}">
        <c:if test="${empty defLatitude}">
            <c:set var="defLatitude" value="${place.latitude}"/>
            <c:set var="defLongitude" value="${place.longitude}"/>
        </c:if>
        CoordinatorMap.addPoint({
            type: TYPE_POI,
            placeId: <c:out value="${place.id}" />,
            description: "<c:out value="${place.poiCategory.name}"/>",
            longitude: <c:out value="${place.longitude}"/>,
            latitude: <c:out value="${place.latitude}"/>
        });
        </c:if>
        </c:forEach>
        </c:if>
    }
</script>

<h2><s:message code="header.poiList"/></h2>

<div class="eastPanel" style="float:right;width: 300px;margin-left: 30px">
    <tags:osm
            width="300px"
            height="300px"
            longitude="${defLongitude}"
            latitude="${defLatitude}"
            zoom="13"
            onLoad="initialize()"
            />
</div>

<div class="mainPanel" style="padding-right: 330px">

    <div class="buttonPanel">
        <c:choose>
            <c:when test="${can:hasRole('BACKEND')}">
                <a class="btn" href="<s:url value="/admin/event/place/edit?eventId=${params.eventId}"/>"><s:message code="button.addNew"/></a>
            </c:when>
        </c:choose>
    </div>

    <c:choose>
        <c:when test="${!empty placeList}">
            <sf:form action="" method="post" modelAttribute="selectionForm">
                <div class="dataList poiListTable">
                    <sf:hidden path="eventId"/>

                    <table class="table table-striped">
                        <thead>
                        <tr>
                            <th></th>
                            <th><s:message code="PoiEntity.poiCategory"/></th>
                            <th><s:message code="label.locality"/></th>
                            <th><s:message code="PoiEntity.userCount"/></th>
                            <th><s:message code="PoiEntity.workflow"/></th>
                            <th><s:message code="PoiEntity.workflowState"/></th>
                            <th><s:message code="label.action"/></th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${placeList}" var="poi" varStatus="i">
                            <tr>
                                <td><input type="checkbox" name="selectedPois[${i.index}]" value="${poi.id}"/></td>
                                <td><c:out value="${poi.poiCategory.name}"/></td>
                                <td><tags:gps longitude="${poi.longitude}" latitude="${poi.latitude}"/></td>
                                <td><c:out value="${poi.userCount}"/></td>
                                <td><c:out value="${poi.workflow.name}"/></td>
                                <td><c:out value="${poi.workflowState.name}"/></td>
                                <td>
                                    <a class="btn" href="<s:url value="${root}/admin/event/place/edit?eventId=${poi.eventId}&placeId=${poi.id}"/>"><s:message code="button.edit"/></a>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>

                    <div>
                        <sf:select path="selectedAction">
                            <sf:option value=""/>
                            <c:forEach items="${selectedPoiActions}" var="action">
                                <sf:option value="${action}"><s:message code="SelectedPoiAction.${action}"/></sf:option>
                            </c:forEach>
                        </sf:select>

                        <sf:button><s:message code="button.submit"/></sf:button>
                    </div>
                </div>
            </sf:form>

        </c:when>
        <c:otherwise>
            <p><s:message code="msg.noPoisFound"/></p>
        </c:otherwise>

    </c:choose>
</div>
