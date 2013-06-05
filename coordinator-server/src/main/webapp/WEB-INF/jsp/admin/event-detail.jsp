<%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
        taglib prefix="sf" uri="http://www.springframework.org/tags/form" %><%@
        taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %><%@
        taglib prefix="tags" tagdir="/WEB-INF/tags"
%>
        <tags:initDatePicker element="#datePublishInput, #dateClosedInput, #dateClosedRegistrationInput"/>

        <sf:form method="POST" modelAttribute="form">

            <sf:errors cssClass="alert alert-error" />

            <%--
            <c:if test="${empty form.organizationId}">
                <div><s:message code="msg.notEventsDueEmptyOrganizationId"/></div>
            </c:if>
--%>
            <div>
                <sf:hidden path="id"/>
                <sf:hidden path="organizationId"/>
                <tags:hiddenEvent/>

                <tags:hiddenEvent/>
                <c:choose>
                    <c:when test="${form.new}">
                            <h2><c:out value="${event.name}"/></h2>
                    </c:when>
                    <c:otherwise>
                            <tags:hiddenEvent/>
                            <h2><c:out value="${form.eventEntity.name}"/></h2>
                    </c:otherwise>
                </c:choose>

            </div>

            <div class="fluid">
                <div class="row-fluid">
                    <div class="mini-layout span6">
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

                        <div>
                            <tags:input field="datePublish" modelAttribute="form" captionCode="OrganizationInEventEntity.datePublish">
                                <sf:input path="datePublish" id="datePublishInput"/>
                            </tags:input>
                        </div>

                        <div>
                            <tags:input field="dateClosedRegistration" modelAttribute="form" captionCode="OrganizationInEventEntity.dateClosedRegistration">
                                <sf:input path="dateClosedRegistration" id="dateClosedRegistrationInput"/>
                            </tags:input>
                        </div>

                        <div>
                            <tags:input field="dateClosed" modelAttribute="form" captionCode="OrganizationInEventEntity.dateClosed">
                                <sf:input path="dateClosed" id="dateClosedInput"/>
                            </tags:input>
                        </div>
                    </div>

                    <div class="mini-layout span6">
                        <div class="panel checkboxList">
                            <h4><s:message code="header.equipmentList"/></h4>
                            <sf:checkboxes path="registrationEquipment" items="${config.equipmentList}" itemLabel="name" itemValue="id"/>
                        </div>

                        <div class="panel checkboxList">
                            <h4><s:message code="header.skillList"/></h4>
                            <sf:checkboxes path="registrationSkills" items="${config.skillList}" itemLabel="name" itemValue="id"/>
                        </div>
                    </div>
                </div>
            </div>

            <div class="buttonPanel">
                <button type="submit" class="btn btn-primary"><span class="icon-ok icon-white"></span> <s:message code="button.save"/></button>
            </div>
        </sf:form>