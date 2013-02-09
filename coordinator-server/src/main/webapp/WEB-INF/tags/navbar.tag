<%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="app" uri="/WEB-INF/www.tld" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
        taglib prefix="sf" uri="http://www.springframework.org/tags/form" %><%@
    attribute name="inverse" type="java.lang.Boolean"
%>
<div class="navbar navbar<c:out value="${inverse ? '-inverse' : ''}"/> navbar-fixed-top">
    <div class="navbar-inner">
        <div class="container-fluid">
            <jsp:doBody/>
        </div>
    </div>
</div>

