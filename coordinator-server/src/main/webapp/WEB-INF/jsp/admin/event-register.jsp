<%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
        taglib prefix="sf" uri="http://www.springframework.org/tags/form" %><%@
        taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %><%@
        taglib prefix="tags" tagdir="/WEB-INF/tags"
%>
<script>
    $(function() {
        $( "#datePublishInput" ).datepicker({dateFormat: "dd.mm.yy"});
        $( "#dateClosedInput" ).datepicker({dateFormat: "dd.mm.yy"});
        $( "#dateClosedRegistrationInput" ).datepicker({dateFormat: "dd.mm.yy"});
    });
</script>

<div class="hero-unit">
    <p>You are not registered.</p>

    <div class="accordion" id="accordion2">
        <div class="accordion-group" style="background-color: white">
            <div class="accordion-heading">
                <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#collapseOne">
                    Register
                </a>
            </div>
            <div id="collapseOne" class="accordion-body collapse">
                <div class="accordion-inner">
                    <sf:form method="POST" modelAttribute="form">

                        <sf:errors />

                        <div>
                            <sf:hidden path="id"/>
                            <sf:hidden path="organizationId"/>
                            <tags:hiddenEvent/>

                            <p class="lead">
                                <tags:hiddenEvent/>
                                <b><s:message code="label.event"/>:</b> <c:out value="${form.eventEntity.name}"/>
                            </p>

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

                        <div class="panel checkboxList">
                            <h3><s:message code="header.equipmentList"/></h3>
                            <sf:checkboxes path="registrationEquipment" items="${config.equipmentList}" itemLabel="name" itemValue="id"/>
                        </div>

                        <div class="panel checkboxList">
                            <h3><s:message code="header.skillList"/></h3>
                            <sf:checkboxes path="registrationSkills" items="${config.skillList}" itemLabel="name" itemValue="id"/>
                        </div>

                        <div class="buttonPanel">
                            <sf:button><s:message code="button.save"/></sf:button>
                        </div>
                    </sf:form>
                </div>
            </div>
        </div>
    </div>
</div>