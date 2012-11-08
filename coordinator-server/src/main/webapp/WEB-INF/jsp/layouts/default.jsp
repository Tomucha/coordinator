<%@ page contentType="text/html; charset=UTF-8" language="java" %><%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
        taglib prefix="sf" uri="http://www.springframework.org/tags/form" %><%@
        taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %><%@
        taglib prefix="tags" tagdir="/WEB-INF/tags"
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="cs" lang="cs">
<%--@elvariable id="loggedUser" type="cz.clovekvtisni.coordinator.server.domain.UserEntity"--%>
<%--@elvariable id="root" type="java.lang.String"--%>
    <head>
        <meta http-equiv="content-type" content="text/html; charset=utf-8" />
        <title>Coordinator</title>
    </head>
    <body class="<tiles:getAsString name="extraClass"/>">
            <div id="page-header">
                <c:if test="${!empty loggedUser}">
                    <c:out value="${loggedUser.email}" /> <a href="<s:url value="/logout"/>">(<s:message code="logout"/>)</a>
                </c:if>
            </div>
            <div id="page-content">

                <div id="page-content-left">
                    <h2><s:message code="title.activityFeed"/></h2>
                    <div class="activityList">
                        <table>
                            <tr>
                                <td>foo bar</td>
                                <td>5 min</td>
                            </tr>
                        </table>
                    </div>
                </div>

                <div id="page-content-main">
                    <div class="tabPanel">
                        <a href="<s:url value="/admin/event"/>"><s:message code="tab.eventList"/></a>
                        <!--<a href=""><s:message code="tab.userList"/></a>-->
                    </div>
                    <div class="tabContent">
                        <tiles:insertAttribute name="content" />
                    </div>
                </div>

            </div>
    </body>
</html>
