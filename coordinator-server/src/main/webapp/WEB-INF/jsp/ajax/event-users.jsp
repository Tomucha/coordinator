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

    <c:choose>
        <c:when test="${!empty userInEvents}">
                <div class="eventListTable">
                    <tags:hiddenEvent/>
                    <table class="table table-striped">
                        <thead>
                        <tr>
                            <tags:eventUserList renderHeader="true"/>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${userInEvents}" var="userInEvent" begin="0" step="1" varStatus="i">
                            <!-- This javascript function should be defined on page which includes picker -->
                            <tr onclick="onUserClick(${userInEvent.userId})" class="clickable">
                                <tags:eventUserList renderHeader="false" user="${userInEvent}"/>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
        </c:when>
        <c:otherwise>
            <p><s:message code="msg.noUsersFound"/></p>
        </c:otherwise>

    </c:choose>
