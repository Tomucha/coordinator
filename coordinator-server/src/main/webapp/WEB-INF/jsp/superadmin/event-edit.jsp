<%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
        taglib prefix="sf" uri="http://www.springframework.org/tags/form" %><%@
        taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %><%@
        taglib prefix="tags" tagdir="/WEB-INF/tags"
%><script type="text/javascript">

    var hiddenCount = 0;
    <c:if test="${!empty form.eventLocationEntityList}">
        hiddenCount = ${fn:length(form.eventLocationEntityList)};
    </c:if>

    function addLocation(point) {
        var cont = $("#hiddenInputContainer");
        $('<input>').attr({
                type: 'hidden',
                name: 'eventLocationEntityList[' + hiddenCount + '].longitude',
                value: point.longitude
        }).appendTo(cont);
        $('<input>').attr({
                type: 'hidden',
                name: 'eventLocationEntityList[' + hiddenCount + '].latitude',
                value: point.latitude
        }).appendTo(cont);
        $('<input>').attr({
                type: 'hidden',
                name: 'eventLocationEntityList[' + hiddenCount + '].radius',
                value: 2000
        }).appendTo(cont);
        hiddenCount++;
    }

    osmCallback.onNewPoint = function(point) {
        CoordinatorMap.addPoint(point);
        addLocation(point);
    }

    osmCallback.onLoad = function() {
        CoordinatorMap.setOnClickAddPoint(null);

        <c:if test="${!empty form.eventLocationEntityList}">
            <c:forEach items="${form.eventLocationEntityList}" var="eventLocation">
                 <c:if test="${!empty eventLocation.longitude and !empty eventLocation.latitude}">
                     CoordinatorMap.addPoint({
                         id: eventLocation.id,
                         type: TYPE_LOCATION,
                         icon: ICON_GENERIC,
                         longitude: <c:out value="${eventLocation.longitude}"/>,
                         latitude: <c:out value="${eventLocation.latitude}"/>
                    });
                </c:if>
            </c:forEach>
        </c:if>
    }
</script>

<h2>
    <s:message code="${form.new ? 'header.eventCreate' : 'header.eventEdit'}"/>
</h2>

<div class="mainPanel">

    <div class="eventForm">
        <sf:form method="POST" action="${root}/superadmin/event/edit" modelAttribute="form">

            <sf:errors cssClass="alert alert-error" />

            <tags:hiddenEvent/>

            <sf:hidden path="id"/>

            <tags:input field="name" modelAttribute="form" captionCode="label.name">
                <sf:input path="name"/>
            </tags:input>

            <div id="hiddenInputContainer">
                <c:if test="${!empty form.eventLocationEntityList}">
                    <c:forEach items="${form.eventLocationEntityList}" var="eventLocation" varStatus="c">
                        <input type="hidden" name="eventLocationEntityList[${c.count-1}].longitude" value="${eventLocation.longitude}"/>
                        <input type="hidden" name="eventLocationEntityList[${c.count-1}].latitude" value="${eventLocation.latitude}"/>
                        <input type="hidden" name="eventLocationEntityList[${c.count-1}].radius" value="${eventLocation.radius}"/>
                    </c:forEach>
                </c:if>
            </div>

            <div>
                <tags:input field="description" modelAttribute="form" captionCode="label.description">
                    <sf:textarea path="description" cols="142" rows="7" cssStyle="width:50%"/>
                </tags:input>
            </div>

            <div class="buttonPanel">
                <button type="submit" class="btn btn-primary"><span class="icon-ok icon-white"></span> <s:message code="button.save"/></button>
            </div>

        </sf:form>
    </div>

    <tags:osm
            width="100%"
            height="400px"
            hideMarkers="true"
            longitude="${!empty form.firstEventLocation and form.firstEventLocation.longitude > 0.0 ? form.firstEventLocation.longitude : null}"
            latitude="${!empty form.firstEventLocation and form.firstEventLocation.latitude > 0.0 ? form.firstEventLocation.latitude : null}"
            zoom="13"
            />

</div>

