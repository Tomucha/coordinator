<%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
        taglib prefix="sf" uri="http://www.springframework.org/tags/form" %><%@
        taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %><%@
        taglib prefix="tags" tagdir="/WEB-INF/tags"
%><script type="text/javascript">
    function onNewMarker(marker) {
       var location = CoordinatorMap.toLocation(marker);
       $("#latitudeInput").val(location.latitude);
       $("#longitudeInput").val(location.longitude);
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
            enableLocations="true"
            onNewMarker="onNewMarker"
            maxLocations="1"
            />
</div>

<div class="mainPanel">
    <div class="eventForm">
        <sf:form method="POST" action="${root}/admin/place/edit" modelAttribute="form">

            <sf:errors />

            <div>
                <sf:hidden path="id"/>
                <sf:hidden path="eventId"/>

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
                        <sf:select path="workflowId" items="${config.workflowMap}" itemLabel="name"/>
                    </tags:input>
                </div>
            </div>

            <div class="buttonPanel">
                <sf:button><s:message code="button.save"/></sf:button>
            </div>

        </sf:form>
    </div>
</div>

