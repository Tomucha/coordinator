<%@
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
    taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
    taglib prefix="sf" uri="http://www.springframework.org/tags/form" %><%@
    taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %><%@
    taglib prefix="can" uri="/WEB-INF/permissions.tld" %><%@
    taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %><%@
    taglib prefix="tags" tagdir="/WEB-INF/tags"
%><script type="text/javascript">
    function onDataSubmit() {
        if ($("#inputSelectedAction").val() == "SUSPEND") {
            $('#suspendReasonModal').modal({});
            return false;

        } else
            return true;
    }
</script>
<h2><s:message code="header.userList"/></h2>

<div class="mainPanel">
    <div class="buttonPanel">
        <c:choose>
            <c:when test="${can:hasRole('BACKEND')}">
                <a class="btn" href="<s:url value="/admin/event/user/edit?eventId=${params.eventId}"/>"><s:message code="button.addNew"/></a>
                <c:if test="${!empty loggedUser.organizationId}">
                    <a class="btn" href="<s:url value="/admin/import?eventId=${params.eventId}&organizationId=${loggedUser.organizationId}"/>"><s:message code="button.import"/></a>
                </c:if>
            </c:when>
        </c:choose>
    </div>

    <sf:form action="" modelAttribute="params" method="get">
        <div class="searchFormPanel">
            <sf:hidden path="eventId"/>
            <label><s:message code="label.name"/>: <sf:input path="userFulltext"/></label>
            <button type="submit" class="btn"><s:message code="button.filterList"/></button>
        </div>
    </sf:form>

    <c:choose>
        <c:when test="${!empty userInEvents}">
            <sf:form action="${root}/admin/event/user/list" method="post" modelAttribute="selectionForm">
                <div class="eventListTable">
                    <sf:hidden path="eventId"/>

                    <table class="table table-striped">
                        <thead>
                        <tr>
                            <th></th>
                            <th><s:message code="label.name"/></th>
                            <th><s:message code="label.phone"/></th>
                            <th><s:message code="label.status"/></th>
                            <th><s:message code="label.address"/></th>
                            <th><s:message code="label.action"/></th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${userInEvents}" var="userInEvent" begin="0" step="1" varStatus="i">
                            <tr>
                                <td><input type="checkbox" name="selectedUsers[${i.index}]" value="${userInEvent.userId}"/></td>
                                <th><c:out value="${userInEvent.userEntity.fullName}"/></th>
                                <td><c:out value="${userInEvent.userEntity.phone}"/></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${userInEvent.userEntity.suspended}">
                                            <span class="alert alert-error" title="<c:out value="${userInEvent.userEntity.reasonSuspended}"/>"><s:message code="label.suspended"/></span>
                                        </c:when>
                                        <c:when test="${!empty userInEvent.status}">
                                            <s:message code="RegistrationStatus.${userInEvent.status}"/>
                                        </c:when>
                                    </c:choose>
                                </td>
                                <td><c:out value="${userInEvent.userEntity.fullAddress}"/></td>
                                <td>
                                    <a class="btn" href="<s:url value="${root}/admin/event/user/edit?eventId=${params.eventId}&userId=${userInEvent.userId}"/>"><s:message code="button.detail"/></a>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>

                <div>
                    <sf:select id="inputSelectedAction" path="selectedAction" onchange="$('#selectedTaskIdSelect').toggle(this.value=='REGISTER_TO_TASK')">
                        <sf:option value=""/>
                        <c:forEach items="${selectedUserActions}" var="action">
                            <sf:option value="${action}"><s:message code="SelectedUserAction.${action}"/></sf:option>
                        </c:forEach>
                    </sf:select>

                    <c:choose>
                        <c:when test="${empty tasks or fn:length(tasks) == 0}">
                            <span id="selectedTaskIdSelect" style="display: none"><s:message code="msg.noTasksFound"/></span>
                        </c:when>
                        <c:otherwise>
                            <sf:select path="selectedTaskId" id="selectedTaskIdSelect" cssStyle="display: none">
                                <c:forEach items="${tasks}" var="task">
                                    <sf:option value="${task.id}"><c:out value="${task.poiCategory.name}"/> - <c:out value="${task.createdDate}"/></sf:option>
                                </c:forEach>
                            </sf:select>
                        </c:otherwise>
                    </c:choose>

                    <%-- Suspend reason modal --%>
                    <div id="suspendReasonModal" class="modal hide fade">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                            <h3><s:message code="modalTitle.writeSuspendReason"/></h3>
                        </div>
                        <div class="modal-body">
                            <p><sf:textarea path="suspendReason" cols="74" rows="9" cssStyle="width:90%"/></p>
                        </div>
                        <div class="modal-footer">
                            <a href="#" class="btn" data-dismiss="modal" aria-hidden="true">Close</a>
                            <button type="submit" class="btn btn-primary">Save changes</button>
                        </div>
                    </div>

                    <button type="submit" class="btn" onclick="return onDataSubmit()"><s:message code="button.submit"/></button>
                </div>
            </sf:form>

        </c:when>
        <c:otherwise>
            <p><s:message code="msg.noUsersFound"/></p>
        </c:otherwise>

    </c:choose>
</div>
