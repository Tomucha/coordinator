<%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
        taglib prefix="sf" uri="http://www.springframework.org/tags/form" %><%@
        taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %><%@
        taglib prefix="tags" tagdir="/WEB-INF/tags" %><%@
        taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %><%@
        taglib prefix="can" uri="/WEB-INF/permissions.tld"
%>
<tags:initDatePicker element="#birthdayInput"/>

<script type="text/javascript">

    osmCallback.onNewPoint = function(point) {
        $("#latitudeInput").val(point.latitude);
        $("#longitudeInput").val(point.longitude);
    }

    osmCallback.onLoad = function() {
        CoordinatorMap.setOnClickAddPoint();

        <c:if test="${!empty form.lastLocationLatitude and !empty form.lastLocationLongitude}">
        CoordinatorMap.addSinglePoint({
            poiId: <c:out value="${form.id}"/>,
            longitude: <c:out value="${form.lastLocationLongitude}"/>,
            latitude: <c:out value="${form.lastLocationLatitude}"/>
        });
        CoordinatorMap.goTo(<c:out value="${form.lastLocationLongitude}"/>, <c:out value="${form.lastLocationLatitude}"/>);
        </c:if>
    }

</script>


<c:if test="${!empty form.lastLocationLatitude and !empty form.lastLocationLongitude}">
    <tags:zoomButton latitude="${form.lastLocationLatitude}" longitude="${form.lastLocationLongitude}"/>
</c:if>


<h2>
    <c:choose>
        <c:when test="${empty form.id}"><s:message code="header.userCreate"/></c:when>
        <c:otherwise><s:message code="header.userUpdate"/></c:otherwise>
    </c:choose>
</h2>



<div class="mainPanel">
    <div class="userForm">
        <sf:form method="POST" action="${root}/admin/event/user/edit" modelAttribute="form">

            <sf:errors cssClass="alert alert-error" />

            <div class="fluid">
                <div class="row-fluid">
                    <div class="mini-layout span6">
                        <div>
                            <c:choose>
                                <c:when test="${can:isSuperadmin() and empty form.id}">
                                    <div>
                                        <tags:input field="organizationId" modelAttribute="form" captionCode="UserEntity.organization">
                                            <sf:select path="organizationId" items="${config.organizationMap}" itemLabel="name"/>
                                        </tags:input>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <sf:hidden path="organizationId"/>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <div>
                            <tags:input field="roleIdList" modelAttribute="" captionCode="UserEntity.roles">
                                <sf:select path="roleIdList" multiple="true" items="${form.acceptableRoleMap}"/>
                            </tags:input>
                        </div>

                        <div>
                            <tags:hiddenEvent/>
                            <sf:hidden path="userId"/>
                            <sf:hidden path="createdDate"/>

                            <tags:input field="firstName" modelAttribute="form" captionCode="UserEntity.firstName">
                                <sf:input path="firstName" />
                            </tags:input>
                        </div>

                        <div>
                            <tags:input field="lastName" modelAttribute="form" captionCode="UserEntity.lastName">
                                <sf:input path="lastName"/>
                            </tags:input>
                        </div>

                        <div>
                            <tags:input field="email" modelAttribute="form" captionCode="UserEntity.email">
                                <sf:input path="email"/>
                            </tags:input>
                        </div>

                        <c:if test="${empty form.id}">
                            <div>
                                <tags:input field="password" modelAttribute="form" captionCode="UserEntity.password">
                                    <sf:password path="password"/>
                                </tags:input>
                            </div>

                            <div>
                                <tags:input field="confirmPassword" modelAttribute="form" captionCode="UserEntity.confirmPassword">
                                    <sf:password path="confirmPassword"/>
                                </tags:input>
                            </div>
                        </c:if>

                        <div>
                            <tags:input field="phone" modelAttribute="form" captionCode="UserEntity.phone">
                                <sf:input path="phone"/>
                            </tags:input>
                        </div>

                        <div>
                            <tags:input field="birthday" modelAttribute="form" captionCode="UserEntity.birthday">
                                <sf:input path="birthday" id="birthdayInput" />
                            </tags:input>
                        </div>

                        <div>
                            <tags:input field="addressLine" modelAttribute="form" captionCode="UserEntity.addressLine">
                                <sf:input path="addressLine"/>
                            </tags:input>
                        </div>

                        <div>
                            <tags:input field="city" modelAttribute="form" captionCode="UserEntity.city">
                                <sf:input path="city"/>
                            </tags:input>
                        </div>

                        <div>
                            <tags:input field="zip" modelAttribute="form" captionCode="UserEntity.zip">
                                <sf:input path="zip"/>
                            </tags:input>
                        </div>

                        <div>
                            <tags:input field="country" modelAttribute="form" captionCode="UserEntity.country">
                                <sf:select path="country">
                                    <sf:option value=""><s:message code="label.emptyCountry"/></sf:option>
                                    <sf:options items="${config.countryMap}"/>
                                </sf:select>
                            </tags:input>
                        </div>

                        <div>
                            <tags:input field="lastLocationLongitude" modelAttribute="form" captionCode="UserEntity.lastLocationLongitude">
                                <sf:input id="longitudeInput" path="lastLocationLongitude" readonly="true"/>
                            </tags:input>
                        </div>

                        <div>
                            <tags:input field="lastLocationLatitude" modelAttribute="form" captionCode="UserEntity.lastLocationLatitude">
                                <sf:input id="latitudeInput" path="lastLocationLatitude" readonly="true"/>
                            </tags:input>
                        </div>
                    </div>

                    <div class="mini-layout span6">

                        <div class="panel checkboxList">
                            <h3><s:message code="header.equipmentList"/></h3>
                            <sf:checkboxes path="selectedEquipment" items="${equipmentList}" itemLabel="name" itemValue="id"/>
                            <c:if test="${empty equipmentList or fn:length(equipmentList) == 0}">
                                <p class="label label-inf"><s:message code="msg.noItemsAreRequired"/></p>
                            </c:if>
                        </div>

                        <div class="panel checkboxList">
                            <h3><s:message code="header.skillList"/></h3>
                            <sf:checkboxes path="selectedSkill" items="${skillList}" itemLabel="name" itemValue="id"/>
                            <c:if test="${empty skillList or fn:length(skillList) == 0}">
                                <p class="label label-inf"><s:message code="msg.noItemsAreRequired"/></p>
                            </c:if>
                        </div>

                        <div class="panel checkboxList">
                            <h3><s:message code="header.userGroupList"/></h3>
                            <sf:checkboxes path="groupIdList" items="${userGroups}" itemLabel="name" itemValue="id"/>
                        </div>

                    </div>

                </div>
            </div>

            <div class="buttonPanel">
                <sf:button class="btn btn-primary" disabled="${!can:create('userEntity')}" ><span class="icon-ok icon-white"></span> <s:message code="button.save"/></sf:button>
            </div>
        </sf:form>
    </div>
</div>
