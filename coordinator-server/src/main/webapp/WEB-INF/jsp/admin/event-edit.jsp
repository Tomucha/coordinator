<%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
        taglib prefix="sf" uri="http://www.springframework.org/tags/form" %><%@
        taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %><%@
        taglib prefix="tags" tagdir="/WEB-INF/tags"
%><script type="text/javascript">
    function fetchLocations() {
        var points = CoordinatorMap.getPoints();
        var cont = $("#hiddenInputContainer");
        var i = 0;
        for (var id in points) {
            var location = points[id];
            if (location.type != TYPE_LOCATION) continue;
            $('<input>').attr({
                <%-- TODO json --%>
                eventId: "<c:out value="${form.eventId}" escapeXml="true"/>",
                type: 'hidden',
                name: 'eventLocationEntityList[' + i + '].longitude',
                value: location.longitude
            }).appendTo(cont);
            $('<input>').attr({
                eventId: "<c:out value="${form.eventId}" escapeXml="true"/>",
                type: 'hidden',
                name: 'eventLocationEntityList[' + i + '].latitude',
                value: location.latitude
            }).appendTo(cont);
            $('<input>').attr({
                eventId: "<c:out value="${form.eventId}" escapeXml="true"/>",
                type: 'hidden',
                name: 'eventLocationEntityList[' + i + '].radius',
                value: location.radius
            }).appendTo(cont);
            i++;
        }

        return true;
    }

    function initialize() {
        <c:if test="${!empty form.eventLocationEntityList}">
            <c:forEach items="${form.eventLocationEntityList}" var="eventLocation">
                 <c:if test="${!empty eventLocation.longitude and !empty eventLocation.latitude}">
                     CoordinatorMap.addPoint({
                         type: TYPE_LOCATION,
                         longitude: <c:out value="${eventLocation.longitude}"/>,
                         latitude: <c:out value="${eventLocation.latitude}"/>,
                         radius: <c:out value="${!empty eventLocation.radius ? eventLocation.radius : 'null'}"/>
                    });
                </c:if>
            </c:forEach>
        </c:if>
    }
</script>

<h2>
    <s:message code="${form.new ? 'header.eventCreate' : 'header.eventEdit'}"/>
</h2>

<div class="eastPanel" style="float:right;width: 300px;margin-left: 30px">
    <tags:osm
            width="300px"
            height="300px"
            longitude="${!empty form.firstEventLocation and form.firstEventLocation.longitude > 0.0 ? form.firstEventLocation.longitude : null}"
            latitude="${!empty form.firstEventLocation and form.firstEventLocation.latitude > 0.0 ? form.firstEventLocation.latitude : null}"
            zoom="13"
            onLoad="initialize()"
            buttons="addLocation"
            />
</div>

<div class="mainPanel">
    <div class="eventForm">
        <sf:form method="POST" action="${root}/admin/event/edit" modelAttribute="form" onsubmit="return fetchLocations()">

            <sf:errors />

            <div style="display:${form.new ? 'block' : 'none'}">
                <tags:input field="eventId" modelAttribute="form" captionCode="label.eventId">
                    <sf:input path="eventId" />
                </tags:input>
            </div>

            <div id="hiddenInputContainer">
                <sf:hidden path="id"/>
                <tags:input field="name" modelAttribute="form" captionCode="label.name">
                    <sf:input path="name"/>
                </tags:input>
            </div>

            <div>
                <tags:input field="description" modelAttribute="form" captionCode="label.description">
                    <sf:textarea path="description" cols="142" rows="7" cssStyle="width:50%"/>
                </tags:input>
            </div>

            <div class="buttonPanel">
                <sf:button><s:message code="button.save"/></sf:button>
            </div>

        </sf:form>
    </div>
</div>

