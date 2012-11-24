<%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
        taglib prefix="sf" uri="http://www.springframework.org/tags/form" %><%@
        taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %><%@
        taglib prefix="tags" tagdir="/WEB-INF/tags"
%><c:choose>
    <c:when test="${!form.new or !empty eventList}">
        <sf:form method="POST" modelAttribute="form">

            <sf:errors />

            <c:if test="${empty form.organizationId}">
                <div><s:message code="msg.notEventsDueEmptyOrganizationId"/></div>
            </c:if>

            <div>
                <sf:hidden path="organizationId"/>

                <c:choose>
                    <c:when test="${form.new}">
                        <tags:input field="eventId" modelAttribute="form" captionCode="OrganizationInEventEntity.eventId">
                            <sf:select path="eventId" disabled="${!form.new}" items="${eventList}" itemLabel="name" itemValue="id"/>
                        </tags:input>
                    </c:when>
                    <c:otherwise>
                        <p>
                            <sf:hidden path="eventId"/>
                            <c:out value="${form.eventEntity.name}"/>
                        </p>
                    </c:otherwise>
                </c:choose>

                <c:if test="${!form.new}">
                    <sf:hidden path="eventId"/>
                </c:if>
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
    </c:when>
    <c:otherwise>
        <p><s:message code="msg.noEventsFound"/></p>
    </c:otherwise>
</c:choose>
