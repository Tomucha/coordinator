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

    function showWorkflowStates() {
        $("#workflowStateSelect").val("");

        var workflowId = $("#workflowSelect").val();

        if (workflowId == "") {
            $("#workflowStateBox").hide();
            return;
        }

        $("#workflowStateBox").show();

        // TODO no completely safe
        $("#workflowStateSelect option").each(function(index, option) {
            if (option.id.substr(3, workflowId.length + 1) == workflowId + ".") {
                $(option).show();
            } else {
                $(option).hide();
            }
        });
    }

    $(function() {
        showWorkflowStates();
    });

    function initialize() {
    <c:if test="${!empty form.latitude and !empty form.longitude}">
        CoordinatorMap.addPoint({
            type: TYPE_LOCATION,
            longitude: <c:out value="${form.longitude}"/>,
            latitude: <c:out value="${form.latitude}"/>
        });
    </c:if>
    }

</script>

<h2>
    <s:message code="${form.new ? 'header.poiCreate' : 'header.poiEdit'}"/>
</h2>

<div class="eastPanel" style="float:right;width: 300px;margin-left: 30px">
    <tags:osm
            width="300px"
            height="300px"
            longitude="${form.longitude}"
            latitude="${form.latitude}"
            zoom="13"
            onLoad="initialize()"
            onNewPoint="onNewPoint"
            maxPoints="loc=1"
            buttons="addLocation"
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

                <tags:input field="poiCategoryId" modelAttribute="form" captionCode="PoiEntity.poiCategory">
                    <sf:select path="poiCategoryId" items="${config.poiCategoryMap}" itemLabel="name"/>
                </tags:input>

                <div>
                    <tags:input field="confirmed" modelAttribute="form" captionCode="PoiEntity.confirmed">
                        <sf:checkbox path="confirmed"/>
                    </tags:input>
                </div>

                <div>
                    <tags:input field="longitude" modelAttribute="form" captionCode="PoiEntity.longitude">
                        <sf:input id="longitudeInput" path="longitude"/>
                    </tags:input>
                </div>

                <div>
                    <tags:input field="latitude" modelAttribute="form" captionCode="PoiEntity.latitude">
                        <sf:input id="latitudeInput" path="latitude"/>
                    </tags:input>
                </div>

                <div>
                    <tags:input field="precission" modelAttribute="form" captionCode="PoiEntity.precission">
                        <sf:input path="precission"/>
                    </tags:input>
                </div>

                <div>
                    <tags:input field="workflowId" modelAttribute="form" captionCode="PoiEntity.workflow">
                        <sf:select id="workflowSelect" path="workflowId" onchange="showWorkflowStates()">
                            <sf:option value=""><s:message code="label.emptyWorkflow"/></sf:option>
                            <c:forEach items="${config.workflowMap}" var="entry">
                                <sf:option value="${entry.key}" label="${entry.value.name}"/>
                            </c:forEach>
                        </sf:select>
                    </tags:input>
                </div>

                <div id="workflowStateBox">
                    <tags:input field="workflowStateId" modelAttribute="form" caption="PoiEntity.workflowState">
                        <sf:select id="workflowStateSelect" path="workflowStateId">
                            <sf:option value=""></sf:option>
                            <c:forEach items="${config.workflowList}" var="workflow">
                                <c:if test="${!empty workflow.states}">
                                    <c:forEach items="${workflow.states}" var="state">
                                        <sf:option id="ws.${workflow.id}.${state.id}" value="${state.id}" label="${state.name}"/>
                                    </c:forEach>
                                </c:if>
                            </c:forEach>
                        </sf:select>
                    </tags:input>
                </div>
            </div>

            <div class="buttonPanel">
                <sf:button><s:message code="button.save"/></sf:button>
            </div>

        </sf:form>
    </div>
</div>

