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

<script type="text/javascript">
    function poiPopupNewSubmit(form) {
        var data = form.serializeArray();
        var container = form.parent();
        container.load(form.attr("action"), data);

        return false;
    }
</script>

<sf:form action="${root}/admin/event/map/ajax/poi-update" modelAttribute="poiForm" onsubmit="return poiPopupNewSubmit($(this))" method="post">
    <sf:errors />

    <div>
    <sf:hidden path="id"/>
    <tags:hiddenEvent/>
    <sf:hidden path="organizationId"/>
    <sf:hidden path="workflowStateId"/>
    <sf:hidden path="confirmed"/>
    <sf:hidden path="longitude" id="mapLongitudeSet"/>
    <sf:hidden path="latitude" id="mapLatitudeSet"/>

    <tags:input field="poiCategoryId" modelAttribute="form" captionCode="PoiEntity.poiCategory" fluid="true">
        <sf:select path="poiCategoryId" items="${config.poiCategoryMap}" itemLabel="name" cssClass="input-medium"/>
    </tags:input>

    <div>
        <tags:input field="name" modelAttribute="form" captionCode="PoiEntity.name" fluid="true">
            <sf:input id="name" path="name"  cssClass="input-medium"/>
        </tags:input>
    </div>

    <div>
        <tags:input field="description" modelAttribute="form" captionCode="PoiEntity.descriptionShort" fluid="true">
            <sf:textarea id="description" path="description" rows="2"  cssClass="input-medium"/>
        </tags:input>
    </div>

    <div class="btn-group">
    <button class="btn btn-mini btn-primary" type="submit"><s:message code="button.addNew"/></button>
</div>
</sf:form>
