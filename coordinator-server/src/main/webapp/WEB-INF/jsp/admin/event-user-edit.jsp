<%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
        taglib prefix="sf" uri="http://www.springframework.org/tags/form" %><%@
        taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %><%@
        taglib prefix="tags" tagdir="/WEB-INF/tags" %><%@
        taglib prefix="can" uri="/WEB-INF/permissions.tld"
%><script>
    $(function() {
        $( "#birthdayInput" ).datepicker({dateFormat: "dd.mm.yy"});
    });
</script>
<h2>
    <c:choose>
        <c:when test="${empty form.id}"><s:message code="header.userCreate"/></c:when>
        <c:otherwise><s:message code="header.userUpdate"/></c:otherwise>
    </c:choose>
</h2>

<div class="mainPanel">
    <div class="userForm">
        <sf:form method="POST" action="${root}/admin/event/user/edit" modelAttribute="form">

            <sf:errors />

            <div class="formPanel">
                <div>
                    <sf:hidden path="id"/>
                    <sf:hidden path="eventId"/>
                    <sf:hidden path="organizationId"/>
                    <sf:hidden path="userInEventId"/>

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
                            <c:forEach items="${config.countryMap}" var="entry">
                                <sf:option value="${entry.key}" label="${entry.value}"/>
                            </c:forEach>
                        </sf:select>
                    </tags:input>
                </div>
            </div>

            <div class="panel">
                <h3><s:message code="header.equipmentList"/></h3>
                <sf:checkboxes path="selectedEquipment" items="${config.equipmentList}" itemLabel="name" itemValue="id"/>
            </div>

            <div class="panel">
                <h3><s:message code="header.skillList"/></h3>
                <sf:checkboxes path="selectedSkill" items="${config.skillList}" itemLabel="name" itemValue="id"/>
            </div>

            <div class="buttonPanel">
                <sf:button><s:message code="button.save"/></sf:button>
            </div>
        </sf:form>
    </div>
</div>