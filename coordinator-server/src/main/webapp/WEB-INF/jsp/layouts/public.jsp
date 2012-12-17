<%@ page contentType="text/html; charset=UTF-8" language="java" %><%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
        taglib prefix="sf" uri="http://www.springframework.org/tags/form" %><%@
        taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %><%@
        taglib prefix="tags" tagdir="/WEB-INF/tags"
%><!DOCTYPE html>
<html lang="en">
<%--@elvariable id="loggedUser" type="cz.clovekvtisni.coordinator.server.domain.UserEntity"--%>
<%--@elvariable id="root" type="java.lang.String"--%>
    <head>
        <meta http-equiv="content-type" content="text/html; charset=utf-8" />
        <title>Coordinator</title>
    </head>
    <body class="<tiles:getAsString name="extraClass"/>">
            <div id="page-header"></div>
            <div id="page-content">
                <tiles:insertAttribute name="content" />
            </div>
    </body>
</html>
