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
    function onDataSubmit() {
        switch ($("#inputSelectedAction").val()) {
            case "SUSPEND":
                $('#suspendReasonModal').modal({});
                return false;

            case "ADD_TO_GROUP":
                $('#addToUserGroupModal').modal({});
                return false;

            default:
                return true;
        }
    }
</script>
<h2><s:message code="header.userList"/></h2>

<%--@elvariable id="config" type="cz.clovekvtisni.coordinator.server.domain.CoordinatorConfig"--%>

<div class="mainPanel">
    <div class="btn-toolbar">
        <c:choose>
            <c:when test="${can:hasRole('BACKEND')}">
                <c:set var="isFilter" value="${!empty params.groupId or !empty params.userFulltext}"/>

                <button accesskey="f" class="btn${isFilter ? ' btn-danger' : ''}" onclick="$('#searchFormPanel').slideToggle();"><i class="icon-filter${isFilter ? ' icon-white' : ''}"></i> <s:message code="button.filterList"/> <span class="caret"></span></button>

                <a class="btn" href="<s:url value="/admin/event/user/edit?eventId=${params.eventId}"/>"><i class=" icon-plus"></i> <i class="icon-user"></i> <s:message
                        code="button.addNewUser"/></a>
                <a class="btn" href="<s:url value="/admin/event/user-group/edit?eventId=${params.eventId}"/>"><i class="icon-plus"></i> <s:message
                        code="button.addNewUserGroup"/></a>

                <c:if test="${!empty loggedUser.organizationId}">
                    <a class="btn"
                       href="<s:url value="/admin/import?eventId=${params.eventId}&organizationId=${loggedUser.organizationId}"/>"><i class=" icon-folder-open"></i> <s:message
                            code="button.import"/></a>
                </c:if>


            </c:when>
        </c:choose>

    </div>

    <div class="searchFormPanel" id="searchFormPanel" style="display: none;">
        <sf:form action="" modelAttribute="params" method="get">
            <tags:hiddenEvent/>
            <label><s:message code="label.group"/>:</label>
                <sf:select path="groupId">
                    <sf:option value=""/>
                    <sf:options items="${userGroups}" itemLabel="name" itemValue="id"/>
                </sf:select>
            <label><s:message code="label.name"/>:</label> <sf:input path="userFulltext"/>

            <p><button type="submit" class="btn"><s:message code="button.filterList"/></button></p>
        </sf:form>
    </div>


    <c:choose>
        <c:when test="${!empty userInEvents}">
            <sf:form action="${root}/admin/event/user/list" method="post" modelAttribute="selectionForm">
                <div class="eventListTable">
                    <tags:hiddenEvent/>

                    <table class="table table-striped">
                        <thead>
                        <tr>
                            <th></th>
                            <tags:eventUserList renderHeader="true"/>
                            <th><s:message code="label.action"/></th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${userInEvents}" var="userInEvent" begin="0" step="1" varStatus="i">
                            <tr>
                                <td><input type="checkbox" name="selectedUsers[${i.index}]"
                                           value="${userInEvent.userId}"/></td>
                                <tags:eventUserList renderHeader="false" user="${userInEvent}"/>
                                <td>
                                    <a class="btn"
                                       href="<s:url value="${root}/admin/event/user/edit?eventId=${params.eventId}&userId=${userInEvent.userId}"/>"><span class=" icon-pencil"></span> <s:message
                                            code="button.edit"/></a>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>

                <div class="bottomTableControl">
                    <sf:select id="inputSelectedAction" path="selectedAction"
                               onchange="$('#selectedTaskIdSelect').toggle(this.value=='REGISTER_TO_TASK')">
                        <sf:option value="" disabled="disabled"><s:message code="option.selectAction"/></sf:option>
                        <c:forEach items="${selectedUserActions}" var="action">
                            <sf:option value="${action}"><s:message code="SelectedUserAction.${action}"/></sf:option>
                        </c:forEach>
                    </sf:select>


                        <%-- Suspend reason modal --%>
                    <tags:modal id="suspendReasonModal" titleCode="modalTitle.writeSuspendReason">
                        <p><sf:textarea path="suspendReason" cols="74" rows="9" cssStyle="width:90%"/></p>
                    </tags:modal>

                        <%-- Add to user group modal --%>
                    <tags:modal id="addToUserGroupModal" titleCode="modalTitle.addToUserGroup">
                        <p><sf:select path="groupId" items="${userGroups}" itemLabel="name" itemValue="id"/></p>
                    </tags:modal>

                    <button type="submit" class="btn" onclick="return onDataSubmit()"><span class="icon-ok"></span> <s:message
                            code="button.submit"/></button>
                </div>
            </sf:form>

        </c:when>
        <c:otherwise>
            <p><s:message code="msg.noUsersFound"/></p>
        </c:otherwise>

    </c:choose>
</div>
