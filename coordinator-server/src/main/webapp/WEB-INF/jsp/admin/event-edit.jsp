<%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
        taglib prefix="sf" uri="http://www.springframework.org/tags/form" %><%@
        taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %><%@
        taglib prefix="tags" tagdir="/WEB-INF/tags"
%>
<h2><s:message code="header.eventCreate"/></h2>

<div class="eastPanel" style="float:right;width: 300px;margin-left: 30px">
    <div class="buttonPanel">
        <button type="button" onclick="CoordinatorMap.startSetLocation()">__Set location__</button>
    </div>
    <tags:osm width="300px" height="300px" longitude="14.4489967" latitude="50.0789306" zoom="13"/>
</div>

<script type="text/javascript">
    function fetchLocations() {
        var locations = CoordinatorMap.getLocations();
        var cont = $("#hiddenInputContainer");
        for (var i = 0 ; i < locations.length ; i++) {
            var location = locations[i];
            $('<input>').attr({
                <%-- TODO json --%>
                eventId: "<c:out value="${form.eventId}" escapeXml="true"/>",
                type: 'hidden',
                name: 'locationList[' + i + '].longitude',
                value: location.longitude
            }).appendTo(cont);
            $('<input>').attr({
                eventId: "<c:out value="${form.eventId}" escapeXml="true"/>",
                type: 'hidden',
                name: 'locationList[' + i + '].latitude',
                value: location.latitude
            }).appendTo('form');
            $('<input>').attr({
                eventId: "<c:out value="${form.eventId}" escapeXml="true"/>",
                type: 'hidden',
                name: 'locationList[' + i + '].radius',
                value: location.radius
            }).appendTo('form');
        }

        return true;
    }
</script>

<div class="mainPanel">
    <div class="eventForm">
        <sf:form method="POST" action="${root}/admin/event/edit" modelAttribute="form" onsubmit="return fetchLocations()">

            <sf:errors />

            <c:if test="${form.new}">
                <div>
                    <tags:input field="eventId" modelAttribute="form" captionCode="label.eventId">
                        <sf:input path="eventId" />
                    </tags:input>
                </div>
            </c:if>

            <div id="hiddenInputContainer">
                <sf:hidden path="id"/>
                <tags:input field="name" modelAttribute="form" captionCode="label.name">
                    <sf:input path="name"/>
                </tags:input>
            </div>

            <div>
                <tags:input field="description" modelAttribute="form" captionCode="label.description">
                    <sf:textarea path="description" cols="72" rows="7"/>
                </tags:input>
            </div>

            <div class="buttonPanel">
                <sf:button><s:message code="button.save"/></sf:button>
            </div>

        </sf:form>
    </div>
</div>

