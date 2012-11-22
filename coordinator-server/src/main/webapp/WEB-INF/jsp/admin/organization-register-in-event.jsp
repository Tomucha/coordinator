<%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
        taglib prefix="sf" uri="http://www.springframework.org/tags/form" %><%@
        taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %><%@
        taglib prefix="tags" tagdir="/WEB-INF/tags"
%><sf:form method="POST" modelAttribute="form">

    <sf:errors />

    <div>
        <sf:hidden path="organizationId"/>

        <tags:input field="eventId" modelAttribute="form" captionCode="OrganizationInEventEntity.eventId">
            <sf:select path="eventId" disabled="${form.new}">
                <c:forEach items="${eventList}" var="event">
                    <sf:option value="${event.id}" label="${event.name}"/>
                </c:forEach>
            </sf:select>
        </tags:input>
    </div>

    <div>
        <tags:input field="name" modelAttribute="form" captionCode="OrganizationInEventEntity.name">
            <sf:input path="name"/>
        </tags:input>
    </div>

    <div>
        <tags:input field="description" modelAttribute="form" captionCode="OrganizationInEventEntity.description">
            <sf:textarea path="description"/>
        </tags:input>
    </div>

    <div>
        <tags:input field="operationalInfo" modelAttribute="form" captionCode="OrganizationInEventEntity.operationalInfo">
            <sf:textarea path="operationalInfo"/>
        </tags:input>
    </div>

    <div class="panel">
        <h3><s:message code="header.equipmentList"/></h3>
        <sf:checkboxes path="registrationEquipment" items="${config.equipmentList}" itemLabel="name" itemValue="id"/>
    </div>

    <div class="panel">
        <h3><s:message code="header.skillList"/></h3>
        <sf:checkboxes path="registrationSkills" items="${config.skillList}" itemLabel="name" itemValue="id"/>
    </div>

    <div class="buttonPanel">
        <sf:button><s:message code="button.save"/></sf:button>
    </div>

</sf:form>
