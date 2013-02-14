<%@ page contentType="text/html; charset=UTF-8" language="java" %><%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
        taglib prefix="sf" uri="http://www.springframework.org/tags/form" %><%@
        taglib prefix="can" uri="/WEB-INF/permissions.tld" %><%@
        taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %><%@
        taglib prefix="tags" tagdir="/WEB-INF/tags"
        %><!DOCTYPE html>
<html lang="en">
<%--@elvariable id="loggedUser" type="cz.clovekvtisni.coordinator.server.domain.UserEntity"--%>
<%--@elvariable id="root" type="java.lang.String"--%>

    <jsp:include page="inc_head.jsp"/>

    <body class="<tiles:getAsString name="extraClass"/>">

    <tags:navbar inverse="true">
        <a href="${root}/admin" class="brand"><s:message code="application.name"/></a>
    </tags:navbar>

    <div class="container">

        <!-- Docs nav
        ================================================== -->
        <div class="row">
            <div class="span3 bs-docs-sidebar">
                <s:message code="application.public.info"/>


                License
                The project “Map Icons Collection” was created by Nicolas Mollet under the Creative Commons Attribution-Share Alike 3.0 Unported license (CC BY SA 3.0).

                This license lets you remix, tweak, and build upon our work even for commercial reasons, as long as you credit the project and license your new creations under the identical terms.

                Please credit: Maps Icons Collection http://mapicons.nicolasmollet.com

            </div>
            <div class="span6">
                <div id="page-content">
                    <tiles:insertAttribute name="content" />
                </div>
            </div>
            <div class="span3">
                <s:message code="application.public.info2"/>
            </div>
        </div>
    </body>
</html>
