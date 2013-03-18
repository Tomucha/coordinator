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
        taglib prefix="tags" tagdir="/WEB-INF/tags"
        %>

<%--@elvariable id="config" type="cz.clovekvtisni.coordinator.server.domain.CoordinatorConfig"--%>

<h2><s:message code="header.poiList"/></h2>

<div class="mainPanel">

    <div class="buttonPanel">
        <c:choose>
            <c:when test="${can:hasRole('BACKEND')}">
                <button accesskey="f" class="btn" onclick="\$('#searchFormPanel').show();"><i class="icon-filter"></i> <s:message code="button.filterList"/> <span class="caret"></span></button>
                <a class="btn" href="<s:url value="/admin/event/poi/edit?eventId=${params.eventId}"/>"><span class="icon-plus"></span> <span class="icon-map-marker"></span> <s:message
                        code="button.addNew"/></a>
            </c:when>
        </c:choose>
    </div>

    <div class="searchFormPanel" id="searchFormPanel" style="display: none;">
        <sf:form action="" modelAttribute="params" method="get">
            <tags:hiddenEvent/>
            <label><s:message code="label.workflow"/>:</label>
            <sf:select path="workflowId">
                <sf:option value=""/>
                <sf:options items="${config.workflowList}" itemLabel="name" itemValue="id"/>
            </sf:select>
            <label><s:message code="label.workflowState"/>:</label>
            <sf:select path="workflowStateId">
                <sf:option value=""/>
                <c:forEach items="${config.workflowList}" var="workflow">
                    <c:forEach items="${workflow.states}" var="state">
                        <sf:option value="${state.id}">${workflow.name} &gt; ${state.name}</sf:option>
                    </c:forEach>
                </c:forEach>
            </sf:select>

            <p><button type="submit" class="btn"><s:message code="button.filterList"/></button></p>
        </sf:form>
    </div>

    <c:choose>
        <c:when test="${!empty poiList}">
            <sf:form action="" method="post" modelAttribute="selectionForm">
                <div class="dataList poiListTable">
                    <tags:hiddenEvent/>

                    <table class="table table-striped">
                        <thead>
                        <tr>
                            <th></th>
                            <th><s:message code="PoiEntity.name"/></th>
                            <th><s:message code="PoiEntity.poiCategory"/></th>
                            <th><s:message code="label.locality"/></th>
                            <th><s:message code="PoiEntity.userCount"/></th>
                            <th><s:message code="PoiEntity.workflow"/></th>
                            <th><s:message code="PoiEntity.workflowState"/></th>
                            <th><s:message code="label.action"/></th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${poiList}" var="poi" varStatus="i">
                            <tr>
                                <td><input type="checkbox" name="selectedPois[${i.index}]" value="${poi.id}"/>

                                </td>
                                <td><img src="${root}${poi.poiCategory.icon}" class="pull-left"/>
                                    <b><c:out value="${poi.name}"/></b>
                                    <tags:poiStatusIcon poi="${poi}"/>
                                    <br/>
                                    <small><c:out value="${poi.description}"/></small>
                                </td>
                                <td>
                                    <c:out value="${poi.poiCategory.name}"/>
                                    <br/>
                                    <small><c:out value="${poi.poiCategory.description}"/></small>
                                </td>
                                <td><tags:gps longitude="${poi.longitude}" latitude="${poi.latitude}"/></td>
                                <td><c:out value="${poi.userCount}"/></td>
                                <td><c:out value="${poi.workflow.name}"/><br/>
                                    <small><c:out value="${poi.workflow.description}"/></small>
                                </td>
                                <td>
                                    <c:if test="${!empty poi.workflowId}">
                                        <a href="${root}/admin/event/poi/workflow?poiId=<c:out value='${poi.id}'/>&eventId=${event.id}">
                                           <c:out value="${poi.workflowState.name}"/><br/>
                                           <small><c:out value="${poi.workflowState.description}"/></small>
                                        </a>
                                    </c:if>
                                </td>
                                <td>
                                    <a class="btn"
                                       href="<s:url value="${root}/admin/event/poi/edit?eventId=${poi.eventId}&poiId=${poi.id}"/>"><span class=" icon-pencil"></span> <s:message
                                            code="button.edit"/></a>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>

                    <div class="bottomTableControl">
                        <sf:select path="selectedAction">
                            <sf:option value="" disabled="disabled"><s:message code="option.selectAction"/></sf:option>
                            <c:forEach items="${selectedPoiActions}" var="action">
                                <sf:option value="${action}"><s:message code="SelectedPoiAction.${action}"/></sf:option>
                            </c:forEach>
                        </sf:select>

                        <sf:button class="btn"><span class="icon-ok"></span> <s:message code="button.submit"/></sf:button>
                    </div>
                </div>
            </sf:form>


        </c:when>
        <c:otherwise>
            <p><s:message code="msg.noPoisFound"/></p>
        </c:otherwise>

    </c:choose>
</div>
