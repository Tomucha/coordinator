<%@
        attribute name="longitude" required="true" type="java.lang.Double" %><%@
        attribute name="latitude" required="true" type="java.lang.Double" %><%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
        taglib prefix="sf" uri="http://www.springframework.org/tags/form"
%><c:out value="${longitude}"/>N, <c:out value="${latitude}"/>E