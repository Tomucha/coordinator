<%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
        taglib prefix="sf" uri="http://www.springframework.org/tags/form" %><%@
        taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %><%@
        taglib prefix="tags" tagdir="/WEB-INF/tags"
%><script type="text/javascript">
    function onNewPoint(point) {
       $("#latitudeInput").val(point.latitude);
       $("#longitudeInput").val(point.longitude);
    }

    function initialize() {
        CoordinatorMap.disablePopup(TYPE_POI);
        CoordinatorMap.disablePopup(TYPE_LOCATION);

        <c:if test="${!empty form.latitude and !empty form.longitude}">
            CoordinatorMap.addPoint({
                type: TYPE_POI,
                placeId: <c:out value="${form.id}"/>,
                longitude: <c:out value="${form.longitude}"/>,
                latitude: <c:out value="${form.latitude}"/>
            });
            </c:if>

        <c:if test="${!empty event.eventLocationEntityList}">
            <c:forEach items="${event.eventLocationEntityList}" var="location">
                CoordinatorMap.addPoint({
                    type: TYPE_LOCATION,
                    longitude: <c:out value="${location.longitude}"/>,
                    latitude: <c:out value="${location.latitude}"/>
                });
            </c:forEach>
        </c:if>
    }

</script>

<h2>
    <s:message code="${form.new ? 'header.poiCreate' : 'header.poiEdit'}"/>
</h2>


<div class="pull-right" width="400px">
    <tags:osm
            width="400px"
            height="300px"
            longitude="${!empty form.longitude ? form.longitude : event.firstEventLocation.longitude}"
            latitude="${!empty form.latitude ? form.latitude : event.firstEventLocation.latitude}"
            zoom="13"
            onLoad="initialize()"
            onNewPoint="onNewPoint"
            maxPoints="poi=1"
            buttons="addPlace"
            />

</div>


<div class="mainPanel">
    <div class="eventForm">
        <sf:form method="POST" action="${root}/admin/event/place/edit" modelAttribute="form">

            <sf:errors />

            <div>
                <sf:hidden path="id"/>
                <sf:hidden path="eventId"/>
                <sf:hidden path="organizationId"/>
                <sf:hidden path="workflowStateId"/>
                <sf:hidden path="confirmed"/>

                <tags:input field="poiCategoryId" modelAttribute="form" captionCode="PoiEntity.poiCategory">
                    <sf:select path="poiCategoryId" items="${config.poiCategoryMap}" itemLabel="name"/>
                </tags:input>

                <div>
                    <tags:input field="name" modelAttribute="form" captionCode="PoiEntity.name">
                        <sf:input id="name" path="name"/>
                    </tags:input>
                </div>

                <div>
                    <tags:input field="description" modelAttribute="form" captionCode="PoiEntity.description">
                        <sf:textarea id="description" path="description" rows="3"/>
                    </tags:input>
                </div>

                <div>
                    <tags:input field="longitude" modelAttribute="form" captionCode="PoiEntity.longitude">
                        <sf:input id="longitudeInput" path="longitude" readonly="true"/>
                    </tags:input>
                </div>

                <div>
                    <tags:input field="latitude" modelAttribute="form" captionCode="PoiEntity.latitude">
                        <sf:input id="latitudeInput" path="latitude" readonly="true"/>
                    </tags:input>
                </div>



<%--
                                    <div>
                                        <tags:input field="precission" modelAttribute="form" captionCode="PoiEntity.precission">
                                            <sf:input path="precission"/>
                                        </tags:input>
                                    </div>

                                    <div>
                                        <tags:input field="workflowId" modelAttribute="form" captionCode="PoiEntity.workflow">
                                            <sf:select id="workflowSelect" path="workflowId">
                                                <sf:option value=""><s:message code="label.emptyWorkflow"/></sf:option>
                                                <c:forEach items="${config.workflowMap}" var="entry">
                                                    <sf:option value="${entry.key}" label="${entry.value.name}"/>
                                                </c:forEach>
                                            </sf:select>
                                        </tags:input>
                                    </div>--%>

                <%--<div>--%>
                    <%--<tags:input field="assignedUsers" modelAttribute="form" captionCode="label.assignedUsers">--%>
                        <%--<sf:checkboxes path="assignedUsers" items="${users}" itemValue="id" itemLabel="fullName"/>--%>
                    <%--</tags:input>--%>
                <%--</div>--%>
            </div>

            <div class="buttonPanel">
                <sf:button><s:message code="button.save"/></sf:button>
            </div>


        </sf:form>


    </div>
</div>
