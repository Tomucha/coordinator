<%@
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
    taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
    taglib prefix="sf" uri="http://www.springframework.org/tags/form" %><%@
    taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %><%@
    taglib prefix="www" uri="/WEB-INF/www.tld" %><%@
    taglib prefix="tags" tagdir="/WEB-INF/tags"
%>
<script type="text/javascript">

    var eventId = ${event.id};
    function initialize() {
        CoordinatorMap.setOnClickAddPoint(TYPE_POI, "${root}/admin/event/map/popup/poi?eventId=${event.id}");
        refreshMarkers();
    }

    function fillPoiMarkers(bounds) {
        var url = root+"/admin/event/map/api/poi";
        var decodedRequest = $("#filterForm").serialize();

        $.getJSON(url, decodedRequest, function(response, txt) {
            $.each(response, function(i, item) {
                item.popupUrl = "${root}/admin/event/map/popup/poi?poiId="+item.id+"&eventId=${event.id}",
                item.type = TYPE_POI;
                item.icon = ICON_POI[item.poiCategoryId];
                CoordinatorMap.addPoint(item);
            });
        });
    }

    function fillUserMarkers() {
        var url = root+"/admin/event/map/api/user";
        var decodedRequest = $("#filterForm").serialize();

        $.getJSON(url, decodedRequest, function(response, txt) {
            $.each(response, function(i, item) {
                item.name = item.userEntity.fullName;
                item.popupUrl = "${root}/admin/event/map/popup/user?userId="+item.userId+"&eventId=${event.id}",
                item.type = TYPE_USER;
                item.icon = ICON_USER;
                item.latitude = item.lastLocationLatitude;
                item.longitude = item.lastLocationLongitude;
                CoordinatorMap.addPoint(item);
            });
        });
    }

    function boundsToFilterForm(bounds) {
        var url = root+"/admin/event/map/api/user";
        var arrBounds = bounds.toArray(); //array order: left, bottom, right, top
        $("#inputLatS").val(arrBounds[1]);
        $("#inputLatN").val(arrBounds[3]);
        $("#inputLonW").val(arrBounds[0]);
        $("#inputLonE").val(arrBounds[2]);
    }

    function refreshMarkers() {
        CoordinatorMap.clearMarkers();
        var bounds = map.getExtent();
        var proj = new OpenLayers.Projection("EPSG:4326");
        bounds.transform(map.getProjectionObject(), proj);
        boundsToFilterForm(bounds);

        if ($("#showpois").prop("checked")) {
            fillPoiMarkers();
            $("#poisFilter").slideDown();
        } else
            $("#poisFilter").slideUp();

        if ($("#showusers").prop("checked")) {
            fillUserMarkers();
            $("#usersFilter").slideDown();
        } else
            $("#usersFilter").slideUp();
    }
</script>

<div>
    <div>
    <tags:osm
            width="90%"
            height="500px"
            latitude="${event.firstEventLocation.latitude}"
            longitude="${event.firstEventLocation.longitude}"
            onLoad="initialize()"
            onMapChange="refreshMarkers()"
            />
    <sf:form modelAttribute="params" id="filterForm" onsubmit="return false">
        <div class="container-fluid">
            <tags:hiddenEvent/>

            <input type="hidden" id="inputLatS" name="latS"/>
            <input type="hidden" id="inputLatN" name="latN"/>
            <input type="hidden" id="inputLonW" name="lonW"/>
            <input type="hidden" id="inputLonE" name="lonE"/>

            <div class="row-fluid">
                <div class="span5">
                    <label class="checkbox"><input type="checkbox" id="showusers" onchange="refreshMarkers()" checked="checked"/> <s:message code="label.showUsers"/></label>
                    <div id="usersFilter">
                        <label><s:message code="label.group"/>:</label>
                        <sf:select path="groupId" onchange="refreshMarkers()">
                            <sf:option value=""/>
                            <sf:options items="${userGroups}" itemLabel="name" itemValue="id"/>
                        </sf:select>
                        <label><s:message code="label.name"/>:</label> <sf:input path="userFulltext" onchange="refreshMarkers()"/>
                    </div>
                </div>
                <div class="span5">
                    <label class="checkbox"><input type="checkbox" id="showpois" onchange="refreshMarkers()" checked="checked"/> <s:message code="label.showPois"/></label>
                    <div id="poisFilter">
                        <label><s:message code="label.workflow"/>:</label>
                        <sf:select path="workflowId" onchange="refreshMarkers()">
                            <sf:option value=""/>
                            <sf:options items="${config.workflowList}" itemLabel="name" itemValue="id"/>
                        </sf:select>
                        <label><s:message code="label.workflowState"/>:</label>
                        <sf:select path="workflowStateId" onchange="refreshMarkers()">
                            <sf:option value=""/>
                            <c:forEach items="${config.workflowList}" var="workflow">
                                <c:forEach items="${workflow.states}" var="state">
                                    <sf:option value="${state.id}">${workflow.name} &gt; ${state.name}</sf:option>
                                </c:forEach>
                            </c:forEach>
                        </sf:select>
                    </div>
                </div>
            </div>
        </div>
    </sf:form>
    </div>
</div>
