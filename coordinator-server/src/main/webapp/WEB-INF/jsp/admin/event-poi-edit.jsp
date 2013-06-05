<%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
        taglib prefix="sf" uri="http://www.springframework.org/tags/form" %><%@
        taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %><%@
        taglib prefix="tags" tagdir="/WEB-INF/tags"
%><script type="text/javascript">

    osmCallback.onNewPoint = function(point) {
       $("#latitudeInput").val(point.latitude);
       $("#longitudeInput").val(point.longitude);
    }

    osmCallback.onLoad = function() {
        CoordinatorMap.setOnClickAddPoint();

        <c:if test="${!empty form.latitude and !empty form.longitude}">
            CoordinatorMap.addSinglePoint({
                poiId: <c:out value="${form.id}"/>,
                longitude: <c:out value="${form.longitude}"/>,
                latitude: <c:out value="${form.latitude}"/>
            });
            CoordinatorMap.goTo(<c:out value="${form.longitude}"/>, <c:out value="${form.latitude}"/>);
        </c:if>
    }

</script>


<c:if test="${!empty form.latitude and !empty form.longitude}">
    <tags:zoomButton latitude="${form.latitude}" longitude="${form.longitude}"/>
</c:if>

<h2>
    <s:message code="${form.new ? 'header.poiCreate' : 'header.poiEdit'}"/>
</h2>

<div class="fluid">
    <div class="row-fluid">
            <sf:form method="POST" action="${root}/admin/event/poi/edit" modelAttribute="form">

                <sf:errors cssClass="alert alert-error"  />

                <div>
                    <sf:hidden path="id"/>
                    <tags:hiddenEvent/>
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
                    <button type="submit" class="btn btn-primary"><span class="icon-ok icon-white"></span> <s:message code="button.save"/></button>
                </div>


            </sf:form>
    </div>
</div>

